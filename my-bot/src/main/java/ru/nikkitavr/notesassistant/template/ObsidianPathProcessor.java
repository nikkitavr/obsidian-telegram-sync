package ru.nikkitavr.notesassistant.template;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Процессор путей для Obsidian Plugin.
 * Обрабатывает generic variables для создания путей к файлам и папкам.
 */
public class ObsidianPathProcessor {
    
    private final TemplateProcessor templateProcessor;
    private final ObsidianPathContext context;
    
    public ObsidianPathProcessor() {
        this.context = new ObsidianPathContext();
        this.templateProcessor = TemplateProcessor.builder(ObsidianPathContext.class)
                // Generic variables для путей
                .register("content", this::processContent)
                .register("messageDate", this::processMessageDate)
                .register("messageTime", this::processMessageTime)
                .register("date", this::processCurrentDate)
                .register("time", this::processCurrentTime)
                .register("user", this::processUser)
                .register("userId", this::processUserId)
                .register("chat", this::processChat)
                .register("chatId", this::processChatId)
                .register("topic", this::processTopic)
                .register("topicId", this::processTopicId)
                .register("messageId", this::processMessageId)
                .register("replyMessageId", this::processReplyMessageId)
                .register("forwardFrom", this::processForwardFrom)
                .register("creationDate", this::processCreationDate)
                .register("creationTime", this::processCreationTime)
                .register("hashtag", this::processHashtag)
                .build();
    }
    
    /**
     * Обработать шаблон пути.
     * @param template Шаблон пути
     * @param messageData Данные сообщения
     * @return Обработанный путь
     */
    public String processPath(String template, ObsidianTemplateExample.MessageData messageData) {
        context.setMessageData(messageData);
        String result = templateProcessor.process(template, context);
        
        // Постобработка пути
        result = sanitizePath(result);
        result = ensureFileExtension(result);
        
        return result;
    }
    
    // ========== ОБРАБОТЧИКИ ПЕРЕМЕННЫХ ==========
    
    private String processContent(ObsidianPathContext ctx) {
        String content = ctx.getMessageData().getText();
        String arg = ctx.arg("value");
        
        if (arg == null) {
            // {{content}} - первые 100 символов
            return content != null ? content.substring(0, Math.min(content.length(), 100)) : "";
        }
        
        // {{content:XX}} - XX символов
        if (arg.matches("\\d+")) {
            int length = Integer.parseInt(arg);
            return content != null ? content.substring(0, Math.min(content.length(), length)) : "";
        }
        
        // {{content:[X]}} - строка номер X
        Matcher lineMatcher = Pattern.compile("\\[(\\d+)\\]").matcher(arg);
        if (lineMatcher.find()) {
            int lineNum = Integer.parseInt(lineMatcher.group(1));
            String[] lines = content != null ? content.split("\n") : new String[0];
            if (lineNum > 0 && lineNum <= lines.length) {
                return lines[lineNum - 1];
            }
            return "";
        }
        
        return content != null ? content : "";
    }
    
    private String processMessageDate(ObsidianPathContext ctx) {
        LocalDateTime messageDate = ctx.getMessageData().getMessageDate();
        String format = ctx.arg("value");
        if (format == null) format = "YYYYMMDD";
        return formatDateTime(messageDate, format);
    }
    
    private String processMessageTime(ObsidianPathContext ctx) {
        LocalDateTime messageDate = ctx.getMessageData().getMessageDate();
        String format = ctx.arg("value");
        if (format == null) format = "HHmmss";
        return formatDateTime(messageDate, format);
    }
    
    private String processCurrentDate(ObsidianPathContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        String format = ctx.arg("value");
        if (format == null) format = "YYYYMMDD";
        return formatDateTime(now, format);
    }
    
    private String processCurrentTime(ObsidianPathContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        String format = ctx.arg("value");
        if (format == null) format = "HHmmss";
        return formatDateTime(now, format);
    }
    
    private String processUser(ObsidianPathContext ctx) {
        ObsidianTemplateExample.UserData user = ctx.getMessageData().getUser();
        if (user == null) return "";
        
        String arg = ctx.arg("value");
        if (arg == null) {
            // {{user}} - ссылка на пользователя
            return user.getLink();
        }
        
        switch (arg) {
            case "name":
                // {{user:name}} - username
                return user.getUsername();
            case "fullName":
                // {{user:fullName}} - полное имя
                String fullName = user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "");
                return fullName.trim();
            default:
                return user.getLink();
        }
    }
    
    private String processUserId(ObsidianPathContext ctx) {
        ObsidianTemplateExample.UserData user = ctx.getMessageData().getUser();
        return user != null ? String.valueOf(user.getId()) : "";
    }
    
    private String processChat(ObsidianPathContext ctx) {
        ObsidianTemplateExample.ChatData chat = ctx.getMessageData().getChat();
        if (chat == null) return "";
        
        String arg = ctx.arg("value");
        if (arg == null) {
            // {{chat}} - ссылка на чат
            return chat.getLink();
        }
        
        switch (arg) {
            case "name":
                // {{chat:name}} - название чата
                return chat.getName();
            default:
                return chat.getLink();
        }
    }
    
    private String processChatId(ObsidianPathContext ctx) {
        ObsidianTemplateExample.ChatData chat = ctx.getMessageData().getChat();
        return chat != null ? String.valueOf(chat.getId()) : "";
    }
    
    private String processTopic(ObsidianPathContext ctx) {
        ObsidianTemplateExample.TopicData topic = ctx.getMessageData().getTopic();
        if (topic == null) return "";
        
        String arg = ctx.arg("value");
        if (arg == null) {
            // {{topic}} - ссылка на топик
            return topic.getLink();
        }
        
        switch (arg) {
            case "name":
                // {{topic:name}} - название топика
                return topic.getName();
            default:
                return topic.getLink();
        }
    }
    
    private String processTopicId(ObsidianPathContext ctx) {
        ObsidianTemplateExample.TopicData topic = ctx.getMessageData().getTopic();
        return topic != null ? String.valueOf(topic.getId()) : "";
    }
    
    private String processMessageId(ObsidianPathContext ctx) {
        return String.valueOf(ctx.getMessageData().getMessageId());
    }
    
    private String processReplyMessageId(ObsidianPathContext ctx) {
        Integer replyId = ctx.getMessageData().getReplyMessageId();
        return replyId != null ? String.valueOf(replyId) : "";
    }
    
    private String processForwardFrom(ObsidianPathContext ctx) {
        ObsidianTemplateExample.ForwardData forward = ctx.getMessageData().getForwardFrom();
        if (forward == null) return "";
        
        String arg = ctx.arg("value");
        if (arg == null) {
            // {{forwardFrom}} - ссылка на источник пересылки
            return forward.getLink();
        }
        
        switch (arg) {
            case "name":
                // {{forwardFrom:name}} - название источника
                return forward.getName();
            default:
                return forward.getLink();
        }
    }
    
    private String processCreationDate(ObsidianPathContext ctx) {
        LocalDateTime creationDate = ctx.getMessageData().getCreationDate();
        String format = ctx.arg("value");
        if (format == null) format = "YYYYMMDD";
        return formatDateTime(creationDate, format);
    }
    
    private String processCreationTime(ObsidianPathContext ctx) {
        LocalDateTime creationDate = ctx.getMessageData().getCreationDate();
        String format = ctx.arg("value");
        if (format == null) format = "HHmmss";
        return formatDateTime(creationDate, format);
    }
    
    private String processHashtag(ObsidianPathContext ctx) {
        String content = ctx.getMessageData().getText();
        String arg = ctx.arg("value");
        
        if (content == null || arg == null) return "";
        
        Matcher matcher = Pattern.compile("\\[(\\d+)\\]").matcher(arg);
        if (!matcher.find()) return "";
        
        int hashtagIndex = Integer.parseInt(matcher.group(1));
        Pattern hashtagPattern = Pattern.compile("#(\\w+)");
        Matcher hashtagMatcher = hashtagPattern.matcher(content);
        
        int currentIndex = 0;
        while (hashtagMatcher.find()) {
            currentIndex++;
            if (currentIndex == hashtagIndex) {
                return hashtagMatcher.group(1); // возвращаем хештег без #
            }
        }
        
        return "";
    }
    
    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========
    
    private String formatDateTime(LocalDateTime dateTime, String format) {
        // Простая реализация форматирования даты
        // В реальности можно использовать более сложную логику
        if (format.contains("YYYY")) {
            format = format.replace("YYYY", String.valueOf(dateTime.getYear()));
        }
        if (format.contains("MM")) {
            format = format.replace("MM", String.format("%02d", dateTime.getMonthValue()));
        }
        if (format.contains("DD")) {
            format = format.replace("DD", String.format("%02d", dateTime.getDayOfMonth()));
        }
        if (format.contains("HH")) {
            format = format.replace("HH", String.format("%02d", dateTime.getHour()));
        }
        if (format.contains("mm")) {
            format = format.replace("mm", String.format("%02d", dateTime.getMinute()));
        }
        if (format.contains("ss")) {
            format = format.replace("ss", String.format("%02d", dateTime.getSecond()));
        }
        if (format.contains("SSS")) {
            format = format.replace("SSS", String.format("%03d", dateTime.getNano() / 1_000_000));
        }
        return format;
    }
    
    private String sanitizePath(String path) {
        // Удаляем недопустимые символы для путей
        return path.replaceAll("[\\\\/:*?\"<>|\\n\\r]", "_");
    }
    
    private String ensureFileExtension(String path) {
        // Добавляем .md если нет расширения
        if (!path.contains(".")) {
            return path + ".md";
        }
        return path;
    }
    
    // ========== ВНУТРЕННИЕ КЛАССЫ ==========
    
    /**
     * Контекст для обработки путей Obsidian.
     */
    public static class ObsidianPathContext extends TemplateUnitContext {
        private ObsidianTemplateExample.MessageData messageData;
        
        public void setMessageData(ObsidianTemplateExample.MessageData messageData) {
            this.messageData = messageData;
        }
        
        public ObsidianTemplateExample.MessageData getMessageData() {
            return messageData;
        }
    }
} 