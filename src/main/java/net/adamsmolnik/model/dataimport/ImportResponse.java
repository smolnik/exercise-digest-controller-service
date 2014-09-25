package net.adamsmolnik.model.dataimport;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class ImportResponse extends ServiceMessage {

    public String importedObjectKey;

    public String version;

    public ImportResponse() {

    }

    public ImportResponse(String importedObjectKey, String version) {
        this.importedObjectKey = importedObjectKey;
        this.version = version;
    }

    @Override
    public String toString() {
        return "ImportResponse [importedObjectKey=" + importedObjectKey + ", version=" + version + ", id=" + id + "]";
    }

}
