package net.adamsmolnik.provider;

public interface NotifierProvider {

    void subscribe(String email);

    void publish(String message);

}
