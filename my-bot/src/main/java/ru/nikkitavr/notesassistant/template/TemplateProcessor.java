package ru.nikkitavr.notesassistant.template;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static ru.nikkitavr.notesassistant.template.TemplateUnitContext.SINGLE_ARGUMENT;

public final class TemplateProcessor {
  private static final Pattern TEMPLATE =
      Pattern.compile("\\{\\{\\s*(\\w+)\\s*([^}]*)}}");
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

  private static Map<String,String> parseArgs(String s) {
    Map<String,String> m = new LinkedHashMap<>();
    if (s == null || s.isBlank()) return m;

    s = s.trim();
    /* ── НОВОЕ: компактный синтаксис  {{unit:val}}  ->  -value:val ── */
    if (s.charAt(0) == ':') {                        // начинается с ':'
      m.put(SINGLE_ARGUMENT, s.substring(1));              // всё остальное ‒ value
      return m;
    }

    /* ── старый разбор «-k:v -q:w» ── */
    for (String t : s.split("\\s+")) {
      int i = t.indexOf(':');
      if (t.startsWith("-") && i > 0)
        m.put(t.substring(1, i), t.substring(i + 1));
    }
    return m;
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


