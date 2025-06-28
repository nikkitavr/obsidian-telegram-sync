package ru.nikkitavr.notesassistant.template;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateProcessor {
  private static final Pattern TEMPLATE =
      Pattern.compile("\\{\\{\\s*([\\w.-]+)\\s*([^}]*)}}");
  private final Class<? extends TemplateUnitContext> ctxClass;
  private final Map<String, TemplateUnitProcessor<? extends TemplateUnitContext>> registry;

  private TemplateProcessor(Class<? extends TemplateUnitContext> cls, Map<String, TemplateUnitProcessor<?>> reg) {
    this.ctxClass = cls;
    this.registry = Map.copyOf(reg);
  }

  /* ===== публичный API ===== */
  public String process(String source, TemplateUnitContext ctxInstance) {
    Objects.requireNonNull(ctxInstance, "context instance is null");

    if (!ctxClass.isInstance(ctxInstance))          // защита от ошибки вызова
      throw new IllegalArgumentException("Expected context of " + ctxClass.getName());

    String current = source;
    int searchFrom = 0;
    while (true) {
      Matcher m = TEMPLATE.matcher(current);
      if (!m.find(searchFrom)) {
        break;     // шаблонов больше нет
      }

      String raw   = m.group(0);
      String name  = m.group(1);
      String argSt = m.group(2);

      @SuppressWarnings("unchecked")
      var handler = (TemplateUnitProcessor<TemplateUnitContext>) registry.get(name);
      if (handler == null) {              // неизвестный unit: пропускаем
        searchFrom = m.end();           // …просто шагаем дальше
        continue;
      }

      Map<String,String> args = parseArgs(argSt);

      ctxInstance.init(current, m.start(), m.end(), raw, args);

      String newCurrent = handler.apply(ctxInstance);
      if (newCurrent == null)
        throw new IllegalStateException("Handler returned null for unit " + name);

      current    = newCurrent;            // продолжаем с новой строкой
      searchFrom = 0;                     // …и ищем с начала
    }
    return current;
  }

  /* ===== аргументы:  -k:v   -k:"v w"   :val   :"val w"  ===== */
  private static Map<String,String> parseArgs(String s) {
    Map<String,String> map = new LinkedHashMap<>();
    if (s == null || (s = s.trim()).isEmpty()) return map;

    /* компактная форма {{unit:val}} */
    if (s.charAt(0) == ':') {
      String v = s.substring(1).trim();
      if(!v.isEmpty()) {
        map.put(
            TemplateUnitContext.SINGLE_ARGUMENT,
            stripQuotes(s.substring(1).trim())
        );
      }
      return map;
    }

    /* длинная форма -k:val или -k:"v w" или -k:'v w' */
    Pattern tok = Pattern.compile(
        "-(\\w+):" +                           // ключ
            "(\"[^\"]*\"|'[^']*'|[^\\s]+)"        // значение
    );
    Matcher m = tok.matcher(s);
    while (m.find()) {
      map.put(m.group(1), stripQuotes(m.group(2)));
    }
    return map;
  }

  /** убирает наружные кавычки, если они есть */
  private static String stripQuotes(String v) {
    if (v.length() >= 2) {
      char f = v.charAt(0), l = v.charAt(v.length()-1);
      if ((f == '"' && l == '"') || (f == '\'' && l == '\''))
        return v.substring(1, v.length()-1);
    }
    return v;
  }

  /* ===== фабричный метод-билдер ===== */
  public static <C extends TemplateUnitContext> Builder<C> builder(Class<C> ctxClass) {
    return new Builder<>(ctxClass);
  }

  public static Builder<TemplateUnitContext> builder() {
    return new Builder<>(TemplateUnitContext.class);
  }

  /* ====== generic-builder ====== */
  public static final class Builder<C extends TemplateUnitContext> {
    private final Class<C> ctxClass;
    private final Map<String, TemplateUnitProcessor<C>> tmp = new HashMap<>();

    private Builder(Class<C> cls) { this.ctxClass = cls; }

    public Builder<C> register(String name, TemplateUnitProcessor<C> handler) {
      if (tmp.putIfAbsent(name, handler) != null)
        throw new IllegalStateException("unit already registered: " + name);
      return this;
    }

    public TemplateProcessor build() {
      return new TemplateProcessor(ctxClass, new HashMap<>(tmp));
    }
  }
}


