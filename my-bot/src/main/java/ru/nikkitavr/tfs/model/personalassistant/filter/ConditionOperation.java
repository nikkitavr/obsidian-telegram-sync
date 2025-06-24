package ru.nikkitavr.tfs.model.personalassistant.filter;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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

  public boolean anyLeftMatch(List<String> lefts, String right) {
    return switch (this) {
      case EQUAL -> lefts.stream().filter(Objects::nonNull).anyMatch(l -> l.equals(right));
      case CONTAIN -> lefts.stream().filter(Objects::nonNull).anyMatch(l -> l.contains(right));
    };
  }

  public boolean match(String left, String right) {
    return switch (this) {
      case EQUAL -> left.equals(right);
      case CONTAIN -> left.contains(right);
    };
  }
}
