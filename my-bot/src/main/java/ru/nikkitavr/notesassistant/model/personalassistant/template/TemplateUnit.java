package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.Map;
import java.util.regex.Pattern;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public interface TemplateUnit {
    Pattern pattern();
    String apply(Message message, Map<String, String> params);
} 