package ru.nikkitavr.notesassistant.template;

import ru.nikkitavr.templation.TemplateEngine;
import ru.nikkitavr.templation.TemplateContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Процессор тела заметки для Obsidian Plugin.
 * Обрабатывает generic variables и note content variables для создания содержимого заметок.
 */
public class ObsidianNoteProcessor {
    
    private final TemplateEngine templateEngine;
    private final ObsidianNoteContext context;
    
    public ObsidianNoteProcessor() {
        this.context = new ObsidianNoteContext();
        this.templateEngine = TemplateEngine.builder(ObsidianNoteContext.class)
                // Generic variables (наследуем от пути)
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
                
                // Note content variables
                .register("voiceTranscript", this::processVoiceTranscript)
                .register("files", this::processFiles)
                .register("url1", this::processUrl1)
                .register("replace", this::processReplace)
                .build();
    }
    
    /**
     * Обработать шаблон тела заметки.
     * @param template Шаблон тела заметки
     * @param messageData Данные сообщения
     * @return Обработанное содержимое заметки
     */
    public String processNoteContent(String template, ObsidianTemplateExample.NoteMessageData messageData) {
        context.setMessageData(messageData);
        context.setReplacements(new ArrayList<>());
        
        String result = templateEngine.process(template, context);
        
        // Применяем замены
        result = applyReplacements(result, context.getReplacements());
        
        return result;
    }
    
    // ========== ОБРАБОТЧИКИ GENERIC VARIABLES ==========
    
    private String processContent(ObsidianNoteContext ctx) {
        String content = ctx.getMessageData().getFullContent();
        String arg = ctx.arg("value");
        
        if (arg == null) {
            // {{content}} - полное содержимое (включая файлы и пересылку)
            return content != null ? content : "";
        }
        
        // {{content:text}} - только текст сообщения
        if ("text".equals(arg)) {
            return ctx.getMessageData().getText() != null ? ctx.getMessageData().getText() : "";
        }
        
        // {{content:XX}} - XX символов
        if (arg.matches("\\d+")) {
            int length = Integer.parseInt(arg);
            return content != null ? content.substring(0, Math.min(content.length(), length)) : "";
        }
        
        // {{content:[X-Y]}} - строки с X по Y
        Matcher rangeMatcher = Pattern.compile("\\[(\\d+)-(\\d*)\\]").matcher(arg);
        if (rangeMatcher.find()) {
            int startLine = Integer.parseInt(rangeMatcher.group(1));
            String endLineStr = rangeMatcher.group(2);
            
            String[] lines = content != null ? content.split("\n") : new String[0];
            if (startLine > 0 && startLine <= lines.length) {
                int endLine = endLineStr.isEmpty() ? lines.length : Integer.parseInt(endLineStr);
                endLine = Math.min(endLine, lines.length);
                
                StringBuilder result = new StringBuilder();
                for (int i = startLine - 1; i < endLine; i++) {
                    if (i > startLine - 1) result.append("\n");
                    result.append(lines[i]);
                }
                return result.toString();
            }
            return "";
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
    
    private String processMessageDate(ObsidianNoteContext ctx) {
        LocalDateTime messageDate = ctx.getMessageData().getMessageDate();
        String format = ctx.arg("value");
        if (format == null) format = "YYYY-MM-DD";
        return formatDateTime(messageDate, format);
    }
    
    private String processMessageTime(ObsidianNoteContext ctx) {
        LocalDateTime messageDate = ctx.getMessageData().getMessageDate();
        String format = ctx.arg("value");
        if (format == null) format = "HH:mm:ss";
        return formatDateTime(messageDate, format);
    }
    
    private String processCurrentDate(ObsidianNoteContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        String format = ctx.arg("value");
        if (format == null) format = "YYYY-MM-DD";
        return formatDateTime(now, format);
    }
    
    private String processCurrentTime(ObsidianNoteContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        String format = ctx.arg("value");
        if (format == null) format = "HH:mm:ss";
        return formatDateTime(now, format);
    }
    
    private String processUser(ObsidianNoteContext ctx) {
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
    
    private String processUserId(ObsidianNoteContext ctx) {
        ObsidianTemplateExample.UserData user = ctx.getMessageData().getUser();
        return user != null ? String.valueOf(user.getId()) : "";
    }
    
    private String processChat(ObsidianNoteContext ctx) {
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
    
    private String processChatId(ObsidianNoteContext ctx) {
        ObsidianTemplateExample.ChatData chat = ctx.getMessageData().getChat();
        return chat != null ? String.valueOf(chat.getId()) : "";
    }
    
    private String processTopic(ObsidianNoteContext ctx) {
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
    
    private String processTopicId(ObsidianNoteContext ctx) {
        ObsidianTemplateExample.TopicData topic = ctx.getMessageData().getTopic();
        return topic != null ? String.valueOf(topic.getId()) : "";
    }
    
    private String processMessageId(ObsidianNoteContext ctx) {
        return String.valueOf(ctx.getMessageData().getMessageId());
    }
    
    private String processReplyMessageId(ObsidianNoteContext ctx) {
        Integer replyId = ctx.getMessageData().getReplyMessageId();
        return replyId != null ? String.valueOf(replyId) : "";
    }
    
    private String processForwardFrom(ObsidianNoteContext ctx) {
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
    
    private String processCreationDate(ObsidianNoteContext ctx) {
        LocalDateTime creationDate = ctx.getMessageData().getCreationDate();
        String format = ctx.arg("value");
        if (format == null) format = "YYYY-MM-DD";
        return formatDateTime(creationDate, format);
    }
    
    private String processCreationTime(ObsidianNoteContext ctx) {
        LocalDateTime creationDate = ctx.getMessageData().getCreationDate();
        String format = ctx.arg("value");
        if (format == null) format = "HH:mm:ss";
        return formatDateTime(creationDate, format);
    }
    
    private String processHashtag(ObsidianNoteContext ctx) {
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
    
    // ========== ОБРАБОТЧИКИ NOTE CONTENT VARIABLES ==========
    
    private String processVoiceTranscript(ObsidianNoteContext ctx) {
        String transcript = ctx.getMessageData().getVoiceTranscript();
        String arg = ctx.arg("value");
        
        if (arg == null) {
            // {{voiceTranscript}} - полная транскрипция
            return transcript != null ? transcript : "";
        }
        
        // {{voiceTranscript:XX}} - XX символов
        if (arg.matches("\\d+")) {
            int length = Integer.parseInt(arg);
            return transcript != null ? transcript.substring(0, Math.min(transcript.length(), length)) : "";
        }
        
        return transcript != null ? transcript : "";
    }
    
    private String processFiles(ObsidianNoteContext ctx) {
        List<ObsidianTemplateExample.FileData> files = ctx.getMessageData().getFiles();
        if (files == null || files.isEmpty()) return "";
        
        String arg = ctx.arg("value");
        if (arg == null) {
            // {{files}} - список файлов с путями
            StringBuilder result = new StringBuilder();
            for (ObsidianTemplateExample.FileData file : files) {
                if (result.length() > 0) result.append("\n");
                result.append(file.getPath());
            }
            return result.toString();
        }
        
        switch (arg) {
            case "links":
                // {{files:links}} - список файлов как markdown ссылки
                StringBuilder result = new StringBuilder();
                for (ObsidianTemplateExample.FileData file : files) {
                    if (result.length() > 0) result.append("\n");
                    result.append("[").append(file.getName()).append("](").append(file.getPath()).append(")");
                }
                return result.toString();
            default:
                // {{files:XX}} - файл номер XX
                try {
                    int index = Integer.parseInt(arg) - 1;
                    if (index >= 0 && index < files.size()) {
                        return files.get(index).getPath();
                    }
                } catch (NumberFormatException e) {
                    // игнорируем
                }
                return "";
        }
    }
    
    private String processUrl1(ObsidianNoteContext ctx) {
        List<String> urls = ctx.getMessageData().getUrls();
        if (urls == null || urls.isEmpty()) return "";
        
        String arg = ctx.arg("value");
        if (arg == null) {
            // {{url1}} - первая ссылка
            return urls.get(0);
        }
        
        switch (arg) {
            case "preview":
                // {{url1:preview}} - iframe превью
                String url = urls.get(0);
                String height = ctx.arg("height");
                if (height == null) height = "250";
                return "<iframe width=\"100%\" height=\"" + height + "\" src=\"" + url + "\"></iframe>";
            default:
                // {{url1:XX}} - ссылка номер XX
                try {
                    int index = Integer.parseInt(arg) - 1;
                    if (index >= 0 && index < urls.size()) {
                        return urls.get(index);
                    }
                } catch (NumberFormatException e) {
                    // игнорируем
                }
                return urls.get(0);
        }
    }
    
    private String processReplace(ObsidianNoteContext ctx) {
        String replaceThis = ctx.arg("value");
        String replaceWith = ctx.arg("with");
        
        if (replaceThis != null) {
            // Добавляем замену в список для последующего применения
            ctx.getReplacements().add(new Replacement(replaceThis, replaceWith != null ? replaceWith : ""));
        }
        
        return ""; // Замена не вставляется в текст, а применяется позже
    }
    
    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========
    
    private String formatDateTime(LocalDateTime dateTime, String format) {
        // Простая реализация форматирования даты
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
    
    private String applyReplacements(String content, List<Replacement> replacements) {
        String result = content;
        for (Replacement replacement : replacements) {
            String from = replacement.getFrom().replace("\\n", "\n");
            String to = replacement.getTo().replace("\\n", "\n");
            result = result.replace(from, to);
        }
        return result;
    }
    
    // ========== ВНУТРЕННИЕ КЛАССЫ ==========
    
    /**
     * Контекст для обработки заметок Obsidian.
     */
    public static class ObsidianNoteContext extends TemplateContext {
        private ObsidianTemplateExample.NoteMessageData messageData;
        private List<Replacement> replacements;
        
        public void setMessageData(ObsidianTemplateExample.NoteMessageData messageData) {
            this.messageData = messageData;
        }
        
        public ObsidianTemplateExample.NoteMessageData getMessageData() {
            return messageData;
        }
        
        public void setReplacements(List<Replacement> replacements) {
            this.replacements = replacements;
        }
        
        public List<Replacement> getReplacements() {
            return replacements;
        }
    }
    
    public static class Replacement {
        private String from;
        private String to;
        
        public Replacement(String from, String to) {
            this.from = from;
            this.to = to;
        }
        
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
    }
} 