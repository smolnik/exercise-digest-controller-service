package net.adamsmolnik.model.detection;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class DetectionResponse extends ServiceMessage {

    public DetectionResponse() {

    }

    public DetectionResponse(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }

    public String type;

    public String subType;

    @Override
    public String toString() {
        return "DetectionResponse [type=" + type + ", subType=" + subType + "]";
    }

}
