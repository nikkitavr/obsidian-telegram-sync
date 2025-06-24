package ru.nikkitavr.tfs.model.personalassistant.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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
        List<String> nullList = new ArrayList<>();
        nullList.add(null);
        assertFalse(ConditionOperation.EQUAL.anyLeftMatch(nullList, "baz"));
        assertTrue(ConditionOperation.EQUAL.anyLeftMatch(Arrays.asList("baz", null), "baz"));
    }

    @Test
    void testAnyLeftMatchContain() {
        assertTrue(ConditionOperation.CONTAIN.anyLeftMatch(List.of("foobar", "baz"), "foo"));
        assertFalse(ConditionOperation.CONTAIN.anyLeftMatch(List.of("bar", "baz"), "foo"));
        List<String> nullList = new ArrayList<>();
        nullList.add(null);
        assertFalse(ConditionOperation.CONTAIN.anyLeftMatch(nullList, "foo"));
        assertTrue(ConditionOperation.CONTAIN.anyLeftMatch(Arrays.asList("bazooka", null), "baz"));
    }

    @Test
    void testMatch() {
        assertTrue(ConditionOperation.EQUAL.match("foo", "foo"));
        assertFalse(ConditionOperation.EQUAL.match("foo", "bar"));
        assertTrue(ConditionOperation.CONTAIN.match("foobar", "foo"));
        assertFalse(ConditionOperation.CONTAIN.match("bar", "foo"));
    }
} 