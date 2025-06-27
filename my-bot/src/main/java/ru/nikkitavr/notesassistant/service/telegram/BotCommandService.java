package ru.nikkitavr.notesassistant.service.telegram;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import ru.nikkitavr.notesassistant.infra.telegram.PersonalAssistantBotCommand;
import ru.nikkitavr.notesassistant.infra.telegram.exception.WrongCommandUsageException;
import ru.nikkitavr.notesassistant.model.bot.BotCommand;

@Service public class BotCommandService {

  private final TopicTitleService topicTitleService;

  @Autowired
  public BotCommandService(TopicTitleService topicTitleService) {
    this.topicTitleService = topicTitleService;
  }

  public boolean checkForCommands(org.telegram.telegrambots.meta.api.objects.Message message, String botUsername) throws WrongCommandUsageException {
    if(message.getEntities() == null) {
      return false;
    }

    for (MessageEntity entity : message.getEntities()) {
      if (entity != null && entity.getOffset() == 0 && EntityType.BOTCOMMAND.equals(entity.getType())) {
        BotCommand command = BotCommand.fromText(message.getText());
        if(command.qualifier() == null || command.qualifier().equals(botUsername)) {
          handleCommand(command, message);
        }
        return true;
      }
    }
    return false;
  }

  public void handleCommand(BotCommand command, org.telegram.telegrambots.meta.api.objects.Message message) throws WrongCommandUsageException {
    switch (PersonalAssistantBotCommand.fromValue(command.command())) {
      case SET_TOPIC_TITLE -> {
        if (!message.getIsTopicMessage()) {
          throw new WrongCommandUsageException("This command should only be used in topics");
        }
        if (StringUtils.isBlank(command.arguments())) {
          throw new WrongCommandUsageException("Topic name is missing. " +
              "Provide it after command: `/cmnd@bot <topicName>` or `/cmnd <topicName>`");
        }
        topicTitleService.setTitleForTopic(message.getChat().getId(), message.getMessageThreadId(), command.arguments());
      }
    }
  }
}
