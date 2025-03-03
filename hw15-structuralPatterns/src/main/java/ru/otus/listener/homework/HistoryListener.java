package ru.otus.listener.homework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

public class HistoryListener implements Listener, HistoryReader {

    // Map for storing messages in history
    private final Map<Long, Message> history = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {

        // Create a deep copy of the message and save it in history
        history.put(msg.getId(), deepCopy(msg));
    }

    @Override
    public Optional<Message> findMessageById(long id) {

        // Return the message from history if it exists
        return Optional.ofNullable(history.get(id));
    }

    private Message deepCopy(Message original) {
        if(original == null) {
            return null;
        }

        var builder = new Message.Builder(original.getId())
                .field1(original.getField1())
                .field2(original.getField2())
                .field3(original.getField3())
                .field4(original.getField4())
                .field5(original.getField5())
                .field6(original.getField6())
                .field7(original.getField7())
                .field8(original.getField8())
                .field9(original.getField9())
                .field10(original.getField10())
                .field11(original.getField11())
                .field12(original.getField12());

        // Consider deep copying of field13
        if(original.getField13() != null) {
            var field13Copy = new ObjectForMessage();

            if(original.getField13().getData() != null) {
                field13Copy.setData(new ArrayList<>(original.getField13().getData()));
            }
            builder.field13(field13Copy);
        }
        return builder.build();
    }
}
