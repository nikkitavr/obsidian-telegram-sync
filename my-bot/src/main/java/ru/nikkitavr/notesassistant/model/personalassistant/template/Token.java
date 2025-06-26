package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.Map;

public sealed interface Token permits LiteralToken, TemplateUnitToken {}

record LiteralToken(String text) implements Token {}

record TemplateUnitToken(TemplateUnit unit, Map<String, String> params) implements Token {} 