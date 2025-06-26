package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public enum GenericUnit implements TemplateUnit {
    CHAT("chat", (m, a) -> m.getChat() != null ? m.getChat().getTitle() : ""),
    CHAT_ID("chatId", (m, a) -> m.getChat() != null ? String.valueOf(m.getChat().getId()) : ""),
    MESSAGE_ID("messageId", (m, a) -> m.getMessageId() != null ? String.valueOf(m.getMessageId()) : ""),
    CONTENT_N("content", (m, a) -> {
        String text = m.getText() != null ? m.getText() : "";
        int n = 0;
        try { n = Integer.parseInt(a.getOrDefault("0", "0")); } catch (Exception ignored) {}
        return n > 0 && n <= text.length() ? text.substring(0, n) : text;
    }),
    MESSAGE_DATE("messageDate", (m, a) -> {
        String format = a.getOrDefault("format", "yyyyMMdd");
        if (m.getDate() == null) return "";
        return m.getDate().atZone(java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(format));
    });

    private final String key;
    private final BiFunction<Message, Map<String, String>, String> fn;
    private final Pattern regex;

    GenericUnit(String key, BiFunction<Message, Map<String, String>, String> fn) {
        this.key = key;
        this.fn = fn;
        this.regex = Pattern.compile("\\{\\{" + key + "(?::([^}]*))?}}", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Pattern pattern() { return regex; }

    @Override
    public String apply(Message m, Map<String, String> args) { return fn.apply(m, args); }

    public static GenericUnit match(String expr) {
        for (GenericUnit unit : values()) {
            if (expr.equals(unit.key) || expr.startsWith(unit.key + ":")) {
                return unit;
            }
        }
        return null;
    }

    public static Map<String, String> parseParams(String expr) {
        Map<String, String> map = new HashMap<>();
        int idx = expr.indexOf(":");
        if (idx > 0 && idx < expr.length() - 1) {
            String param = expr.substring(idx + 1);
            if (expr.startsWith("messageDate")) {
                map.put("format", param);
            } else {
                map.put("0", param);
            }
        }
        return map;
    }
} 