package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.processor.*;

import java.time.LocalDateTime;
import java.util.List;

public class HomeWork {

    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {

        // Step 1: Prepare processors
        var processors = List.of(
                new ProcessorConcatFields(),
                new ProcessorUpperField10(),
                new ProcessorSwapFields(),
                new ProcessorThrowExceptionInEvenSecond(LocalDateTime::now)
        );

        // Step 2: Error handling
        var complexProcessor = new ComplexProcessor(
                processors,
                ex -> logger.error("Error occurred during message processing:", ex)
        );

        // Step 3: Connect Listener for history tracking
        var historyListener = new HistoryListener();
        complexProcessor.addListener(historyListener);

        // Step 4: Prepare the message for processing
        var message = new Message.Builder(1L)
                .field1("Hello")
                .field2("World")
                .field3("2024")
                .field10("original_field10")
                .field11("Value11")
                .field12("Value12")
                .build();

        logger.info("Initial message: {}", message);

        // Step 5: Process the message through ComplexProcessor
        try {
            var result = complexProcessor.handle(message);

            logger.info("Processed message: {}", result);
        } catch (Exception e) {
            logger.error("Exception during processing:", e);
        }

        // Step 6: Read message history
        historyListener.findMessageById(1L)
                .ifPresentOrElse(
                        savedMessage -> logger.info("Message from history: {}", savedMessage),
                        () -> logger.warn("No message found in history")
                );

        // Step 7: Remove Listener
        complexProcessor.removeListener(historyListener);


    }
}
