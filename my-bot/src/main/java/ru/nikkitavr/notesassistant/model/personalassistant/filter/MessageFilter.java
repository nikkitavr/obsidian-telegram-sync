package ru.nikkitavr.notesassistant.model.personalassistant.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import ru.nikkitavr.notesassistant.infra.telegram.exception.UIException;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

public class MessageFilter {
  @JsonIgnore
  private static final Pattern pattern = Pattern.compile("\\{\\{(\\w+)(?:([=~])([^}]*))?}}");
  @JsonIgnore
  private final String normalizedQuery;

  @Getter
  private String query;

  @JsonCreator
  public MessageFilter(String query) {
    this.query = query;
    this.normalizedQuery = query
        .replaceAll("(?i)\\band\\b", "&&")
        .replaceAll("(?i)\\bor\\b", "||")
        .replaceAll("(?i)\\bnot\\b",  "!");
  }

  public boolean match(Message message) {
    Matcher matcher = pattern.matcher(normalizedQuery);
    StringBuilder resultExpression = new StringBuilder();

    while (matcher.find()) {
      ConditionType conditionType = ConditionType.from(matcher.group(1));
      ConditionOperation conditionOperation = matcher.group(2) != null ? ConditionOperation.from(matcher.group(2)) : null;
      String value = matcher.group(3) != null ? matcher.group(3) : null;

      Boolean conditionMatched = conditionType.match(message, conditionOperation, value);
      matcher.appendReplacement(resultExpression, String.valueOf(conditionMatched));
    }
    matcher.appendTail(resultExpression);

    try {
      return MVEL.evalToBoolean(resultExpression.toString(), Map.of());
    } catch (CompileException e) {
      throw new UIException("Please check filter query it might be wrong: %s. \n\n%s)".formatted(query, e.getMessage()));
    }
  }
}
