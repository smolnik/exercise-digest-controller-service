package net.adamsmolnik.model.notification;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class SubscriptionRequest extends ServiceMessage {

    public SubscriptionRequest() {

    }

    public SubscriptionRequest(String email) {
        this.email = email;
    }

    public String email;

    @Override
    public String toString() {
        return "SubscriptionRequest [email=" + email + "]";
    }

}
