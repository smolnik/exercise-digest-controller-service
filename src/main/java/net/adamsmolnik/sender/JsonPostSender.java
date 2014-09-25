package net.adamsmolnik.sender;

import java.util.concurrent.TimeUnit;
import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import net.adamsmolnik.newinstance.SenderException;

/**
 * @author ASmolnik
 *
 */
@Dependent
public class JsonPostSender<T, R> implements Sender<T, R> {

    @Override
    public R trySending(String serviceUrl, T request, Class<R> responseClass, SendingParams params) {
        int attemptCounter = 0;
        Exception exception = null;
        while (attemptCounter < params.getNumberOfAttempts()) {
            ++attemptCounter;
            try {
                return send(serviceUrl, request, responseClass);
            } catch (Exception ex) {
                if (attemptCounter == params.getNumberOfAttempts()) {
                    throw new SenderException(ex);
                } else {
                    String message = "Attempt (" + attemptCounter + ") to send failed for url " + serviceUrl + " and request " + request
                            + " with reason: " + ex.getLocalizedMessage();
                    params.getLogExceptiomAttemptConsumer().accept(message);
                }
                exception = ex;
            }
            try {
                TimeUnit.SECONDS.sleep(params.getAttemptIntervalSecs());
            } catch (InterruptedException e) {
                // deliberately ignored
            }
        }
        throw new SenderException(exception);
    }

    @Override
    public R send(String serviceUrl, T request, Class<R> responseClass) {
        Client client = ClientBuilder.newClient().property("jersey.config.client.connectTimeout", 5000)
                .property("jersey.config.client.readTimeout", 5 * 60 * 1000);
        Entity<T> requestEntity = Entity.json(request);
        Response response = client.target(serviceUrl).request().post(requestEntity);
        return response.readEntity(responseClass);
    }

}
