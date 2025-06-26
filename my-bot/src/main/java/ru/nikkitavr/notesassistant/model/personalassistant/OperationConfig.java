package ru.nikkitavr.notesassistant.model.personalassistant;

public interface OperationConfig <T extends Operation> {

  T getOperation();
}
