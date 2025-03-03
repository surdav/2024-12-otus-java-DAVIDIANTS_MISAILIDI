package ru.otus.processor;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProcessorSwapFieldsTest {

    private final Processor processor = new ProcessorSwapFields();

    @Test
    void processorSwapFieldsTest() {
        // Arrange
        String field11 = "field11";
        String field12 = "field12";

        Message message = new Message.Builder(1L)
                .field11(field11)
                .field12(field12)
                .build();

        // Act
        Message swappedMessage = processor.process(message);

        // Assert
        assertEquals(field12, swappedMessage.getField11());

        assertEquals(field11, swappedMessage.getField12());

        assertEquals(1L, swappedMessage.getId());

        assertNull(swappedMessage.getField1());
    }
}