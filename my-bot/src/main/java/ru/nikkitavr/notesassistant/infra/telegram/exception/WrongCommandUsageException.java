package ru.nikkitavr.notesassistant.infra.telegram.exception;

public class WrongCommandUsageException extends UIException {
  public WrongCommandUsageException(String message) {
    super(message);
  }
}
