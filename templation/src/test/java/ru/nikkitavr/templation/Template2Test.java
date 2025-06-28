package ru.nikkitavr.templation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Подробные тест-кейсы для итоговой реализации TemplateProcessor.
 */
class Template2Test {

  /* =======================================================================
   *                          ТЕСТОВЫЕ КОНТЕКСТЫ
   * ===================================================================== */

  /** Реальный рабочий контекст со «стейтом» пользователя и пинг-методом. */
  static final class AppContext extends TemplateContext {
    private String user = "UNKNOWN";
    private String lastPing;

    AppContext withUser(String u) { this.user = u; return this; }

    /* методы, которыми будут пользоваться юниты */
    String greet(String a1, String a2) { return "["+a1+"-"+a2+" for "+user+"]"; }
    void ping(String url)              { this.lastPing = url; }

    String getLastPing()               { return lastPing; }
  }

  /** «Чужой» контекст, чтобы проверить type-safety в process(). */
  static final class OtherCtx extends TemplateContext {}

  /* =======================================================================
   *                               Т Е С Т Ы
   * ===================================================================== */

  /**
   * Happy-path: длинная форма аргументов + компактная форма,
   * замена текста и удаление блока.
   */
  @Test
  void process_happyPath_longAndCompactSyntax() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("unit",
            ctx -> ctx.replace(
                ctx.greet(ctx.arg("arg1"), ctx.arg("arg2"))))
        .register("ping",
            ctx -> { ctx.ping(ctx.arg(TemplateContext.SINGLE_ARGUMENT));
              return ctx.replace(""); })
        .build();

    AppContext global = new AppContext().withUser("Alice");

    String in  = "A {{unit -arg1:X -arg2:Y}} B {{ping:https://ex.com}} C";
    String out = proc.process(in, global);

    assertEquals("A [X-Y for Alice] B  C", out);
    assertEquals("https://ex.com", global.getLastPing());
  }

  /**
   * Поддержка компактной формы {{unit:val}} без пробелов.
   */
  @Test
  void process_compactSyntaxSingleValue() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("e", ctx -> ctx.replace(ctx.arg("value")))
        .build();

    assertEquals("VAL", proc.process("{{e:VAL}}", new AppContext()));
  }

  /**
   * Блок без аргументов: args() пустой.
   */
  @Test
  void process_unitWithoutArgs() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("empty",
            ctx -> ctx.replace(ctx.args().isEmpty() ? "OK" : "FAIL"))
        .build();

    String res = proc.process(">>{{empty}}<<", new AppContext());
    assertEquals(">>OK<<", res);
  }

  /**
   * Неизвестный unit остаётся в тексте неизменённым.
   */
  @Test
  void process_unknownUnitRemains() {
    TemplateEngine proc = TemplateEngine.builder(AppContext.class).build();
    String str = "text {{unknown}} text";
    assertEquals(str, proc.process(str, new AppContext()));
  }

  /**
   * В билдере нельзя регистрировать одинаковые имена.
   */
  @Test
  void builder_duplicateNameThrows() {
    Executable code = () -> TemplateEngine
        .builder(AppContext.class)
        .register("dup", ctx -> ctx.replace("1"))
        .register("dup", ctx -> ctx.replace("2"));

    assertThrows(IllegalStateException.class, code);
  }

  /**
   * Если в process() передать контекст другого класса → IllegalArgumentException.
   */
  @Test
  void process_wrongContextTypeThrows() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("x", ctx -> ctx.replace("ok"))
        .build();

    Executable call = () -> proc.process("{{x}}", new OtherCtx());

    assertThrows(IllegalArgumentException.class, call);
  }

  /**
   * Handler ни в коем случае не должен вернуть null.
   */
  @Test
  void process_handlerReturningNullThrows() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("x", ctx -> null)
        .build();

    Executable call = () -> proc.process("{{x}}", new AppContext());

    assertThrows(IllegalStateException.class, call);
  }

  /**
   * Один и тот же объект контекста переиспользуется,
   * а args корректно обновляется для каждого вхождения.
   */
  @Test
  void process_contextReuseAndArgsRefresh() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("echo", ctx -> ctx.replace(ctx.arg("v")))
        .build();

    AppContext shared = new AppContext();

    String out = proc.process("{{echo -v:1}} {{echo -v:2}} {{echo -v:3}}", shared);
    assertEquals("1 2 3", out);
  }

  /**
   * Пустая строка без шаблонов возвращается как есть.
   */
  @Test
  void process_stringWithoutTemplates() {
    TemplateEngine proc = TemplateEngine.builder(AppContext.class).build();
    assertEquals("plain", proc.process("plain", new AppContext()));
  }

  /**
   * Смешанные формы аргументов в одной строке.
   */
  @Test
  void process_mixedLongAndCompactForms() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("u", ctx ->
            ctx.replace(ctx.arg("a") + ctx.arg("value")))
        .build();

    String res = proc.process(
        "{{u -a:X}}-{{u:Y}}", new AppContext());
    assertEquals("Xnull-nullY", res);
  }

  /**
   * Тесты для TemplateUnit: builder без name/handler, build с name/handler, выброс исключения при отсутствии обязательных полей.
   */


  /**
   * Тесты для TemplateUnitContext: методы arg, args, raw, source, replace через публичный API.
   */
  @Test
  void templateUnitContext_methods() {
    class Ctx extends TemplateContext {
      String lastRaw, lastSource, lastA, lastB, replaced;
    }
    var ctx = new Ctx();
    var proc = TemplateEngine
            .builder(Ctx.class)
            .register("test", c -> {
              c.lastRaw = c.raw();
              c.lastSource = c.source();
              c.lastA = c.arg("a");
              c.lastB = c.arg("b");
              c.replaced = c.replace("ZZ");
              return c.replace("ZZ");
            })
            .build();
    String result = proc.process("xx{{test -a:1 -b:2}}yy", ctx);
    org.junit.jupiter.api.Assertions.assertEquals("xxZZyy", result);
    org.junit.jupiter.api.Assertions.assertEquals("{{test -a:1 -b:2}}", ctx.lastRaw);
    org.junit.jupiter.api.Assertions.assertEquals("xx{{test -a:1 -b:2}}yy", ctx.lastSource);
    org.junit.jupiter.api.Assertions.assertEquals("1", ctx.lastA);
    org.junit.jupiter.api.Assertions.assertEquals("2", ctx.lastB);
    org.junit.jupiter.api.Assertions.assertEquals("xxZZyy", ctx.replaced);
  }

  /**
   * Множественные юниты в одной строке с разными формами аргументов.
   */
  @Test
  void process_multipleUnitsInOneString() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("a", ctx -> ctx.replace("A" + ctx.arg("v")))
        .register("b", ctx -> ctx.replace("B" + ctx.arg("value")))
        .register("c", ctx -> ctx.replace("C"))
        .build();

    String result = proc.process("{{a -v:1}} {{b:2}} {{c}}", new AppContext());
    assertEquals("A1 B2 C", result);
  }

  /**
   * Аргументы со спецсимволами и пробелами.
   */
  @Test
  void process_argsWithSpecialCharacters() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("test", ctx -> ctx.replace(ctx.arg("path") + " -> " + ctx.arg("value")))
        .build();

    String result = proc.process("{{test -path:/home/user -value:'file name with spaces''}}", new AppContext());
    assertEquals("/home/user -> file name with spaces", result);
  }

  /**
   * Длинная цепочка шаблонов - каждый шаблон обрабатывает результат предыдущего.
   */
  @Test
  void process_chainOfTemplates() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("step1", ctx -> ctx.replace("STEP1_" + ctx.arg("value")))
        .register("step2", ctx -> ctx.replace("STEP2_" + ctx.arg("value")))
        .register("step3", ctx -> ctx.replace("STEP3_" + ctx.arg("value")))
        .build();

    String result = proc.process("{{step1:input}}", new AppContext());
    result = proc.process("{{step2:" + result + "}}", new AppContext());
    result = proc.process("{{step3:" + result + "}}", new AppContext());
    
    assertEquals("STEP3_STEP2_STEP1_input", result);
  }

  /**
   * Кастомный контекст с дополнительными методами и состоянием.
   */
  @Test
  void process_customContextWithState() {
    class StatefulContext extends TemplateContext {
      private int counter = 0;
      private StringBuilder log = new StringBuilder();
      
      void increment() { counter++; }
      int getCounter() { return counter; }
      void log(String message) { log.append(message).append(";"); }
      String getLog() { return log.toString(); }
    }

    TemplateEngine proc = TemplateEngine
        .builder(StatefulContext.class)
        .register("count", ctx -> {
          ctx.increment();
          return ctx.replace("Count: " + ctx.getCounter());
        })
        .register("log", ctx -> {
          ctx.log(ctx.arg("message"));
          return ctx.replace("");
        })
        .build();

    StatefulContext ctx = new StatefulContext();
    String result = proc.process("{{count}} {{log -message:test1}} {{count}} {{log -message:test2}}", ctx);
    
    assertEquals("Count: 1  Count: 2 ", result);
    assertEquals(2, ctx.getCounter());
    assertEquals("test1;test2;", ctx.getLog());
  }

  /**
   * Проверка корректного обновления контекста между вызовами шаблонов.
   */
  @Test
  void process_contextUpdatesBetweenCalls() {
    class TrackingContext extends TemplateContext {
      private String lastProcessed = "";
      
      void setLastProcessed(String value) { this.lastProcessed = value; }
      String getLastProcessed() { return lastProcessed; }
    }

    TemplateEngine proc = TemplateEngine
        .builder(TrackingContext.class)
        .register("track", ctx -> {
          ctx.setLastProcessed(ctx.raw());
          return ctx.replace("[" + ctx.arg("id") + "]");
        })
        .build();

    TrackingContext ctx = new TrackingContext();
    String result = proc.process("{{track -id:1}} {{track -id:2}} {{track -id:3}}", ctx);
    
    assertEquals("[1] [2] [3]", result);
    assertEquals("{{track -id:3}}", ctx.getLastProcessed()); // последний обработанный
  }

  /**
   * Пустые аргументы и edge cases.
   */
  @Test
  void process_emptyArgsAndEdgeCases() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("empty", ctx -> ctx.replace("EMPTY"))
        .register("echo", ctx -> ctx.replace(ctx.arg("value") != null ? ctx.arg("value") : "NULL"))
        .build();

    assertEquals("EMPTY", proc.process("{{empty}}", new AppContext()));
    assertEquals("NULL", proc.process("{{echo:}}", new AppContext())); // пустое значение
    assertEquals("text", proc.process("{{echo:text}}", new AppContext()));
  }

  /**
   * Смешанные шаблоны с неизвестными юнитами.
   */
  @Test
  void process_mixedKnownAndUnknownUnits() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("known", ctx -> ctx.replace("KNOWN"))
        .build();

    String result = proc.process("{{unknown1}} {{known}} {{unknown2}}", new AppContext());
    assertEquals("{{unknown1}} KNOWN {{unknown2}}", result);
  }

  /**
   * Проверка работы с большим количеством шаблонов подряд.
   */
  @Test
  void process_manyTemplatesInSequence() {
    TemplateEngine proc = TemplateEngine
        .builder(AppContext.class)
        .register("num", ctx -> ctx.replace(ctx.arg("value")))
        .build();

    StringBuilder input = new StringBuilder();
    StringBuilder expected = new StringBuilder();
    
    for (int i = 1; i <= 10; i++) {
      input.append("{{num -value:").append(i).append("}} ");
      expected.append(i).append(" ");
    }

    String result = proc.process(input.toString(), new AppContext());
    assertEquals(expected.toString(), result);
  }

  @Test
  void longForm_emptyQuotedValue_keptAsEmptyString() {
    TemplateEngine p = TemplateEngine
        .builder()
        .register("u", ctx -> ctx.replace(
            ctx.arg("k") == null ? "NULL" : "[" + ctx.arg("k") + "]"))
        .build();

    String out = p.process("{{u -k:\"\"}}", new TemplateContext());
    assertEquals("[]", out);                 // пустая строка сохраняется
  }

  /* ---------- 2. Пустое значение в кавычках, компактная форма ---------- */
  @Test
  void compactForm_emptyQuotedValue_keptAsEmptyString() {
    TemplateEngine p = TemplateEngine
        .builder()
        .register("e", ctx -> ctx.replace(
            ctx.arg("value") == null ? "NULL" : "#" + ctx.arg("value") + "#"))
        .build();

    String out = p.process("{{e:\"\"}}", new TemplateContext());
    assertEquals("##", out);                 // тоже пустая строка
  }

  /* ---------- 3. Длинная форма без кавычек и без символов (`-k:`) ---------- */
  @Test
  void longForm_keyWithoutValue_isIgnored() {
    TemplateEngine p = TemplateEngine
        .builder()
        .register("u", ctx -> ctx.replace(
            ctx.arg("k") == null ? "NULL" : ctx.arg("k")))
        .build();

    String out = p.process("{{u -k: -x:1}}", new TemplateContext());
    assertEquals("NULL", out);               // ключ k отсутствует
  }

  /* ---------- 4. Нет пробела между двумя ключами (`-k1:-k2:val`) ---------- */
  @Test
  void longForm_twoKeysStuckTogether_secondAbsorbedIntoFirst() {
    TemplateEngine p = TemplateEngine
        .builder()
        .register("u", ctx -> ctx.replace(ctx.arg("k1")))
        .build();

    String out = p.process("{{u -k1:-k2:abc}}", new TemplateContext());
    assertEquals("-k2:abc", out);            // всё попало в значение k1
  }
}
