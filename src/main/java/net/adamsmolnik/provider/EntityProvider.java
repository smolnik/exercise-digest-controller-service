package net.adamsmolnik.provider;

import java.io.InputStream;
import java.util.Map;
import net.adamsmolnik.entity.Entity;
import net.adamsmolnik.entity.OperationDetails;
import net.adamsmolnik.entity.EntityReference;
import net.adamsmolnik.entity.EntityReferenceDest;
import net.adamsmolnik.entity.EntityReferenceSource;

/**
 * @author ASmolnik
 *
 */
public interface EntityProvider {

    Entity getEntity(EntityReference entityReference);

    OperationDetails copy(EntityReferenceSource ers, EntityReferenceDest erd);

    void persist(EntityReference entityReference, long size, InputStream is);

    void delete(EntityReference entityReference);

    void setNewMetadata(EntityReference er, Map<String, String> metadata);

    Map<String, String> getMetadata(EntityReference entityReference);

}
