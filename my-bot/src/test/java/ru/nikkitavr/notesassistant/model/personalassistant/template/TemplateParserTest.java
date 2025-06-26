package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TemplateParserTest {
    
    @Test
    void testParseLiteralOnly() {
        TemplateParser parser = new TemplateParser();
        String template = "Hello World";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof LiteralToken);
        assertEquals("Hello World", ((LiteralToken) tokens.get(0)).text());
    }
    
    @Test
    void testParseGenericUnit() {
        TemplateParser parser = new TemplateParser();
        String template = "{{chat}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof TemplateUnitToken);
        TemplateUnitToken token = (TemplateUnitToken) tokens.get(0);
        assertEquals(GenericUnit.CHAT, token.unit());
    }
    
    @Test
    void testParseGenericUnitWithParams() {
        TemplateParser parser = new TemplateParser();
        String template = "{{content:5}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof TemplateUnitToken);
        TemplateUnitToken token = (TemplateUnitToken) tokens.get(0);
        assertEquals(GenericUnit.CONTENT_N, token.unit());
        assertEquals("5", token.params().get("0"));
    }
    
    @Test
    void testParseNoteUnit() {
        TemplateParser parser = new TemplateParser();
        String template = "{{content:text}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.NOTE_BODY);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof TemplateUnitToken);
        TemplateUnitToken token = (TemplateUnitToken) tokens.get(0);
        assertEquals(NoteUnit.CONTENT_TEXT, token.unit());
    }
    
    @Test
    void testParseNoteUnitContent() {
        TemplateParser parser = new TemplateParser();
        String template = "{{content}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.NOTE_BODY);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof TemplateUnitToken);
        TemplateUnitToken token = (TemplateUnitToken) tokens.get(0);
        assertEquals(NoteUnit.CONTENT, token.unit());
    }
    
    @Test
    void testParseNoteUnitContentLines() {
        TemplateParser parser = new TemplateParser();
        String template = "{{content:[2-5]}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.NOTE_BODY);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof TemplateUnitToken);
        TemplateUnitToken token = (TemplateUnitToken) tokens.get(0);
        assertEquals(NoteUnit.CONTENT_LINES, token.unit());
        assertEquals("2-5", token.params().get("range"));
    }
    
    @Test
    void testParseReplaceUnit() {
        TemplateParser parser = new TemplateParser();
        String template = "{{replace:foo=>bar}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof TemplateUnitToken);
        TemplateUnitToken token = (TemplateUnitToken) tokens.get(0);
        assertTrue(token.unit() instanceof ReplaceUnit);
        assertEquals("foo", token.params().get("from"));
        assertEquals("bar", token.params().get("to"));
    }
    
    @Test
    void testParseMixedTemplate() {
        TemplateParser parser = new TemplateParser();
        String template = "Hello {{chat}}, your message: {{content:10}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(4, tokens.size());
        assertTrue(tokens.get(0) instanceof LiteralToken);
        assertTrue(tokens.get(1) instanceof TemplateUnitToken);
        assertTrue(tokens.get(2) instanceof LiteralToken);
        assertTrue(tokens.get(3) instanceof TemplateUnitToken);
    }
    
    @Test
    void testParseUnknownUnit() {
        TemplateParser parser = new TemplateParser();
        String template = "{{unknown}}";
        
        assertThrows(UnknownTemplateUnitException.class, () -> 
            parser.parse(template, TemplateUnitScope.GENERIC));
    }
    
    @Test
    void testParseUnclosedBrace() {
        TemplateParser parser = new TemplateParser();
        String template = "{{chat";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0) instanceof LiteralToken);
        assertEquals("{{chat", ((LiteralToken) tokens.get(0)).text());
    }
    
    @Test
    void testParseEmptyTemplate() {
        TemplateParser parser = new TemplateParser();
        String template = "";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(0, tokens.size());
    }
    
    @Test
    void testParseComplexTemplate() {
        TemplateParser parser = new TemplateParser();
        String template = "{{chat}}-{{chatId}}-{{messageId}}-{{content:5}}-{{messageDate:yyyy-MM-dd}} {{replace:foo=>baz}}";
        
        List<Token> tokens = parser.parse(template, TemplateUnitScope.GENERIC);
        
        assertEquals(11, tokens.size()); // 5 юнитов + 6 литералов (разделители)
        
        // Проверяем, что есть токены нужных типов
        boolean hasChatToken = false;
        boolean hasReplaceToken = false;
        
        for (Token token : tokens) {
            if (token instanceof TemplateUnitToken) {
                TemplateUnitToken unitToken = (TemplateUnitToken) token;
                if (unitToken.unit() == GenericUnit.CHAT) {
                    hasChatToken = true;
                }
                if (unitToken.unit() instanceof ReplaceUnit) {
                    hasReplaceToken = true;
                    ReplaceUnit replaceUnit = (ReplaceUnit) unitToken.unit();
                    assertEquals("foo", unitToken.params().get("from"));
                    assertEquals("baz", unitToken.params().get("to"));
                }
            }
        }
        
        assertTrue(hasChatToken, "Should contain chat token");
        assertTrue(hasReplaceToken, "Should contain replace token");
    }
} 