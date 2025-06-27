package ru.nikkitavr.notesassistant.template;

@FunctionalInterface
public interface TemplateUnitProcessor<C extends TemplateUnitContext> {
  /** вернуть строку-замену для подстановки в исходный текст */
  String apply(C ctx);
}

