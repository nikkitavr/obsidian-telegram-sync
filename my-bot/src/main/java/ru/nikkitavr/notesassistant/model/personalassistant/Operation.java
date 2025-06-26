package ru.nikkitavr.notesassistant.model.personalassistant;

import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public interface Operation {

  Message processMessage(Message message);
}
