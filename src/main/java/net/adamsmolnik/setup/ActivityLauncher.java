package net.adamsmolnik.setup;

/**
 * @author ASmolnik
 *
 */
public interface ActivityLauncher {

    void register(Object activity);

    void launch();

    void shutdown();

}