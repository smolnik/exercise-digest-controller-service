package net.adamsmolnik.newinstance.aws;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import net.adamsmolnik.exceptions.ServiceException;
import net.adamsmolnik.newinstance.ServerInstance;
import net.adamsmolnik.newinstance.ServerInstanceBuilder;
import net.adamsmolnik.newinstance.SetupParamsView;
import net.adamsmolnik.util.Log;
import net.adamsmolnik.util.Scheduler;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.InstanceStatusSummary;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

/**
 * @author ASmolnik
 *
 */
public abstract class AwsBaseServerInstanceBuilder<T extends SetupParamsView, R extends ServerInstance> implements ServerInstanceBuilder<T, R> {

    protected class ServerInstanceImpl implements ServerInstance {

        private final String id;

        private final String publicIpAddress;

        private final String privateIpAddress;

        private final AtomicBoolean closeRequested = new AtomicBoolean();

        protected ServerInstanceImpl(Instance newInstance) {
            this.id = newInstance.getInstanceId();
            this.publicIpAddress = newInstance.getPublicIpAddress();
            this.privateIpAddress = newInstance.getPrivateIpAddress();
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getPublicIpAddress() {
            return publicIpAddress;
        }

        @Override
        public String getPrivateIpAddress() {
            return privateIpAddress;
        }

        @Override
        public final void scheduleCleanup(int delay, TimeUnit unit) {
            if (!closeRequested.getAndSet(true)) {
                doScheduleCleanup(delay, unit);
            }
        }

        @Override
        public final void close() {
            if (!closeRequested.getAndSet(true)) {
                doClose();
            }
        }

        protected void doScheduleCleanup(int delay, TimeUnit unit) {
            scheduler.schedule(() -> cleanup(id), delay, unit);
        }

        protected void doClose() {
            if (!closeRequested.getAndSet(true)) {
                scheduler.schedule(() -> cleanup(id), 15, TimeUnit.MINUTES);
            }
        }

    }

    @Inject
    protected Scheduler scheduler;

    @Inject
    protected Log log;

    protected AmazonEC2 ec2;

    @PostConstruct
    private void init() {
        ec2 = new AmazonEC2Client();
    }

    @Override
    public R build(T t) {
        String instanceId = null;
        try {
            instanceId = setupNewInstance(t).getInstanceId();
            waitUntilNewInstanceGetsReady(instanceId, 600);
            Instance newInstanceReady = fetchInstanceDetails(instanceId);
            String newAppUrl = buildAppUrl(newInstanceReady.getPublicIpAddress(), t.getServiceContext());
            sendHealthCheckUntilGetsHealthy(newAppUrl);
            return newInstance(newInstanceReady, t);
        } catch (Exception ex) {
            log.err(ex);
            if (instanceId != null) {
                cleanup(instanceId);
            }
            throw new ServiceException(ex);
        }
    }

    protected abstract R newInstance(Instance newInstance, T t);

    protected void sendHealthCheckUntilGetsHealthy(String newAppUrl) {
        String healthCheckUrl = newAppUrl + "/hc";
        AtomicInteger hcExceptionCounter = new AtomicInteger();
        scheduler.scheduleAndWaitFor(() -> {
            try {
                URL url = new URL(healthCheckUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(2000);
                con.setRequestMethod("GET");
                con.connect();
                int rc = con.getResponseCode();
                log.info("Healthcheck response code of " + rc + " received for " + healthCheckUrl);
                return HttpURLConnection.HTTP_OK == rc ? Optional.of(rc) : Optional.empty();
            } catch (Exception ex) {
                int c = hcExceptionCounter.incrementAndGet();
                log.err("HC attempt (" + c + ") for " + healthCheckUrl + " has failed due to " + ex.getLocalizedMessage());
                log.err(ex);
                if (c > 2) {
                    throw new ServiceException(ex);
                }
                return Optional.empty();
            }
        }, 15, 300, TimeUnit.SECONDS);
    }

    protected Instance fetchInstanceDetails(String instanceId) {
        return ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId)).getReservations().get(0).getInstances().get(0);
    }

    protected Instance setupNewInstance(SetupParamsView spv) {
        RunInstancesRequest request = new RunInstancesRequest()
                .withImageId(spv.getImageId())
                .withInstanceType(spv.getInstanceType())
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("adamsmolnik-net-key-pair")
                .withSecurityGroupIds("sg-7be68f1e")
                .withSecurityGroups("adamsmolnik.com")
                .withIamInstanceProfile(
                        new IamInstanceProfileSpecification()
                                .withArn("arn:aws:iam::542175458111:instance-profile/glassfish4-1-java8-InstanceProfile-1WX67989SDNGL"));
        RunInstancesResult result = ec2.runInstances(request);
        Instance instance = result.getReservation().getInstances().get(0);

        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag().withKey("Name").withValue(spv.getLabel()));
        CreateTagsRequest ctr = new CreateTagsRequest();
        ctr.setTags(tags);
        ctr.withResources(instance.getInstanceId());
        ec2.createTags(ctr);
        return instance;
    }

    protected InstanceStatus waitUntilNewInstanceGetsReady(String instanceId, int timeoutSec) {
        return scheduler.scheduleAndWaitFor(() -> {
            List<InstanceStatus> instanceStatuses = ec2.describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(instanceId))
                    .getInstanceStatuses();
            if (!instanceStatuses.isEmpty()) {
                InstanceStatus is = instanceStatuses.get(0);
                return isReady(is.getInstanceStatus(), is.getSystemStatus()) ? Optional.of(is) : Optional.empty();
            }
            return Optional.empty();
        }, 15, timeoutSec, TimeUnit.SECONDS);
    }

    protected String buildAppUrl(String newInstancePublicIpAddress, String serviceContext) {
        return "http://" + newInstancePublicIpAddress + serviceContext;
    }

    protected void cleanup(String instanceId) {
        ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceId));
    }

    private static boolean isReady(InstanceStatusSummary isSummary, InstanceStatusSummary ssSummary) {
        return "ok".equals(isSummary.getStatus()) && "ok".equals(ssSummary.getStatus());
    }

}
