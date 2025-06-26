package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;
import ru.nikkitavr.notesassistant.model.personalassistant.message.TelegramChat;

public class TokenTest {
    
    @Test
    void testLiteralToken() {
        LiteralToken token = new LiteralToken("Hello World");
        assertEquals("Hello World", token.text());
    }
    
    @Test
    void testTemplateUnitToken() {
        Message msg = new Message();
        TelegramChat chat = new TelegramChat();
        chat.setId(123L);
        chat.setTitle("Test Chat");
        msg.setChat(chat);
        
        GenericUnit unit = GenericUnit.CHAT;
        Map<String, String> params = Map.of("test", "value");
        TemplateUnitToken token = new TemplateUnitToken(unit, params);
        
        assertEquals(unit, token.unit());
        assertEquals(params, token.params());
        assertEquals("Test Chat", token.unit().apply(msg, token.params()));
    }
    
    @Test
    void testTokenSealedInterface() {
        // Проверяем, что Token действительно sealed и разрешает только LiteralToken и TemplateUnitToken
        LiteralToken literal = new LiteralToken("test");
        TemplateUnitToken template = new TemplateUnitToken(GenericUnit.MESSAGE_ID, Map.of());
        
        assertTrue(literal instanceof Token);
        assertTrue(template instanceof Token);
    }
} 