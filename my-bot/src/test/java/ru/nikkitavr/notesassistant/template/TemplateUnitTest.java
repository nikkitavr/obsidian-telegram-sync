package ru.nikkitavr.notesassistant.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TemplateUnitTest {
    @Test
    void builder_buildWithNameAndHandler_success() {
        var unit = TemplateUnit.builder()
                .name("test")
                .operation(ctx -> "ok")
                .build();
        Assertions.assertEquals("test", unit.getName());
        Assertions.assertNotNull(unit.getHandler());
        Assertions.assertEquals(TemplateUnitContext.class, unit.getContextClass());
    }

    @Test
    void builder_withoutName_throws() {
        var builder = TemplateUnit.builder();
        builder.operation(ctx -> "ok");
        Assertions.assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void builder_withoutHandler_throws() {
        var builder = TemplateUnit.builder();
        builder.name("test");
        Assertions.assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void builder_withCustomContextClass() {
        class MyCtx extends TemplateUnitContext {}
        var unit = TemplateUnit.builder(MyCtx.class)
                .name("custom")
                .operation(ctx -> "custom")
                .build();
        Assertions.assertEquals("custom", unit.getName());
        Assertions.assertEquals(MyCtx.class, unit.getContextClass());
    }
} 