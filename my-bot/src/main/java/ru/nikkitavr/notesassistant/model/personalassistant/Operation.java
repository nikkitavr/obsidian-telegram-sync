package ru.nikkitavr.notesassistant.model.personalassistant;

import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public interface Operation <T extends OperationConfig> {

  Message processMessage(Message message, T operationConfig);
}
