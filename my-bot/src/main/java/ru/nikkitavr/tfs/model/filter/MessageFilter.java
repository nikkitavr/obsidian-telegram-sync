package ru.nikkitavr.tfs.model.filter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mvel2.MVEL;
import ru.nikkitavr.tfs.model.Message;

public class MessageFilter {
  private static final Pattern pattern = Pattern.compile("\\{\\{(\\w+)([=~])([^}]+)}}");

  private final String normalizedQuery;

  public MessageFilter(String query) {
    this.normalizedQuery = query
        .replaceAll("(?i)\\band\\b", "&&")
        .replaceAll("(?i)\\bor\\b",  "||");
  }

  public boolean match(Message message) {
    Matcher matcher = pattern.matcher(normalizedQuery);
    StringBuilder resultExpression = new StringBuilder();

    while (matcher.find()) {
      ConditionType conditionType = ConditionType.from(matcher.group(1));
      ConditionOperation conditionOperation = ConditionOperation.from(matcher.group(2));
      String value = matcher.group(3);

      Boolean conditionMatched = conditionType.match(message, conditionOperation, value);
      matcher.appendReplacement(resultExpression, String.valueOf(conditionMatched));
    }
    matcher.appendTail(resultExpression);

    return MVEL.evalToBoolean(resultExpression.toString(), Map.of());
  }
}
