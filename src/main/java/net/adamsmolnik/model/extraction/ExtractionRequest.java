package net.adamsmolnik.model.extraction;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class ExtractionRequest extends ServiceMessage {

    public String objectKey;

    public String type;

    public ExtractionRequest() {

    }

    public ExtractionRequest(String objectKey, String type) {
        this.objectKey = objectKey;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ExtractionRequest [objectKey=" + objectKey + ", type=" + type + "]";
    }

}
