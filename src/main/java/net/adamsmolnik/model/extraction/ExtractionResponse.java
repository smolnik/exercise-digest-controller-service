package net.adamsmolnik.model.extraction;

import java.util.List;
import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class ExtractionResponse extends ServiceMessage {

    public ExtractionStatus status;

    public String extractionInfoObjectKey;

    public List<String> objectKeys;

    public ExtractionResponse() {

    }

    public ExtractionResponse(ExtractionStatus status) {
        this.status = status;
    }

    public ExtractionResponse(ExtractionStatus status, String extractionInfoObjectKey, List<String> objectKeys) {
        this.status = status;
        this.extractionInfoObjectKey = extractionInfoObjectKey;
        this.objectKeys = objectKeys;
    }

    @Override
    public String toString() {
        return "ExtractionResponse [status=" + status + ", extractionInfoObjectKey=" + extractionInfoObjectKey + ", objectKeys=" + objectKeys + "]";
    }

}
