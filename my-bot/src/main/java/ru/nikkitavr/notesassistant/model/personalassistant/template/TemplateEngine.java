package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.List;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public class TemplateEngine {
    private final TemplateParser parser = new TemplateParser();
    private final TemplateRenderer renderer = new TemplateRenderer();

    public String renderBody(String template, Message message) {
        List<Token> tokens = parser.parse(template, TemplateUnitScope.NOTE_BODY);
        return renderer.render(tokens, message, template);
    }

    public String renderFilePath(String template, Message message) {
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        return renderer.render(tokens, message, template);
    }
} 