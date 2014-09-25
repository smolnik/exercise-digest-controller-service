package net.adamsmolnik.provider;

import java.io.InputStream;
import net.adamsmolnik.entity.EntityReference;

public interface EntitySaver {

    void save(EntityReference entityReference, long size, InputStream is);

}
