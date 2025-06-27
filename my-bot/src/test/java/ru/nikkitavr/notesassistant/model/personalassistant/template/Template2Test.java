package ru.nikkitavr.notesassistant.model.personalassistant.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.nikkitavr.notesassistant.template.TemplateProcessor;
import ru.nikkitavr.notesassistant.template.TemplateUnitContext;

/**
 * Подробные тест-кейсы для итоговой реализации TemplateProcessor.
 */
class Template2Test {

  /* =======================================================================
   *                          ТЕСТОВЫЕ КОНТЕКСТЫ
   * ===================================================================== */

  /** Реальный рабочий контекст со «стейтом» пользователя и пинг-методом. */
  static final class AppContext extends TemplateUnitContext {
    private String user = "UNKNOWN";
    private String lastPing;

    AppContext withUser(String u) { this.user = u; return this; }

    /* методы, которыми будут пользоваться юниты */
    String greet(String a1, String a2) { return "["+a1+"-"+a2+" for "+user+"]"; }
    void ping(String url)              { this.lastPing = url; }

    String getLastPing()               { return lastPing; }
  }

  /** «Чужой» контекст, чтобы проверить type-safety в process(). */
  static final class OtherCtx extends TemplateUnitContext {}

  /* =======================================================================
   *                               Т Е С Т Ы
   * ===================================================================== */

  /**
   * Happy-path: длинная форма аргументов + компактная форма,
   * замена текста и удаление блока.
   */
  @Test
  void process_happyPath_longAndCompactSyntax() {
    TemplateProcessor proc = TemplateProcessor
        .builder(AppContext.class)
        .register("unit",
            ctx -> ctx.replace(
                ctx.greet(ctx.arg("arg1"), ctx.arg("arg2"))))
        .register("ping",
            ctx -> { ctx.ping(ctx.arg(TemplateUnitContext.SINGLE_ARGUMENT));
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
    TemplateProcessor proc = TemplateProcessor
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
    TemplateProcessor proc = TemplateProcessor
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
    TemplateProcessor proc = TemplateProcessor.builder(AppContext.class).build();
    String str = "text {{unknown}} text";
    assertEquals(str, proc.process(str, new AppContext()));
  }

  /**
   * В билдере нельзя регистрировать одинаковые имена.
   */
  @Test
  void builder_duplicateNameThrows() {
    Executable code = () -> TemplateProcessor
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
    TemplateProcessor proc = TemplateProcessor
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
    TemplateProcessor proc = TemplateProcessor
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
    TemplateProcessor proc = TemplateProcessor
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
    TemplateProcessor proc = TemplateProcessor.builder(AppContext.class).build();
    assertEquals("plain", proc.process("plain", new AppContext()));
  }

  /**
   * Смешанные формы аргументов в одной строке.
   */
  @Test
  void process_mixedLongAndCompactForms() {
    TemplateProcessor proc = TemplateProcessor
        .builder(AppContext.class)
        .register("u", ctx ->
            ctx.replace(ctx.arg("a") + ctx.arg("value")))
        .build();

    String res = proc.process(
        "{{u -a:X}}-{{u:Y}}", new AppContext());
    assertEquals("Xnull-nullY", res);
  }
}
