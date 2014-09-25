package net.adamsmolnik.newinstance;

/**
 * @author ASmolnik
 *
 */
public class SenderException extends RuntimeException {

    private static final long serialVersionUID = -2448733395746650433L;

    public SenderException(Throwable t) {
        super(t);
    }

}
