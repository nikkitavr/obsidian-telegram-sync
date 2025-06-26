package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;
import ru.nikkitavr.notesassistant.model.personalassistant.message.TelegramChat;

public class TemplateRendererTest {
    
    @Test
    void testRenderLiteralTokens() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        
        List<Token> tokens = List.of(
            new LiteralToken("Hello "),
            new LiteralToken("World")
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("Hello World", result);
    }
    
    @Test
    void testRenderGenericUnit() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        TelegramChat chat = new TelegramChat();
        chat.setId(123L);
        chat.setTitle("Test Chat");
        msg.setChat(chat);
        
        List<Token> tokens = List.of(
            new LiteralToken("Chat: "),
            new TemplateUnitToken(GenericUnit.CHAT, Map.of())
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("Chat: Test Chat", result);
    }
    
    @Test
    void testRenderGenericUnitWithParams() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        msg.setText("Hello World");
        
        List<Token> tokens = List.of(
            new LiteralToken("First 5 chars: "),
            new TemplateUnitToken(GenericUnit.CONTENT_N, Map.of("0", "5"))
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("First 5 chars: Hello", result);
    }
    
    @Test
    void testRenderNoteUnit() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        msg.setText("Note content");
        
        List<Token> tokens = List.of(
            new LiteralToken("Content: "),
            new TemplateUnitToken(NoteUnit.CONTENT_TEXT, Map.of())
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("Content: Note content", result);
    }
    
    @Test
    void testRenderReplaceUnit() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        msg.setText("foo bar");
        
        List<Token> tokens = List.of(
            new TemplateUnitToken(NoteUnit.CONTENT_TEXT, Map.of()),
            new LiteralToken(" "),
            new TemplateUnitToken(new ReplaceUnit(), Map.of("from", "foo", "to", "baz"))
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("baz bar ", result);
    }
    
    @Test
    void testRenderMixedTokens() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        TelegramChat chat = new TelegramChat();
        chat.setId(123L);
        chat.setTitle("Test Chat");
        msg.setChat(chat);
        msg.setText("Hello");
        
        List<Token> tokens = List.of(
            new LiteralToken("Chat: "),
            new TemplateUnitToken(GenericUnit.CHAT, Map.of()),
            new LiteralToken(", Message: "),
            new TemplateUnitToken(NoteUnit.CONTENT_TEXT, Map.of())
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("Chat: Test Chat, Message: Hello", result);
    }
    
    @Test
    void testRenderEmptyTokens() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        
        List<Token> tokens = List.of();
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("", result);
    }
    
    @Test
    void testRenderNullMessage() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        
        List<Token> tokens = List.of(
            new TemplateUnitToken(GenericUnit.CHAT, Map.of())
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("", result);
    }
    
    @Test
    void testRenderMultipleReplaceUnits() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        msg.setText("foo bar baz");
        
        List<Token> tokens = List.of(
            new TemplateUnitToken(NoteUnit.CONTENT_TEXT, Map.of()),
            new TemplateUnitToken(new ReplaceUnit(), Map.of("from", "foo", "to", "qux")),
            new TemplateUnitToken(new ReplaceUnit(), Map.of("from", "baz", "to", "quux"))
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("qux bar quux", result);
    }
    
    @Test
    void testRenderComplexTemplate() {
        TemplateRenderer renderer = new TemplateRenderer();
        Message msg = new Message();
        TelegramChat chat = new TelegramChat();
        chat.setId(123L);
        chat.setTitle("Test Chat");
        msg.setChat(chat);
        msg.setMessageId(456);
        msg.setText("Hello World");
        msg.setDate(Instant.parse("2024-06-01T12:34:56Z"));
        
        List<Token> tokens = List.of(
            new LiteralToken("Chat: "),
            new TemplateUnitToken(GenericUnit.CHAT, Map.of()),
            new LiteralToken(", ID: "),
            new TemplateUnitToken(GenericUnit.CHAT_ID, Map.of()),
            new LiteralToken(", Message: "),
            new TemplateUnitToken(GenericUnit.CONTENT_N, Map.of("0", "5")),
            new LiteralToken(" "),
            new TemplateUnitToken(new ReplaceUnit(), Map.of("from", "Hello", "to", "Hi"))
        );
        
        String result = renderer.render(tokens, msg, "");
        assertEquals("Chat: Test Chat, ID: 123, Message: Hi ", result);
    }
} 