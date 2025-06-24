package ru.nikkitavr.tfs.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import ru.nikkitavr.tfs.model.personalassistant.message.Message;

/** Simple template processor for {{variable}} placeholders. */
public class TemplateUtils {

  public static String apply(String template, Message message) {
    if (template == null || template.isEmpty()) {
      return "";
    }
    String result = template;
    if (message.getText() != null) {
      result = result.replace("{{content}}", message.getText());
    } else if (message.getCaption() != null) {
      result = result.replace("{{content}}", message.getCaption());
    } else {
      result = result.replace("{{content}}", "");
    }

    Instant messageDate = message.getDate();
    if (messageDate != null) {
      result = replaceDateVar(result, "messageDate", messageDate);
      result = replaceDateVar(result, "messageTime", messageDate);
    }
    Instant now = Instant.now();
    result = replaceDateVar(result, "date", now);
    result = replaceDateVar(result, "time", now);

    if (message.getChat() != null) {
      String chatName = message.getChat().getTitle();
      if (chatName == null || chatName.isEmpty()) {
        chatName = message.getChat().getFirstName();
      }
      if (chatName != null) {
        result = result.replace("{{chat:name}}", chatName);
      }
    }
    return result;
  }

  private static String replaceDateVar(String text, String var, Instant value) {
    int idx;
    while ((idx = text.indexOf("{{" + var + ":")) != -1) {
      int end = text.indexOf("}}", idx);
      if (end == -1) {
        break;
      }
      String format = text.substring(idx + var.length() + 3, end);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
      String formatted = formatter.format(value);
      text = text.substring(0, idx) + formatted + text.substring(end + 2);
    }
    return text;
  }
}
