package ru.nikkitavr.tfs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nikkitavr.tfs.bot.BotProperties;

@Component
public class MyBotService {
  private final BotProperties botProperties;

  @Autowired
  public MyBotService(BotProperties botProperties) {
    this.botProperties = botProperties;
  }

  public void processMessage(Message message) {

  }
}
