package ru.nikkitavr.notesassistant.template;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Пример использования процессоров Obsidian для демонстрации возможностей.
 */
public class ObsidianTemplateExample {
    
    public static void main(String[] args) {
        // Создаем процессоры
        ObsidianPathProcessor pathProcessor = new ObsidianPathProcessor();
        ObsidianNoteProcessor noteProcessor = new ObsidianNoteProcessor();
        
        // Подготавливаем тестовые данные
        MessageData messageData = createTestMessageData();
        NoteMessageData noteMessageData = createTestNoteData();
        
        System.out.println("=== ОБРАБОТКА ПУТЕЙ ===");
        
        // Примеры путей
        String[] pathTemplates = {
            "{{date}}/{{chat:name}}/{{messageId}}.md",
            "{{user:fullName}}/{{messageDate:YYYY-MM-DD}}/{{content:50}}",
            "{{topic:name}}/{{messageTime:HHmm}}/{{hashtag:[1]}}",
            "{{forwardFrom:name}}/{{creationDate}}/{{replyMessageId}}"
        };
        
        for (String template : pathTemplates) {
            String result = pathProcessor.processPath(template, messageData);
            System.out.println("Шаблон: " + template);
            System.out.println("Результат: " + result);
            System.out.println();
        }
        
        System.out.println("=== ОБРАБОТКА СОДЕРЖИМОГО ЗАМЕТОК ===");
        
        // Примеры содержимого заметок
        String[] noteTemplates = {
            "# Сообщение от {{user:fullName}}\n\n{{content:text}}\n\n**Дата:** {{messageDate:YYYY-MM-DD HH:mm}}\n**Чат:** {{chat:name}}",
            
            "## {{content:[1]}}\n\n{{content:text}}\n\n{{files:links}}\n\n{{url1:preview}}\n\n{{voiceTranscript}}",
            
            "**От:** {{user}}\n**В чате:** {{chat}}\n**Топик:** {{topic}}\n\n{{content}}\n\n{{replace:old:new}}",
            
            "### Пересланное сообщение\n\n**Источник:** {{forwardFrom:name}}\n**Оригинальная дата:** {{creationDate:YYYY-MM-DD}}\n\n{{content}}"
        };
        
        for (String template : noteTemplates) {
            String result = noteProcessor.processNoteContent(template, noteMessageData);
            System.out.println("Шаблон: " + template);
            System.out.println("Результат:\n" + result);
            System.out.println("---");
        }
    }
    
    private static MessageData createTestMessageData() {
        MessageData data = new MessageData();
        
        // Основные данные
        data.setText("Привет мир! Это тестовое сообщение с #хештегом1 и #хештегом2");
        data.setMessageDate(LocalDateTime.of(2024, 1, 15, 14, 30, 25));
        data.setCreationDate(LocalDateTime.of(2024, 1, 15, 14, 25, 10));
        data.setMessageId(12345);
        data.setReplyMessageId(12340);
        
        // Пользователь
        UserData user = new UserData();
        user.setId(123456789);
        user.setUsername("testuser");
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setLink("[Иван Петров](https://t.me/testuser)");
        data.setUser(user);
        
        // Чат
        ChatData chat = new ChatData();
        chat.setId(-1001234567890L);
        chat.setName("Мои заметки");
        chat.setLink("[Мои заметки](https://t.me/c/1234567890)");
        data.setChat(chat);
        
        // Топик
        TopicData topic = new TopicData();
        topic.setId(1);
        topic.setName("Общие заметки");
        topic.setLink("[Общие заметки](https://t.me/c/1234567890/1)");
        data.setTopic(topic);
        
        // Пересылка
        ForwardData forward = new ForwardData();
        forward.setName("Канал новостей");
        forward.setLink("[Канал новостей](https://t.me/news_channel)");
        data.setForwardFrom(forward);
        
        return data;
    }
    
    private static NoteMessageData createTestNoteData() {
        NoteMessageData data = new NoteMessageData();
        
        // Основные данные (наследуем от path data)
        MessageData pathData = createTestMessageData();
        data.setText(pathData.getText());
        data.setMessageDate(pathData.getMessageDate());
        data.setCreationDate(pathData.getCreationDate());
        data.setMessageId(pathData.getMessageId());
        data.setReplyMessageId(pathData.getReplyMessageId());
        data.setUser(pathData.getUser());
        data.setChat(pathData.getChat());
        data.setTopic(pathData.getTopic());
        data.setForwardFrom(pathData.getForwardFrom());
        
        // Дополнительные данные для заметки
        data.setFullContent("Переслано из Канал новостей\n\nПривет мир! Это тестовое сообщение с #хештегом1 и #хештегом2\n\nЭто вторая строка сообщения\nИ третья строка");
        data.setVoiceTranscript("Привет мир это тестовое сообщение с хештегом один и хештегом два");
        
        // Файлы
        FileData file1 = new FileData();
        file1.setName("image1");
        file1.setPath("attachments/image1.jpg");
        file1.setType("photo");
        file1.setExtension("jpg");
        
        FileData file2 = new FileData();
        file2.setName("document");
        file2.setPath("attachments/document.pdf");
        file2.setType("document");
        file2.setExtension("pdf");
        
        data.setFiles(Arrays.asList(file1, file2));
        
        // URL
        data.setUrls(Arrays.asList("https://example.com", "https://google.com"));
        
        return data;
    }
    
    // ========== ОБЩИЕ КЛАССЫ ДАННЫХ ==========
    
    public static class MessageData {
        private String text;
        private LocalDateTime messageDate;
        private LocalDateTime creationDate;
        private long messageId;
        private Integer replyMessageId;
        private UserData user;
        private ChatData chat;
        private TopicData topic;
        private ForwardData forwardFrom;
        
        // Геттеры и сеттеры
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public LocalDateTime getMessageDate() { return messageDate; }
        public void setMessageDate(LocalDateTime messageDate) { this.messageDate = messageDate; }
        
        public LocalDateTime getCreationDate() { return creationDate; }
        public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
        
        public long getMessageId() { return messageId; }
        public void setMessageId(long messageId) { this.messageId = messageId; }
        
        public Integer getReplyMessageId() { return replyMessageId; }
        public void setReplyMessageId(Integer replyMessageId) { this.replyMessageId = replyMessageId; }
        
        public UserData getUser() { return user; }
        public void setUser(UserData user) { this.user = user; }
        
        public ChatData getChat() { return chat; }
        public void setChat(ChatData chat) { this.chat = chat; }
        
        public TopicData getTopic() { return topic; }
        public void setTopic(TopicData topic) { this.topic = topic; }
        
        public ForwardData getForwardFrom() { return forwardFrom; }
        public void setForwardFrom(ForwardData forwardFrom) { this.forwardFrom = forwardFrom; }
    }
    
    public static class NoteMessageData extends MessageData {
        private String fullContent; // включая файлы и пересылку
        private String voiceTranscript;
        private List<FileData> files;
        private List<String> urls;
        
        // Геттеры и сеттеры
        public String getFullContent() { return fullContent; }
        public void setFullContent(String fullContent) { this.fullContent = fullContent; }
        
        public String getVoiceTranscript() { return voiceTranscript; }
        public void setVoiceTranscript(String voiceTranscript) { this.voiceTranscript = voiceTranscript; }
        
        public List<FileData> getFiles() { return files; }
        public void setFiles(List<FileData> files) { this.files = files; }
        
        public List<String> getUrls() { return urls; }
        public void setUrls(List<String> urls) { this.urls = urls; }
    }
    
    public static class UserData {
        private long id;
        private String username;
        private String firstName;
        private String lastName;
        private String link;
        
        // Геттеры и сеттеры
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }
    
    public static class ChatData {
        private long id;
        private String name;
        private String link;
        
        // Геттеры и сеттеры
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }
    
    public static class TopicData {
        private long id;
        private String name;
        private String link;
        
        // Геттеры и сеттеры
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }
    
    public static class ForwardData {
        private String name;
        private String link;
        
        // Геттеры и сеттеры
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }
    
    public static class FileData {
        private String name;
        private String path;
        private String type;
        private String extension;
        
        // Геттеры и сеттеры
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getExtension() { return extension; }
        public void setExtension(String extension) { this.extension = extension; }
    }
} 