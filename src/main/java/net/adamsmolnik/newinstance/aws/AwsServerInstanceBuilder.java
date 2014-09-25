package net.adamsmolnik.newinstance.aws;

import javax.enterprise.context.Dependent;
import net.adamsmolnik.newinstance.ServerInstance;
import net.adamsmolnik.newinstance.SetupParamsView;
import com.amazonaws.services.ec2.model.Instance;

/**
 * @author ASmolnik
 *
 */
@Dependent
public class AwsServerInstanceBuilder extends AwsBaseServerInstanceBuilder<SetupParamsView, ServerInstance> {

    @Override
    protected ServerInstance newInstance(Instance newInstance, SetupParamsView spv) {
        return new ServerInstanceImpl(newInstance);
    }

}
