package net.adamsmolnik.fallback;

import java.util.Optional;
import net.adamsmolnik.newinstance.SetupParamsView;

/**
 * @author ASmolnik
 *
 */
public interface FallbackSetupParamsView extends SetupParamsView {

    Optional<String> getLoadBalancerName();

    Optional<String> getDnsName();

    boolean waitForOOMAlarm();

}