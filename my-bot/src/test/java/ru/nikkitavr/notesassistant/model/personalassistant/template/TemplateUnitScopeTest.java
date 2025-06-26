package ru.nikkitavr.notesassistant.model.personalassistant.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class TemplateUnitScopeTest {
    
    @Test
    void testEnumValues() {
        assertEquals(2, TemplateUnitScope.values().length);
        
        assertNotNull(TemplateUnitScope.valueOf("NOTE_BODY"));
        assertNotNull(TemplateUnitScope.valueOf("GENERIC"));
    }
    
    @Test
    void testScopeNames() {
        assertEquals("NOTE_BODY", TemplateUnitScope.NOTE_BODY.name());
        assertEquals("GENERIC", TemplateUnitScope.GENERIC.name());
    }
    
    @Test
    void testScopeOrdinal() {
        assertEquals(0, TemplateUnitScope.NOTE_BODY.ordinal());
        assertEquals(1, TemplateUnitScope.GENERIC.ordinal());
    }
    
    @Test
    void testScopeEquality() {
        TemplateUnitScope scope1 = TemplateUnitScope.NOTE_BODY;
        TemplateUnitScope scope2 = TemplateUnitScope.NOTE_BODY;
        TemplateUnitScope scope3 = TemplateUnitScope.GENERIC;
        
        assertEquals(scope1, scope2);
        assertNotEquals(scope1, scope3);
    }
} 