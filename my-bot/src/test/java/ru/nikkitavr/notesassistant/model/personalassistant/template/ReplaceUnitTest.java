package ru.nikkitavr.notesassistant.model.personalassistant.template;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public class ReplaceUnitTest {
    
    @Test
    void testPattern() {
        ReplaceUnit unit = new ReplaceUnit();
        assertNotNull(unit.pattern());
    }
    
    @Test
    void testApplyThrowsException() {
        ReplaceUnit unit = new ReplaceUnit();
        Message msg = new Message();
        
        assertThrows(UnsupportedOperationException.class, () -> unit.apply(msg, Map.of()));
    }
    
    @Test
    void testPostProcessSimpleReplace() {
        ReplaceUnit unit = new ReplaceUnit();
        String input = "Hello {{replace:Hello=>Hi}}";
        
        String result = unit.postProcess(input);
        assertEquals("Hi ", result);
    }
    
    @Test
    void testPostProcessMultipleReplaces() {
        ReplaceUnit unit = new ReplaceUnit();
        String input = "{{replace:foo=>bar}} {{replace:baz=>qux}} foo baz";
        
        String result = unit.postProcess(input);
        assertEquals("  bar qux", result);
    }
    
    @Test
    void testPostProcessNoReplaces() {
        ReplaceUnit unit = new ReplaceUnit();
        String input = "Hello World";
        
        String result = unit.postProcess(input);
        assertEquals("Hello World", result);
    }
    
    @Test
    void testPostProcessEmptyString() {
        ReplaceUnit unit = new ReplaceUnit();
        String input = "";
        
        String result = unit.postProcess(input);
        assertEquals("", result);
    }
} 