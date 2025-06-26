package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;
import ru.nikkitavr.notesassistant.model.personalassistant.message.TelegramUser;
import ru.nikkitavr.notesassistant.model.personalassistant.message.files.Document;
import ru.nikkitavr.notesassistant.model.personalassistant.message.files.Photo;

public class NoteUnitTest {
    
    @Test
    void testContentFull() {
        Message msg = new Message();
        msg.setText("Hello World");
        
        // Тест полного контента без медиа
        assertEquals("Hello World", NoteUnit.CONTENT.apply(msg, Map.of()));
        
        // Тест с forwarded from
        TelegramUser forwardUser = new TelegramUser();
        forwardUser.setFirstName("John");
        forwardUser.setLastName("Doe");
        msg.setForwardFrom(forwardUser);
        
        String result = NoteUnit.CONTENT.apply(msg, Map.of());
        assertTrue(result.contains("Forwarded from: John Doe"));
        assertTrue(result.contains("Hello World"));
    }
    
    @Test
    void testContentWithMedia() {
        Message msg = new Message();
        msg.setText("Check this out");
        
        // Тест с фото
        msg.setPhoto(new Photo());
        String result = NoteUnit.CONTENT.apply(msg, Map.of());
        assertTrue(result.contains("📷 Image"));
        assertTrue(result.contains("Check this out"));
        
        // Тест с документом
        msg.setPhoto(null);
        Document doc = new Document();
        doc.setFileName("test.pdf");
        msg.setDocument(doc);
        result = NoteUnit.CONTENT.apply(msg, Map.of());
        assertTrue(result.contains("📄 Document: test.pdf"));
        assertTrue(result.contains("Check this out"));
    }
    
    @Test
    void testContentText() {
        Message msg = new Message();
        msg.setText("Note body text");
        
        assertEquals("Note body text", NoteUnit.CONTENT_TEXT.apply(msg, Map.of()));
    }
    
    @Test
    void testContentTextNull() {
        Message msg = new Message();
        
        assertEquals("", NoteUnit.CONTENT_TEXT.apply(msg, Map.of()));
    }
    
    @Test
    void testContentLines() {
        Message msg = new Message();
        msg.setText("Line 1\nLine 2\nLine 3\nLine 4");
        
        // Тест строк 2-3
        Map<String, String> params = Map.of("range", "2-3");
        assertEquals("Line 2\nLine 3", NoteUnit.CONTENT_LINES.apply(msg, params));
        
        // Тест без параметров (должен вернуть весь текст)
        assertEquals("Line 1\nLine 2\nLine 3\nLine 4", NoteUnit.CONTENT_LINES.apply(msg, Map.of()));
    }
    
    @Test
    void testContentLinesEdgeCases() {
        Message msg = new Message();
        msg.setText("Line 1\nLine 2");
        
        // Тест с некорректными границами
        Map<String, String> params = Map.of("range", "5-10");
        assertEquals("Line 2", NoteUnit.CONTENT_LINES.apply(msg, params));
        
        // Тест с отрицательными границами
        params = Map.of("range", "-1-5");
        assertEquals("Line 1\nLine 2", NoteUnit.CONTENT_LINES.apply(msg, params));
    }
    
    @Test
    void testContentLinesEmptyText() {
        Message msg = new Message();
        
        Map<String, String> params = Map.of("range", "1-5");
        assertEquals("", NoteUnit.CONTENT_LINES.apply(msg, params));
    }
    
    @Test
    void testMatch() {
        assertEquals(NoteUnit.CONTENT, NoteUnit.match("content"));
        assertEquals(NoteUnit.CONTENT_TEXT, NoteUnit.match("content:text"));
        assertEquals(NoteUnit.CONTENT_LINES, NoteUnit.match("content:[1-5]"));
        assertEquals(NoteUnit.CONTENT_LINES, NoteUnit.match("content:[10-20]"));
        
        // Несуществующие
        assertNull(NoteUnit.match("content:5"));
        assertNull(NoteUnit.match("unknown"));
        assertNull(NoteUnit.match("content:[invalid]"));
    }
    
    @Test
    void testParseParams() {
        // Для content:[X-Y]
        Map<String, String> params = NoteUnit.parseParams("content:[2-5]");
        assertEquals("2-5", params.get("range"));
        
        // Для остальных - пустые параметры
        params = NoteUnit.parseParams("content");
        assertTrue(params.isEmpty());
        
        params = NoteUnit.parseParams("content:text");
        assertTrue(params.isEmpty());
    }
    
    @Test
    void testPattern() {
        assertNotNull(NoteUnit.CONTENT.pattern());
        assertNotNull(NoteUnit.CONTENT_TEXT.pattern());
        assertNotNull(NoteUnit.CONTENT_LINES.pattern());
    }
    
    @Test
    void testEnumValues() {
        assertEquals(3, NoteUnit.values().length);
    }
} 