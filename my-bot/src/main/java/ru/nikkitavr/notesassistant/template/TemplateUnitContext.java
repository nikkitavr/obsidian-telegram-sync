package ru.nikkitavr.notesassistant.template;

import java.util.Map;

public class TemplateUnitContext {
  private String source;           // вся строка
  private String raw; // «{{tmpUnit1 -arg1:v1}}»
  private int start, end;
  private Map<String, String> args;

  public static final String SINGLE_ARGUMENT = "value";

  /**
   * заполняется процессором перед каждым вызовом handler'а
   */
  final void init(String source, int start, int end, String raw, Map<String,String> args) {
    this.source = source;
    this.start  = start;
    this.end    = end;
    this.raw    = raw;
    this.args   = args;
  }

  public String raw() {
    return raw;
  }

  public String source() {
    return source;
  }

  public String arg(String name) {
    return args.get(name);
  }

  public Map<String, String> args() {
    return args;
  }

  /**
   * Заменяет **только текущий** встретившийся шаблон на `replacement`
   * и возвращает новую строку.
   */
  public String replace(String replacement) {
    return source.substring(0, start) +
        (replacement != null ? replacement : "") +
        source.substring(end);
  }
}

