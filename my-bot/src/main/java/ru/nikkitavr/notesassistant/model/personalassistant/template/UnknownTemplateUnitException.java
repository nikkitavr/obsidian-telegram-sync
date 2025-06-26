package ru.nikkitavr.notesassistant.model.personalassistant.template;

public class UnknownTemplateUnitException extends RuntimeException {
    public UnknownTemplateUnitException(String expr) {
        super("Unknown template unit: " + expr);
    }
} 