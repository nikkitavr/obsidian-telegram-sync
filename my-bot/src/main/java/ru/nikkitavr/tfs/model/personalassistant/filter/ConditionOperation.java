package ru.nikkitavr.tfs.model.personalassistant.filter;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;

@Getter
public enum ConditionOperation {
  EQUAL("="),
  CONTAIN("~");

  private final String value;

  ConditionOperation(String value) {
    this.value = value;
  }

  public static ConditionOperation from(String value) {
    return Arrays.stream(ConditionOperation.values())
        .filter(cd -> cd.getValue().equals(value))
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }
}
