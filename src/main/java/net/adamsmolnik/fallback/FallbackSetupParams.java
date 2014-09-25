package net.adamsmolnik.fallback;

import java.util.Optional;
import javax.validation.constraints.NotNull;
import net.adamsmolnik.newinstance.SetupParams;

/**
 * @author ASmolnik
 *
 */
public class FallbackSetupParams extends SetupParams implements FallbackSetupParamsView {

    private Optional<String> lbName = Optional.empty();

    private Optional<String> dnsName = Optional.empty();

    private boolean waitForOOMAlarm;

    public FallbackSetupParams withLabel(String value) {
        super.withLabel(value);
        return this;
    }

    public FallbackSetupParams withInstanceType(String value) {
        super.withInstanceType(value);
        return this;
    }

    public FallbackSetupParams withImageId(String value) {
        super.withImageId(value);
        return this;
    }

    public FallbackSetupParams withServiceContext(String value) {
        super.withServiceContext(value);
        return this;
    }

    @NotNull
    public FallbackSetupParams withLoadBalancerAndDnsNames(String lbName, String dnsName) {
        this.lbName = Optional.of(lbName);
        this.dnsName = Optional.of(dnsName);
        return this;
    }

    public FallbackSetupParams withWaitForOOMAlarm(boolean value) {
        waitForOOMAlarm = value;
        return this;
    }

    @Override
    public Optional<String> getLoadBalancerName() {
        return lbName;
    }

    @Override
    public Optional<String> getDnsName() {
        return dnsName;
    }

    @Override
    public boolean waitForOOMAlarm() {
        return waitForOOMAlarm;
    }

}
