package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.time.Instant;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;
import ru.nikkitavr.notesassistant.model.personalassistant.message.TelegramChat;

public class GenericUnitTest {
    
    @Test
    void testChat() {
        Message msg = new Message();
        TelegramChat chat = new TelegramChat();
        chat.setId(123L);
        chat.setTitle("Test Chat");
        msg.setChat(chat);
        
        assertEquals("Test Chat", GenericUnit.CHAT.apply(msg, Map.of()));
    }
    
    @Test
    void testChatId() {
        Message msg = new Message();
        TelegramChat chat = new TelegramChat();
        chat.setId(456L);
        msg.setChat(chat);
        
        assertEquals("456", GenericUnit.CHAT_ID.apply(msg, Map.of()));
    }
    
    @Test
    void testMessageId() {
        Message msg = new Message();
        msg.setMessageId(789);
        
        assertEquals("789", GenericUnit.MESSAGE_ID.apply(msg, Map.of()));
    }
    
    @Test
    void testContentN() {
        Message msg = new Message();
        msg.setText("Hello World");
        
        // Без параметров - весь текст
        assertEquals("Hello World", GenericUnit.CONTENT_N.apply(msg, Map.of()));
        
        // С параметром - первые 5 символов
        assertEquals("Hello", GenericUnit.CONTENT_N.apply(msg, Map.of("0", "5")));
        
        // С параметром больше длины текста
        assertEquals("Hello World", GenericUnit.CONTENT_N.apply(msg, Map.of("0", "20")));
    }
    
    @Test
    void testMessageDate() {
        Message msg = new Message();
        msg.setDate(Instant.parse("2024-06-01T12:34:56Z"));
        
        // Без параметра - формат по умолчанию
        assertEquals("20240601", GenericUnit.MESSAGE_DATE.apply(msg, Map.of()));
        
        // С кастомным форматом
        assertEquals("2024-06-01", GenericUnit.MESSAGE_DATE.apply(msg, Map.of("format", "yyyy-MM-dd")));
    }
    
    @Test
    void testNullValues() {
        Message msg = new Message();
        
        assertEquals("", GenericUnit.CHAT.apply(msg, Map.of()));
        assertEquals("", GenericUnit.CHAT_ID.apply(msg, Map.of()));
        assertEquals("", GenericUnit.MESSAGE_ID.apply(msg, Map.of()));
        assertEquals("", GenericUnit.CONTENT_N.apply(msg, Map.of()));
        assertEquals("", GenericUnit.MESSAGE_DATE.apply(msg, Map.of()));
    }
    
    @Test
    void testMatch() {
        assertEquals(GenericUnit.CHAT, GenericUnit.match("chat"));
        assertEquals(GenericUnit.CHAT_ID, GenericUnit.match("chatId"));
        assertEquals(GenericUnit.MESSAGE_ID, GenericUnit.match("messageId"));
        assertEquals(GenericUnit.CONTENT_N, GenericUnit.match("content"));
        assertEquals(GenericUnit.MESSAGE_DATE, GenericUnit.match("messageDate"));
        
        // С параметрами
        assertEquals(GenericUnit.CONTENT_N, GenericUnit.match("content:5"));
        assertEquals(GenericUnit.MESSAGE_DATE, GenericUnit.match("messageDate:yyyy-MM-dd"));
        
        // Несуществующие
        assertNull(GenericUnit.match("unknown"));
        assertNull(GenericUnit.match("chatName")); // не должно совпадать с chat
    }
    
    @Test
    void testParseParams() {
        Map<String, String> params = GenericUnit.parseParams("content:10");
        assertEquals("10", params.get("0"));
        
        params = GenericUnit.parseParams("messageDate:yyyy-MM-dd");
        assertEquals("yyyy-MM-dd", params.get("format"));
        
        params = GenericUnit.parseParams("chat");
        assertTrue(params.isEmpty());
    }
    
    @Test
    void testPattern() {
        assertNotNull(GenericUnit.CHAT.pattern());
        assertNotNull(GenericUnit.CHAT_ID.pattern());
        assertNotNull(GenericUnit.MESSAGE_ID.pattern());
        assertNotNull(GenericUnit.CONTENT_N.pattern());
        assertNotNull(GenericUnit.MESSAGE_DATE.pattern());
    }
} 