package ru.nikkitavr.notesassistant.infra.telegram.client;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramMessageSender extends DefaultAbsSender {
  private static final Logger log = LoggerFactory.getLogger(TelegramMessageSender.class);

  protected TelegramMessageSender(@Value("${telegram.bot.token}") String botToken) {
    super(new DefaultBotOptions(), botToken);
  }

  public void sendMessage(Long chatId, String text) {
    sendMessage(chatId, null, text, null);
  }

  public void sendMessage(Long chatId, String text, Integer replyToMessageId) {
    sendMessage(chatId, null, text, replyToMessageId);
  }

  public void sendMessage(Long chatId, Integer threadId, String text) {
    sendMessage(chatId, threadId, text, null);
  }

  public void sendMessage(Long chatId, Integer threadId, String text, Integer replyToMessageId) {
    try {
      execute(SendMessage.builder()
          .chatId(String.valueOf(chatId))
          .messageThreadId(threadId)
          .text(text)
          .replyToMessageId(replyToMessageId)
          .build()
      );
    } catch (TelegramApiException e) {
      log.error("Error on send message to chat: chatId={}, threadId={}, text={}, replyToMessageId={}",
          chatId,
          threadId,
          text,
          replyToMessageId,
          e
      );
    }
  }

  public void sendReaction(Long chatId, Integer messageId, String emoji) {
    try {
      execute(SetMessageReaction.builder()
          .chatId(String.valueOf(chatId))
          .messageId(messageId)
          .reactionTypes(List.of(ReactionTypeEmoji.builder().emoji(emoji).build()))
          .build()
      );
    } catch (TelegramApiException e) {
      log.error("Error on send message to chat: chatId={}, messageId={}, emoji={}",
          chatId,
          messageId,
          emoji,
          e
      );
    }
  }
}
