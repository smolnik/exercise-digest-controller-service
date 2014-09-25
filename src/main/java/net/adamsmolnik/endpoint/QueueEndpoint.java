package net.adamsmolnik.endpoint;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ASmolnik
 *
 */
public interface QueueEndpoint {

    void handleString(Function<String, String> inputMessageProcessor, String queueIn, Optional<String> queueOut);

    void handleVoid(Consumer<String> inputMessageProcessor, String queueIn);

    <T, R> void handleJson(Function<T, R> inputMessageProcessor, Class<T> inputMessageClass, String queueIn, String queueOut);

    <T, R> void handle(Function<T, R> inputMessageProcessorProcessor, Function<String, T> inputMessageProcessorMapper, String queueIn,
            Optional<String> queueOut);

    void shutdown();

}