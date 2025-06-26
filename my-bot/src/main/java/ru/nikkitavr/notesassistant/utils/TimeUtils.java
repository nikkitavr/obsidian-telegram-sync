package ru.nikkitavr.notesassistant.utils;

import java.time.Instant;

public class TimeUtils {
  public static Instant toInstant(Integer unixTime) {
    if (unixTime == null) {
      return null;
    }
    return Instant.ofEpochSecond(unixTime);
  }
}
