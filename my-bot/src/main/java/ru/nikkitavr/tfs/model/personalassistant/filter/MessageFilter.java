package ru.nikkitavr.tfs.model.personalassistant.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.mvel2.MVEL;
import ru.nikkitavr.tfs.model.personalassistant.Message;

public class MessageFilter {
  @JsonIgnore
  private static final Pattern pattern = Pattern.compile("\\{\\{(\\w+)([=~])([^}]+)}}");
  @JsonIgnore
  private final String normalizedQuery;

  @Getter
  private String query;

  @JsonCreator
  public MessageFilter(String query) {
    this.query = query;
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
