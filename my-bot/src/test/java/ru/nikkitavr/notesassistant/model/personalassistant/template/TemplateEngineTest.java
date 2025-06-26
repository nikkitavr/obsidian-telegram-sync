package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;
import ru.nikkitavr.notesassistant.model.personalassistant.message.TelegramChat;

public class TemplateEngineTest {
    @Test
    void testGenericUnits() {
        Message msg = new Message();
        TelegramChat chat = new TelegramChat();
        chat.setId(123L);
        chat.setTitle("Test Chat");
        msg.setChat(chat);
        msg.setMessageId(42);
        msg.setText("Hello World!");
        msg.setDate(Instant.parse("2024-06-01T12:34:56Z"));

        TemplateEngine engine = new TemplateEngine();
        String tpl = "{{chat}}-{{chatId}}-{{messageId}}-{{content:5}}-{{messageDate:yyyy-MM-dd}}";
        String result = engine.renderFilePath(tpl, msg);
        assertTrue(result.contains("Test Chat-123-42-Hello-2024-06-01"));
    }

    @Test
    void testNoteUnit() {
        Message msg = new Message();
        msg.setText("Note body text");
        TemplateEngine engine = new TemplateEngine();
        String tpl = "{{content:text}}";
        String result = engine.renderBody(tpl, msg);
        assertEquals("Note body text", result);
    }

    @Test
    void testReplaceUnit() {
        Message msg = new Message();
        msg.setText("foo bar");
        TemplateEngine engine = new TemplateEngine();
        String tpl = "{{content:text}} {{replace:foo=>baz}}";
        String result = engine.renderBody(tpl, msg);
        assertEquals("baz bar ", result);
    }

    @Test
    void testUnknownUnitThrows() {
        Message msg = new Message();
        TemplateEngine engine = new TemplateEngine();
        String tpl = "{{unknown}}";
        assertThrows(UnknownTemplateUnitException.class, () -> engine.renderFilePath(tpl, msg));
    }

    @Test
    void testNoteUnits() {
        TemplateEngine engine = new TemplateEngine();
        Message msg = new Message();
        msg.setText("Hello World");
        
        // Тест content:text
        String result = engine.renderBody("{{content:text}}", msg);
        assertEquals("Hello World", result);
        
        // Тест content (полный)
        result = engine.renderBody("{{content}}", msg);
        assertEquals("Hello World", result);
        
        // Тест content:[X-Y]
        msg.setText("Line 1\nLine 2\nLine 3");
        result = engine.renderBody("{{content:[2-3]}}", msg);
        assertEquals("Line 2\nLine 3", result);
    }
    
    @Test
    void testNoteUnitsWithMedia() {
        TemplateEngine engine = new TemplateEngine();
        Message msg = new Message();
        msg.setText("Check this");
        
        // Добавляем фото
        msg.setPhoto(new ru.nikkitavr.notesassistant.model.personalassistant.message.files.Photo());
        
        String result = engine.renderBody("{{content}}", msg);
        assertTrue(result.contains("📷 Image"));
        assertTrue(result.contains("Check this"));
        
        // content:text должен игнорировать медиа
        result = engine.renderBody("{{content:text}}", msg);
        assertEquals("Check this", result);
    }
} 