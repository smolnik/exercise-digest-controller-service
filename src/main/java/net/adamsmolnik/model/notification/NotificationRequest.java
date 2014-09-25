package net.adamsmolnik.model.notification;

import net.adamsmolnik.model.ServiceMessage;

/**
 * @author ASmolnik
 *
 */
public class NotificationRequest extends ServiceMessage {

    public NotificationRequest() {

    }

    public NotificationRequest(String message) {
        this.message = message;
    }

    public String message;

    @Override
    public String toString() {
        return "NotificationRequest [message=" + message + "]";
    }

}
