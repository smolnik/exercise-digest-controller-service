package net.adamsmolnik.entity;

/**
 * @author ASmolnik
 *
 */
public class EntityReference {

    private final String entityReferenceKey;

    public EntityReference(String entityReferenceKey) {
        this.entityReferenceKey = entityReferenceKey;
    }

    public String getEntityReferenceKey() {
        return entityReferenceKey;
    }

    @Override
    public String toString() {
        return "EntityReference [entityReferenceKey=" + entityReferenceKey + "]";
    }

}
