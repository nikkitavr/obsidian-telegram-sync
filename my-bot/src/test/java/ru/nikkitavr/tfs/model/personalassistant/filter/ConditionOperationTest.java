package ru.nikkitavr.tfs.model.personalassistant.filter;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ConditionOperationTest {
    @Test
    void testFromEqual() {
        assertEquals(ConditionOperation.EQUAL, ConditionOperation.from("="));
    }

    @Test
    void testFromContain() {
        assertEquals(ConditionOperation.CONTAIN, ConditionOperation.from("~"));
    }

    @Test
    void testValue() {
        assertEquals("=", ConditionOperation.EQUAL.getValue());
        assertEquals("~", ConditionOperation.CONTAIN.getValue());
    }

    @Test
    void testFromInvalid() {
        assertThrows(Exception.class, () -> ConditionOperation.from("!"));
    }

    @Test
    void testAnyLeftMatchEqual() {
        assertTrue(ConditionOperation.EQUAL.anyLeftMatch(List.of("foo", "bar"), "foo"));
        assertFalse(ConditionOperation.EQUAL.anyLeftMatch(List.of("foo", "bar"), "baz"));
        assertFalse(ConditionOperation.EQUAL.anyLeftMatch(List.of((String)null), "baz"));
    }

    @Test
    void testAnyLeftMatchContain() {
        assertTrue(ConditionOperation.CONTAIN.anyLeftMatch(List.of("foobar", "baz"), "foo"));
        assertFalse(ConditionOperation.CONTAIN.anyLeftMatch(List.of("bar", "baz"), "foo"));
        assertFalse(ConditionOperation.CONTAIN.anyLeftMatch(List.of((String)null), "foo"));
    }

    @Test
    void testMatch() {
        assertTrue(ConditionOperation.EQUAL.match("foo", "foo"));
        assertFalse(ConditionOperation.EQUAL.match("foo", "bar"));
        assertTrue(ConditionOperation.CONTAIN.match("foobar", "foo"));
        assertFalse(ConditionOperation.CONTAIN.match("bar", "foo"));
    }
} 