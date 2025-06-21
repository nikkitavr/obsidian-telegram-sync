package ru.nikkitavr.tfs.model.personalassistant.filter;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import org.apache.commons.lang3.function.TriFunction;
import ru.nikkitavr.tfs.model.personalassistant.Message;

public enum ConditionType {
  ALL("all", ((message, operation, value) -> {
    return true;
  })),

  USER("user", ((message, operation, value) -> {
    return true;
  })),

  CHAT("chat", ((message, operation, value) -> {
    return true;
  })),

  TOPIC("topic", ((message, operation, value) -> {
    return true;
  })),

  FORWARD_FROM("forwardFrom", ((message, operation, value) -> {
    return true;
  })),

  CONTENT("content", ((message, operation, value) -> {
    return true;
  })),

  VOICE_TRANSCRIPT("voiceTranscript", ((message, operation, value) -> {
    return true;
  }));

  @Getter
  private final String name;
  private final TriFunction<Message, ConditionOperation, String, Boolean> matcher;


  ConditionType(String name, TriFunction<Message, ConditionOperation, String, Boolean> matcher) {
    this.name = name;
    this.matcher = matcher;
  }

  public static ConditionType from(String name) {
    return Arrays.stream(ConditionType.values())
        .filter(cd -> cd.getName().equals(name))
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }

  public Boolean match(Message message, ConditionOperation operation, String value) {
    return matcher.apply(message, operation, value);
  }
}
