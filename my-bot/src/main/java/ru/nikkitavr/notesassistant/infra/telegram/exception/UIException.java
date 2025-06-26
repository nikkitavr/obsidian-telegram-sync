package ru.nikkitavr.notesassistant.infra.telegram.exception;


public class UIException extends RuntimeException {

  public UIException(String message) {
    super(message);
  }
}
