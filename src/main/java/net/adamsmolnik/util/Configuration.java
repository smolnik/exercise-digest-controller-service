package net.adamsmolnik.util;

import java.util.Map;

/**
 * @author ASmolnik
 *
 */
public interface Configuration {

    String getServiceName();

    String getLocalValue(String key);

    String getGlobalValue(String key);

    String getServiceValue(String key);

    Map<String, String> getServiceConfMap();

}
