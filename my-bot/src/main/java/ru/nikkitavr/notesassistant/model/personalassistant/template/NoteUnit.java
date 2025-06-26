package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public enum NoteUnit implements TemplateUnit {
    CONTENT("content", (m, a) -> {
        // Полный контент: forwarded from + file | image + message text
        StringBuilder content = new StringBuilder();
        
        // Добавляем forwarded from если есть
        if (m.getForwardFrom() != null) {
            content.append("Forwarded from: ").append(m.getForwardFrom().getFirstName());
            if (m.getForwardFrom().getLastName() != null) {
                content.append(" ").append(m.getForwardFrom().getLastName());
            }
            content.append("\n\n");
        }
        
        // Добавляем информацию о файлах/медиа
        if (m.getPhoto() != null) {
            content.append("📷 Image\n\n");
        }
        if (m.getVideo() != null) {
            content.append("🎥 Video\n\n");
        }
        if (m.getDocument() != null) {
            content.append("📄 Document: ").append(m.getDocument().getFileName()).append("\n\n");
        }
        if (m.getAudio() != null) {
            content.append("🎵 Audio\n\n");
        }
        if (m.getVoice() != null) {
            content.append("🎤 Voice message\n\n");
        }
        
        // Добавляем текст сообщения
        if (m.getText() != null && !m.getText().isEmpty()) {
            content.append(m.getText());
        }
        
        return content.toString();
    }),
    
    CONTENT_TEXT("content:text", (m, a) -> {
        // Только текст сообщения
        return m.getText() != null ? m.getText() : "";
    }),
    
    CONTENT_LINES("content:[X-Y]", (m, a) -> {
        // Строки с X по Y включительно
        String text = m.getText() != null ? m.getText() : "";
        if (text.isEmpty()) return "";
        
        String[] lines = text.split("\n", -1); // -1 чтобы сохранить пустые строки в конце
        
        int startLine = 1; // По умолчанию с первой строки
        int endLine = lines.length; // По умолчанию до последней строки
        
        try {
            String range = a.get("range");
            if (range != null && range.matches("\\d+-\\d+")) {
                String[] parts = range.split("-");
                startLine = Integer.parseInt(parts[0]);
                endLine = Integer.parseInt(parts[1]);
            }
        } catch (Exception ignored) {}
        
        // Корректируем границы
        startLine = Math.max(1, Math.min(startLine, lines.length));
        endLine = Math.max(startLine, Math.min(endLine, lines.length));
        
        StringBuilder result = new StringBuilder();
        for (int i = startLine - 1; i < endLine; i++) {
            if (i > startLine - 1) result.append("\n");
            result.append(lines[i]);
        }
        
        return result.toString();
    });

    private final String key;
    private final BiFunction<Message, Map<String, String>, String> fn;
    private final Pattern regex;

    NoteUnit(String key, BiFunction<Message, Map<String, String>, String> fn) {
        this.key = key;
        this.fn = fn;
        // Для content:[X-Y] нужен специальный regex
        if (key.equals("content:[X-Y]")) {
            this.regex = Pattern.compile("\\{\\{content:\\[(\\d+)-(\\d+)\\]}}", Pattern.CASE_INSENSITIVE);
        } else {
            this.regex = Pattern.compile("\\{\\{" + key + "}}", Pattern.CASE_INSENSITIVE);
        }
    }

    @Override
    public Pattern pattern() { return regex; }

    @Override
    public String apply(Message m, Map<String, String> args) { return fn.apply(m, args); }

    public static NoteUnit match(String expr) {
        // Сначала проверяем content:[X-Y]
        if (expr.matches("content:\\[\\d+-\\d+\\]")) {
            return CONTENT_LINES;
        }
        
        // Затем проверяем остальные
        for (NoteUnit unit : values()) {
            if (expr.equals(unit.key)) return unit;
        }
        return null;
    }
    
    public static Map<String, String> parseParams(String expr) {
        Map<String, String> params = new java.util.HashMap<>();
        
        if (expr.matches("content:\\[\\d+-\\d+\\]")) {
            // Извлекаем диапазон из content:[X-Y]
            String range = expr.substring(expr.indexOf('[') + 1, expr.indexOf(']'));
            params.put("range", range);
        }
        
        return params;
    }
} 