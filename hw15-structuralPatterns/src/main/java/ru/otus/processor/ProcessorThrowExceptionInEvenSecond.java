package ru.otus.processor;

import ru.otus.exceptions.InEvenSecondException;
import ru.otus.model.Message;
import ru.otus.provider.TimeProvider;

public class ProcessorThrowExceptionInEvenSecond implements Processor {

    private final TimeProvider timeProvider;

    public ProcessorThrowExceptionInEvenSecond(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public Message process(Message message) {
        int currentSecond = timeProvider.getCurrentTime().getSecond();

        if(currentSecond % 2 == 0) {
            throw new InEvenSecondException("Exception in even second: " + currentSecond);
        }
        return message;
    }
}
