package ru.otus.processor;

import org.junit.jupiter.api.Test;
import ru.otus.exceptions.InEvenSecondException;
import ru.otus.model.Message;
import ru.otus.provider.TimeProvider;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProcessorThrowExceptionInEvenSecondTest {

    @Test
    void shouldThrowExceptionWhenSecondIsEven() {
        // Arrange
        TimeProvider timeProvider = mock(TimeProvider.class);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(
                2023, 10, 24, 22, 30, 2
        )); // Even second

        var processor = new ProcessorThrowExceptionInEvenSecond(timeProvider);

        var message = new Message.Builder(1L).build();

        // Act & Assert
        assertThrows(InEvenSecondException.class, () ->
                processor.process(message), "Exception in even second");
    }

    @Test
    void shouldNotThrowExceptionWhenSecondIsOdd() {
        // Arrange
       TimeProvider timeProvider = mock(TimeProvider.class);

       when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(
               2023, 10, 24, 22, 30, 3
       ));

       var processor = new ProcessorThrowExceptionInEvenSecond(timeProvider);

        var message = new Message.Builder(1L).build();

        // Act
        var result = processor.process(message);

        // Assert
        assertEquals(message, result); // Ensure that the message remains unchanged
    }
}