package net.adamsmolnik.model.digest;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class DigestResponse extends ServiceMessage {

    public DigestResponse() {

    }

    public DigestResponse(String digest) {
        this.digest = digest;
    }

    public String digest;

    @Override
    public String toString() {
        return "DigestResponse [digest=" + digest + ", id=" + id + "]";
    }

}
