package ru.nikkitavr.templation;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TemplateContextTest {
    static class TestCtx extends TemplateContext {
        void callInit(String source, int start, int end, String raw, Map<String, String> args) {
            super.init(source, start, end, raw, args);
        }
    }

    @Test
    void arg_args_raw_source_methods() {
        var ctx = new TestCtx();
        Map<String, String> args = Map.of("a", "1", "b", "2");
        ctx.callInit("source string", 2, 8, "{{raw}}", args);
        Assertions.assertEquals("{{raw}}", ctx.raw());
        Assertions.assertEquals("source string", ctx.source());
        Assertions.assertEquals("1", ctx.arg("a"));
        Assertions.assertEquals("2", ctx.arg("b"));
        Assertions.assertEquals(args, ctx.args());
    }

    @Test
    void replace_method() {
        var ctx = new TestCtx();
        ctx.callInit("source string", 2, 7, "{{raw}}", Map.of());
        Assertions.assertEquals("soZZstring", ctx.replace("ZZ"));
        Assertions.assertEquals("sostring", ctx.replace(null));
    }
} 