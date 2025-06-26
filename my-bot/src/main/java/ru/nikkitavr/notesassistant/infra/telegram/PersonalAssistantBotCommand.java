package ru.nikkitavr.notesassistant.infra.telegram;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;

@Getter
public enum PersonalAssistantBotCommand {
  SET_TOPIC_TITLE("/set_topic_title");

  private final String value;

  PersonalAssistantBotCommand(String value) {
    this.value = value;
  }

  public static PersonalAssistantBotCommand fromValue(String value) {
    return Arrays.stream(PersonalAssistantBotCommand.values())
        .filter(cd -> cd.getValue().equals(value))
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }
}
