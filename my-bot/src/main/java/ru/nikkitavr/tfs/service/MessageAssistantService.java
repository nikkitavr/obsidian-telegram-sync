package ru.nikkitavr.tfs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nikkitavr.tfs.model.Message;
import ru.nikkitavr.tfs.model.MessageAssistantConfiguration;
import static ru.nikkitavr.tfs.model.MessageAssistantConfiguration.DistributionRule;
import ru.nikkitavr.tfs.utils.FileSystemUtils;
import ru.nikkitavr.tfs.utils.TemplateUtils;

public class MessageAssistantService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageAssistantService.class);

  private final MessageAssistantConfigurationProvider configProvider;
  private final FileSystemUtils fileSystemUtils;

  public MessageAssistantService(MessageAssistantConfigurationProvider configProvider,
      FileSystemUtils fileSystemUtils) {
    this.configProvider = configProvider;
    this.fileSystemUtils = fileSystemUtils;
  }

  /**
   * Process incoming message and store it according to configuration rules.
   */
  public void processMessage(Message message) {
    MessageAssistantConfiguration config = configProvider.get();
    if (config == null || config.getDistributionRules() == null) {
      return;
    }

    DistributionRule rule = getFirstMatchedRule(config, message);
    if (rule == null) {
      return;
    }

    String template;
    if (rule.getTemplatePath() != null && !rule.getTemplatePath().isBlank()) {
      Path tplPath = Path.of(config.getVaultPath(), rule.getTemplatePath());
      try {
        template = Files.readString(tplPath);
      } catch (IOException e) {
        LOGGER.error("Failed to read template {}", tplPath, e);
        template = "{{content}}";
      }
    } else {
      template = "{{content}}";
    }

    String noteContent = TemplateUtils.apply(template, message) + System.lineSeparator();

    if (rule.getNotePathTemplate() != null && !rule.getNotePathTemplate().isBlank()) {
      String notePathStr = TemplateUtils.apply(rule.getNotePathTemplate(), message);
      Path notePath = Path.of(config.getVaultPath(), notePathStr);
      try {
        fileSystemUtils.appendToFile(notePath, noteContent);
      } catch (IOException e) {
        LOGGER.error("Failed to write note {}", notePath, e);
      }
    }
  }

  private DistributionRule getFirstMatchedRule(MessageAssistantConfiguration config, Message message) {
    List<DistributionRule> rules = config.getDistributionRules();
    if (rules == null) {
      return null;
    }

    for (DistributionRule rule : rules) {
      if (matches(rule.getMessageFilterQuery(), message)) {
        return rule;
      }
    }
    return null;
  }

  private boolean matches(String query, Message message) {
    if (query == null || query.isBlank()) {
      return true;
    }
    Pattern pattern = Pattern.compile("\\{\\{([^=~]+)(=|~)([^}]+)\\}}");
    Matcher matcher = pattern.matcher(query);
    while (matcher.find()) {
      String key = matcher.group(1);
      String op = matcher.group(2);
      String value = matcher.group(3);

      if (!matchCondition(key, op, value, message)) {
        return false;
      }
    }
    return true;
  }

  private boolean matchCondition(String key, String op, String value, Message message) {
    switch (key) {
      case "chat":
        if (message.getChat() == null) return false;
        String chatName = message.getChat().getTitle();
        if (chatName == null || chatName.isEmpty()) {
          chatName = message.getChat().getFirstName();
        }
        String chatId = String.valueOf(message.getChat().getId());
        return compare(chatName, value, op) || compare(chatId, value, op);
      case "user":
        if (message.getFrom() == null) return false;
        String username = message.getFrom().getUserName();
        String fullName = (message.getFrom().getFirstName() + " " +
            (message.getFrom().getLastName() != null ? message.getFrom().getLastName() : "")).trim();
        String userId = String.valueOf(message.getFrom().getId());
        return compare(username, value, op) || compare(fullName, value, op) || compare(userId, value, op);
      case "content":
        String text = message.getText();
        if (text == null) text = message.getCaption();
        return compare(text, value, op);
      case "topic":
        if (message.getMessageThreadId() == null) return false;
        return compare(String.valueOf(message.getMessageThreadId()), value, op);
      default:
        return false;
    }
  }

  private boolean compare(String actual, String expected, String op) {
    if (actual == null) return false;
    if ("=".equals(op)) {
      return actual.equals(expected);
    } else if ("~".equals(op)) {
      return actual.contains(expected);
    }
    return false;
  }

}
