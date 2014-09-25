package net.adamsmolnik.fallback.aws;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import net.adamsmolnik.exceptions.ServiceException;
import net.adamsmolnik.fallback.FallbackServerInstance;
import net.adamsmolnik.fallback.FallbackServerInstanceBuilder;
import net.adamsmolnik.fallback.FallbackSetupParamsView;
import net.adamsmolnik.newinstance.aws.AwsBaseServerInstanceBuilder;
import net.adamsmolnik.util.OutOfMemoryAlarm;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;

@Dependent
public class AwsFallbackServerInstanceBuilder extends AwsBaseServerInstanceBuilder<FallbackSetupParamsView, FallbackServerInstance> implements
        FallbackServerInstanceBuilder {

    protected class FallbackServerInstanceImpl extends ServerInstanceImpl implements FallbackServerInstance {

        private final Optional<String> elbName;

        protected FallbackServerInstanceImpl(Instance newInstance, Optional<String> elbName) {
            super(newInstance);
            this.elbName = elbName;
        }

        @Override
        public void doScheduleCleanup(int delay, TimeUnit unit) {
            scheduler.schedule(() -> cleanup(getId(), elbName), delay, unit);
        }

        @Override
        public void doClose() {
            scheduler.schedule(() -> cleanup(getId(), elbName), 15, TimeUnit.MINUTES);
        }

    }

    @Inject
    private OutOfMemoryAlarm oomAlarm;

    private AmazonElasticLoadBalancing elb;

    @PostConstruct
    private void init() {
        elb = new AmazonElasticLoadBalancingClient();
    }

    @Override
    public FallbackServerInstance build(FallbackSetupParamsView spv) {
        String instanceId = null;
        try {
            if (spv.waitForOOMAlarm()) {
                waitUntilOutOfMemoryAlarmReported();
            }
            instanceId = setupNewInstance(spv).getInstanceId();
            waitUntilNewInstanceGetsReady(instanceId, 600);
            Instance newInstanceReady = fetchInstanceDetails(instanceId);
            String ip = newInstanceReady.getPublicIpAddress();
            attachInstanceToElb(instanceId, spv.getLoadBalancerName());
            String newAppUrl = buildAppUrlWithDns(ip, spv);
            sendHealthCheckUntilGetsHealthy(newAppUrl);
            return newInstance(newInstanceReady, spv);
        } catch (Exception ex) {
            log.err(ex);
            if (instanceId != null) {
                cleanup(instanceId, spv.getLoadBalancerName());
            }
            throw new ServiceException(ex);
        }
    }

    private void waitUntilOutOfMemoryAlarmReported() {
        int timeout = 300;
        TimeUnit unit = TimeUnit.SECONDS;
        scheduler.scheduleAndWaitFor(() -> {
            boolean oaReported = oomAlarm.isReported();
            if (oaReported) {
                log.info("OutOfMemoryAlarmReported has just arrived");
                oomAlarm.reset();
                return Optional.of(oaReported);

            }
            return Optional.empty();
        }, 15, timeout, unit);
        log.info("OOM alarm report has arrived");
    }

    private String buildAppUrlWithDns(String newInstancePublicIpAddress, FallbackSetupParamsView spv) {
        String serviceContext = spv.getServiceContext();
        Optional<String> dnsName = spv.getDnsName();
        String serverUrl = dnsName.isPresent() ? dnsName.get() : newInstancePublicIpAddress;
        return "http://" + serverUrl + serviceContext;
    }

    private void attachInstanceToElb(String instanceId, Optional<String> elbName) {
        if (elbName.isPresent()) {
            elb.registerInstancesWithLoadBalancer(new RegisterInstancesWithLoadBalancerRequest().withLoadBalancerName(elbName.get()).withInstances(
                    new com.amazonaws.services.elasticloadbalancing.model.Instance().withInstanceId(instanceId)));
        }
    }

    private void cleanup(String instanceId, Optional<String> elbName) {
        if (elbName.isPresent()) {
            DeregisterInstancesFromLoadBalancerResult result = elb
                    .deregisterInstancesFromLoadBalancer(new DeregisterInstancesFromLoadBalancerRequest().withLoadBalancerName(elbName.get())
                            .withInstances(new com.amazonaws.services.elasticloadbalancing.model.Instance().withInstanceId(instanceId)));
            log.info("Instance " + instanceId + " deregistered from elb " + elbName.get() + " with result " + result);
        }
        ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceId));
    }

    @Override
    protected FallbackServerInstance newInstance(Instance newInstance, FallbackSetupParamsView spv) {
        return new FallbackServerInstanceImpl(newInstance, spv.getLoadBalancerName());
    }

}
