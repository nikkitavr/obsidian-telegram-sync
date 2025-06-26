package ru.nikkitavr.notesassistant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nikkitavr.notesassistant.model.personalassistant.PersonalAssistantConfiguration;
import static ru.nikkitavr.notesassistant.model.personalassistant.PersonalAssistantConfiguration.DistributionRule;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;

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

    System.out.println(rule);

    //TODO: на этом этапе реализован парсинг конфигурации и получение DistributionRule по фильтру.
    // Теперь надо реализовать создание (или дополнение) md файла в хранилище на основе distribution rules, шаблонных переменных, шаблона файла
    // и поддержки других функциональных требований, таких как закачка файлов и их сохранение по указанному в конфигу пути (с прилинковой к md заметке),
    // поддержка транскрипции и др.

  }

  private DistributionRule getFirstMatchedRule(PersonalAssistantConfiguration config, Message message) {
    return config.getDistributionRules().stream()
        .filter(dr -> dr.getMessageFilter().match(message))
        .findFirst()
        .orElse(null);
  }
}
