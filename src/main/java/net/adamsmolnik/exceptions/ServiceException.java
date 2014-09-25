package net.adamsmolnik.exceptions;

/**
 * @author ASmolnik
 *
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 7421403673567769386L;

    public ServiceException(Throwable throwable) {
        super(throwable);
    }

    public ServiceException(String message) {
        super(message);
    }

}
