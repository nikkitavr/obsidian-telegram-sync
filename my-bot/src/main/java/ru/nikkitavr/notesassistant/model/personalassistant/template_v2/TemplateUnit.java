package ru.nikkitavr.notesassistant.model.personalassistant.template_v2;

public interface TemplateUnit <R> {
  R process(String templatedSource);
}
