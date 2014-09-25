package net.adamsmolnik.sender;

/**
 * @author ASmolnik
 *
 */
public interface Sender<T, R> {

    R trySending(String serviceUrl, T request, Class<R> responseClass, SendingParams params);

    R send(String serviceUrl, T request, Class<R> responseClass);

}