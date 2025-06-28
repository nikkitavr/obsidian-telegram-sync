package ru.nikkitavr.templation;

@FunctionalInterface
public interface TemplateUnitProcessor<C extends TemplateContext> {
  /** вернуть строку-замену для подстановки в исходный текст */
  String apply(C ctx);
}

