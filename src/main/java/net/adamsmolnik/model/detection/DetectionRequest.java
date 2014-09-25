package net.adamsmolnik.model.detection;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class DetectionRequest extends ServiceMessage {

    public DetectionRequest() {

    }

    public DetectionRequest(String objectKey) {
        this.objectKey = objectKey;
    }

    public String objectKey;

    @Override
    public String toString() {
        return "DetectionRequest [objectKey=" + objectKey + "]";
    }

}
