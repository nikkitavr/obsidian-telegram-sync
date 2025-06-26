package ru.nikkitavr.notesassistant.model.personalassistant.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class UnknownTemplateUnitExceptionTest {
    
    @Test
    void testExceptionCreation() {
        String expr = "unknown";
        UnknownTemplateUnitException exception = new UnknownTemplateUnitException(expr);
        
        assertNotNull(exception);
        assertEquals("Unknown template unit: " + expr, exception.getMessage());
    }
    
    @Test
    void testExceptionWithComplexExpression() {
        String expr = "complex:expression:with:params";
        UnknownTemplateUnitException exception = new UnknownTemplateUnitException(expr);
        
        assertEquals("Unknown template unit: " + expr, exception.getMessage());
    }
    
    @Test
    void testExceptionWithEmptyExpression() {
        String expr = "";
        UnknownTemplateUnitException exception = new UnknownTemplateUnitException(expr);
        
        assertEquals("Unknown template unit: " + expr, exception.getMessage());
    }
    
    @Test
    void testExceptionInheritance() {
        String expr = "test";
        UnknownTemplateUnitException exception = new UnknownTemplateUnitException(expr);
        
        assertTrue(exception instanceof RuntimeException);
    }
} 