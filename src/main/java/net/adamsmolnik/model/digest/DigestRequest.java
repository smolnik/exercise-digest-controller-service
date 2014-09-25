package net.adamsmolnik.model.digest;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class DigestRequest extends ServiceMessage {

    public DigestRequest() {

    }

    public DigestRequest(String algorithm, String objectKey) {
        this.algorithm = algorithm;
        this.objectKey = objectKey;
    }

    public String algorithm;

    public String objectKey;

}
