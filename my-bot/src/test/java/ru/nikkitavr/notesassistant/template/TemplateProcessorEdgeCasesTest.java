package ru.nikkitavr.notesassistant.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты для edge cases парсинга аргументов и граничных случаев регулярных выражений.
 */
class TemplateProcessorEdgeCasesTest {

    /* =======================================================================
     *                    ТЕСТЫ ПАРСИНГА АРГУМЕНТОВ
     * ===================================================================== */

    /**
     * Аргументы с экранированными символами в длинной форме.
     */
    @Test
    void parseArgs_escapedCharacters_longForm() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> {
                    String result = "path:" + ctx.arg("path") + 
                                   ",value:" + ctx.arg("value") + 
                                   ",special:" + ctx.arg("special");
                    return ctx.replace(result);
                })
                .build();

        // Экранированные символы в значениях
        String result = proc.process("{{test -path:C:\\Users\\Name -value:file\\with\\slashes -special:quotes\"inside}}", 
                                   new TemplateUnitContext());
        
        Assertions.assertEquals("path:C:\\Users\\Name,value:file\\with\\slashes,special:quotes\"inside", result);
    }

    /**
     * Аргументы с экранированными символами в компактной форме.
     */
    @Test
    void parseArgs_escapedCharacters_compactForm() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> ctx.replace("value:" + ctx.arg("value")))
                .build();

        // Экранированные символы в компактной форме
        String result = proc.process("{{test:C:\\Users\\Name\\file.txt}}", new TemplateUnitContext());
        Assertions.assertEquals("value:C:\\Users\\Name\\file.txt", result);
    }

    /**
     * Очень длинные аргументы.
     */
    @Test
    void parseArgs_veryLongArguments() {
        StringBuilder longValue = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longValue.append("very_long_value_").append(i).append("_");
        }
        String longArg = longValue.toString();

        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> ctx.replace("length:" + ctx.arg("value").length()))
                .build();

        String result = proc.process("{{test -value:" + longArg + "}}", new TemplateUnitContext());
        Assertions.assertEquals("length:" + longArg.length(), result);
    }

    /**
     * Аргументы с пробелами и табуляцией.
     */
    @Test
    void parseArgs_whitespaceAndTabs() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> {
                    String result = "arg1:" + ctx.arg("arg1") + 
                                   ",arg2:" + ctx.arg("arg2") + 
                                   ",arg3:" + ctx.arg("arg3");
                    return ctx.replace(result);
                })
                .build();

        // Пробелы и табуляция в аргументах
        String result = proc.process("{{test -arg1:\"value with spaces\t\" -arg2:\"\ttabbed value\" -arg3:normal}}",
                                   new TemplateUnitContext());
        
        Assertions.assertEquals("arg1:value with spaces\t,arg2:\ttabbed value,arg3:normal", result);
    }

    /* =======================================================================
     *                ТЕСТЫ РЕГУЛЯРНЫХ ВЫРАЖЕНИЙ
     * ===================================================================== */

    /**
     * Шаблоны с вложенными скобками (должны остаться как есть).
     */
    @Test
    void regex_nestedBrackets_shouldRemainUnchanged() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> ctx.replace("PROCESSED"))
                .build();

        // Вложенные скобки не должны обрабатываться как шаблоны
        String result = proc.process("{{test}} and {{nested {{brackets}}}} and {{test}}", new TemplateUnitContext());
        Assertions.assertEquals("PROCESSED and {{nested {{brackets}}}} and PROCESSED", result);
    }

    /**
     * Шаблоны с экранированными символами.
     */
    @Test
    void regex_escapedCharacters() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> ctx.replace("PROCESSED"))
                .build();

        // Экранированные символы в шаблонах
        String result = proc.process("{{test}} and \\{{escaped}} and {{test}}", new TemplateUnitContext());
        Assertions.assertEquals("PROCESSED and \\{{escaped}} and PROCESSED", result);
    }

    /**
     * Очень длинные имена юнитов.
     */
    @Test
    void regex_veryLongUnitNames() {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longName.append("very_long_unit_name_").append(i).append("_");
        }
        String unitName = longName.toString();

        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register(unitName, ctx -> ctx.replace("LONG_UNIT_PROCESSED"))
                .build();

        String result = proc.process("{{" + unitName + "}}", new TemplateUnitContext());
        Assertions.assertEquals("LONG_UNIT_PROCESSED", result);
    }

    /**
     * Шаблоны с пробелами и табуляцией в именах.
     */
    @Test
    void regex_whitespaceInUnitNames() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> ctx.replace("PROCESSED"))
                .register("test2", ctx -> ctx.replace("PROCESSED2"))
                .build();

        // Пробелы в именах юнитов
        String result = proc.process("{{ test }} {{  test2  }}", new TemplateUnitContext());
        Assertions.assertEquals("PROCESSED PROCESSED2", result);
    }

    /**
     * Граничные случаи с пустыми шаблонами.
     */
    @Test
    void regex_edgeCases_emptyTemplates() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test", ctx -> ctx.replace("PROCESSED"))
                .build();

        // Пустые шаблоны должны остаться как есть
        String result = proc.process("{{}} {{ }} {{  }}", new TemplateUnitContext());
        Assertions.assertEquals("{{}} {{ }} {{  }}", result);
    }

    /**
     * Шаблоны с цифрами в именах.
     */
    @Test
    void regex_numbersInUnitNames() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test123", ctx -> ctx.replace("NUMERIC"))
                .register("123test", ctx -> ctx.replace("START_NUMERIC"))
                .register("test_456", ctx -> ctx.replace("UNDERSCORE_NUMERIC"))
                .build();

        String result = proc.process("{{test123}} {{123test}} {{test_456}}", new TemplateUnitContext());
        Assertions.assertEquals("NUMERIC START_NUMERIC UNDERSCORE_NUMERIC", result);
    }

    /**
     * Шаблоны с специальными символами в именах.
     */
    @Test
    void regex_specialCharactersInNames() {
        TemplateProcessor proc = TemplateProcessor
                .builder()
                .register("test-name", ctx -> ctx.replace("HYPHEN"))
                .register("test_name", ctx -> ctx.replace("UNDERSCORE"))
                .register("test.name", ctx -> ctx.replace("DOT"))
                .build();

        String result = proc.process("{{test-name}} {{test_name}} {{test.name}}", new TemplateUnitContext());
        Assertions.assertEquals("HYPHEN UNDERSCORE DOT", result);
    }
} 