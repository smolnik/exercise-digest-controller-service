package net.adamsmolnik.model.dataimport;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class ImportRequest extends ServiceMessage {

    public String srcObjectKey;

    public ImportRequest() {

    }

    public ImportRequest(String srcObjectKey) {
        this.srcObjectKey = srcObjectKey;
    }

}
