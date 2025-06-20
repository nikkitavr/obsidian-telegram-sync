package ru.nikkitavr.tfs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nikkitavr.tfs.bot.BotProperties;

import java.nio.file.Path;

import ru.nikkitavr.tfs.service.FileService;

@Component
public class MyBotService {
  private final BotProperties botProperties;
  private final FileService fileService;

  @Autowired
  public MyBotService(BotProperties botProperties, FileService fileService) {
    this.botProperties = botProperties;
    this.fileService = fileService;
  }

  public void processMessage(Message message) {
    if (!message.hasText()) {
      return;
    }
    Path root = Path.of(botProperties.getRootDirectory());
    try {
      fileService.saveMessage(root, message);
    } catch (Exception e) {
      // for now just print stacktrace, later add logging
      e.printStackTrace();
    }
  }
}
