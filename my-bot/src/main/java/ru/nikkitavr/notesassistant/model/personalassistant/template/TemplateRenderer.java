package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.List;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public class TemplateRenderer {
    private final ReplaceUnit replaceUnit = new ReplaceUnit();

    public String render(List<Token> tokens, Message message, String template) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            if (t instanceof LiteralToken l) {
                sb.append(l.text());
            } else if (t instanceof TemplateUnitToken u) {
                if (u.unit() instanceof ReplaceUnit) continue; // не рендерим, только пост-обработка
                sb.append(u.unit().apply(message, u.params()));
            }
        }
        String result = sb.toString();
        
        // Применяем все replace-юниты
        for (Token t : tokens) {
            if (t instanceof TemplateUnitToken u && u.unit() instanceof ReplaceUnit) {
                String from = u.params().get("from");
                String to = u.params().get("to");
                if (from != null && to != null) {
                    result = result.replace(from, to);
                }
            }
        }
        
        return result;
    }
    
    private String getUnitKey(TemplateUnit unit) {
        if (unit instanceof GenericUnit gu) {
            return gu.name().toLowerCase().replace("_", "");
        } else if (unit instanceof NoteUnit nu) {
            return nu.name().toLowerCase().replace("_", ":");
        }
        return "";
    }
} 