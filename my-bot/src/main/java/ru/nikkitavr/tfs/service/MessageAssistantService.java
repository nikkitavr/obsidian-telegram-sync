package ru.nikkitavr.tfs.service;

import org.apache.commons.lang3.NotImplementedException;
import ru.nikkitavr.tfs.model.Message;
import ru.nikkitavr.tfs.model.MessageAssistantConfiguration;
import static ru.nikkitavr.tfs.model.MessageAssistantConfiguration.DistributionRule;
import ru.nikkitavr.tfs.utils.FileSystemUtils;

public class MessageAssistantService {
  private final MessageAssistantConfigurationProvider configProvider;
  private final FileSystemUtils fileSystemUtils;

  public MessageAssistantService(MessageAssistantConfigurationProvider configProvider, FileSystemUtils fileSystemUtils) {
    this.configProvider = configProvider;
    this.fileSystemUtils = fileSystemUtils;
  }

  public void processMessage(Message message) {
    //TODO: добавить обработку и сохранение сообщения на основании конфигурации MessageAssistantConfiguration и данных в Message. С учетом шаблонов.
    MessageAssistantConfiguration config = configProvider.get();
    DistributionRule distributionRule = getFirstMatchedRule(config, message);
    //TODO: После получения правила - нужно сохранить message как .md файл (или записать в существующий) с учетом настроек distributionRule

  }

  private DistributionRule getFirstMatchedRule(MessageAssistantConfiguration config, Message message) {
    //TODO: Метод должен вернуть первый DistributionRule из конфига, совпавший по полю messageFilterQuery с контентом сообщения на основе шаблона
    // см. ConditionType, ConditionOperation в obsidian-plugin
    throw new NotImplementedException();
  }

}
