package ru.nikkitavr.templation;

@Deprecated
public final class TemplateUnit<C extends TemplateContext> {
  private final String name;
  private final Class<C> contextClass;
  private final TemplateUnitProcessor<C> handler;

  public static <C extends TemplateContext> Builder<C> builder(Class<C> clazz) {
    return new Builder<>(clazz);
  }

  public static Builder<TemplateContext> builder() {
    return new Builder<>(TemplateContext.class);
  }

  public static final class Builder<C extends TemplateContext> {
    private String name;
    private final Class<C> contextClass;
    private TemplateUnitProcessor<C> handler;

    private Builder(Class<C> clazz) {
      this.contextClass = clazz;
    }

    public Builder<C> name(String n) {
      this.name = n;
      return this;
    }

    public Builder<C> operation(TemplateUnitProcessor<C> h) {
      this.handler = h;
      return this;
    }

    public TemplateUnit<C> build() {
      if (name == null || handler == null) {
        throw new IllegalStateException("name and operation must be set");
      }
      return new TemplateUnit<>(name, contextClass, handler);
    }
  }

  public String getName() {
    return name;
  }

  public Class<C> getContextClass() {
    return contextClass;
  }

  public TemplateUnitProcessor<C> getHandler() {
    return handler;
  }

  /* ---------- закрытый конструктор ---------- */
  private TemplateUnit(String n, Class<C> cls, TemplateUnitProcessor<C> h) {
    this.name = n;
    this.contextClass = cls;
    this.handler = h;
  }
}
