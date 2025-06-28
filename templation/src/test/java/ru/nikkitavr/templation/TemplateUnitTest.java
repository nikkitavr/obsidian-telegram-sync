package ru.nikkitavr.templation;

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
        Assertions.assertEquals(TemplateContext.class, unit.getContextClass());
    }

    @Test
    void templateUnit_builderAndValidation() {
        // build с name и handler
        var unit = TemplateUnit.builder()
                .name("test")
                .operation(ctx -> "ok")
                .build();
        org.junit.jupiter.api.Assertions.assertEquals("test", unit.getName());
        org.junit.jupiter.api.Assertions.assertNotNull(unit.getHandler());

        // builder без name
        var builderNoName = TemplateUnit.builder();
        builderNoName.operation(ctx -> "ok");
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, builderNoName::build);

        // builder без handler
        var builderNoHandler = TemplateUnit.builder();
        builderNoHandler.name("test");
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, builderNoHandler::build);
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
        class MyCtx extends TemplateContext {}
        var unit = TemplateUnit.builder(MyCtx.class)
                .name("custom")
                .operation(ctx -> "custom")
                .build();
        Assertions.assertEquals("custom", unit.getName());
        Assertions.assertEquals(MyCtx.class, unit.getContextClass());
    }
} 