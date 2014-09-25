package net.adamsmolnik.sender;

import java.util.function.Consumer;

/**
 * @author ASmolnik
 *
 */
public class SendingParams {

    private int numberOfAttempts = 1;

    private int attemptIntervalSecs = 0;

    private Consumer<String> logExceptiomAttemptConsumer = message -> {
    };

    public SendingParams withNumberOfAttempts(int value) {
        numberOfAttempts = value;
        return this;
    }

    public SendingParams withAttemptIntervalSecs(int value) {
        attemptIntervalSecs = value;
        return this;
    }

    public SendingParams withLogExceptiomAttemptConsumer(Consumer<String> value) {
        logExceptiomAttemptConsumer = value;
        return this;
    }

    public int getAttemptIntervalSecs() {
        return attemptIntervalSecs;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public Consumer<String> getLogExceptiomAttemptConsumer() {
        return logExceptiomAttemptConsumer;
    }

}
