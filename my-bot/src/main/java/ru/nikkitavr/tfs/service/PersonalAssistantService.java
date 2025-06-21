package ru.nikkitavr.tfs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nikkitavr.tfs.model.personalassistant.Message;
import ru.nikkitavr.tfs.model.personalassistant.PersonalAssistantConfiguration;
import static ru.nikkitavr.tfs.model.personalassistant.PersonalAssistantConfiguration.DistributionRule;
import ru.nikkitavr.tfs.utils.FileSystemUtils;
import ru.nikkitavr.tfs.utils.TemplateUtils;

public class PersonalAssistantService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonalAssistantService.class);

  private final PersonalAssistantConfigurationProvider configProvider;

  public PersonalAssistantService(PersonalAssistantConfigurationProvider configProvider) {
    this.configProvider = configProvider;
  }

  /**
   * Process incoming message and store it according to configuration rules.
   */
  public void processMessage(Message message) {
    PersonalAssistantConfiguration config = configProvider.get();
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
        FileSystemUtils.appendToFile(notePath, noteContent);
      } catch (IOException e) {
        LOGGER.error("Failed to write note {}", notePath, e);
      }
    }
  }

  private DistributionRule getFirstMatchedRule(PersonalAssistantConfiguration config, Message message) {
    return config.getDistributionRules().stream()
        .filter(dr -> dr.getMessageFilter().match(message))
        .findFirst()
        .orElse(null);
  }

}
