package net.adamsmolnik.setup.aws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.adamsmolnik.exceptions.ServiceException;
import net.adamsmolnik.setup.ActivityLauncher;
import net.adamsmolnik.util.Configuration;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;

/**
 * @author ASmolnik
 *
 */
@Singleton
public class SwfActivityLauncher implements ActivityLauncher {

    private final List<Object> activites = new ArrayList<Object>();

    private ActivityWorker aw;

    @Inject
    private Configuration conf;

    @PostConstruct
    private void init() {
        Map<String, String> confMap = conf.getServiceConfMap();
        ClientConfiguration config = new ClientConfiguration().withSocketTimeout(Integer.valueOf(confMap.get("swf.socketTimeout")));
        AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(config);
        service.setEndpoint(confMap.get("swf.endpoint"));
        String domain = confMap.get("swf.domain");
        String taskListToPoll = confMap.get("swf.tasks");
        aw = new ActivityWorker(service, domain, taskListToPoll);
    }

    @Override
    public void register(Object activity) {
        activites.add(activity);

    }

    @Override
    public void launch() {
        try {
            for (Object activity : activites) {
                aw.addActivitiesImplementation(activity);
            }
            aw.start();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void shutdown() {
        aw.shutdownNow();
    }

}
