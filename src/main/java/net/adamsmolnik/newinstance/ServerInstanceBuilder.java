package net.adamsmolnik.newinstance;

/**
 * @author ASmolnik
 *
 */
public interface ServerInstanceBuilder<T extends SetupParamsView, R extends ServerInstance> {

    R build(T spv);

}