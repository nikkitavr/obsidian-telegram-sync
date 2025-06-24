package ru.nikkitavr.tfs.model.personalassistant.filter;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.lang3.function.TriFunction;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.nikkitavr.tfs.model.personalassistant.Message;

public enum ConditionType {
  ALL("all", ((message, operation, value) -> true)),

  USER("user", ((message, operation, value) -> {
    User user = Optional.ofNullable(message).map(Message::getFrom).orElse(null);
    if (user == null) {
      return false;
    }

    String firstName = user.getFirstName();
    String lastName = user.getLastName();
    String fullName = null;
    if (firstName != null) {
      fullName = firstName;
      if (lastName != null) {
        fullName += " " + lastName;
      }
    }
    
    String userId = String.valueOf(user.getId());
    String username = user.getUserName();
    return operation.anyLeftMatch(List.of(username, userId, fullName), value);
  })),

  CHAT("chat", ((message, operation, value) -> {
    Chat chat = Optional.ofNullable(message).map(Message::getChat).orElse(null);
    if (chat == null) {
      return false;
    }
    String chatId = String.valueOf(chat.getId());
    String chatTitle = chat.getTitle();
    String chatUsername = chat.getUserName();
    return operation.anyLeftMatch(List.of(chatId, chatTitle, chatUsername), value);
  })),

  TOPIC("topic", ((message, operation, value) -> {
    // title в Message подставим из базы при чтении сообщения в боте
    String topicTitle = Optional.ofNullable(message).map(Message::getMessageThreadTitle).orElse(null);
    return operation.match(topicTitle, value);
  })),

  FORWARD_FROM("forwardFrom", ((message, operation, value) -> {
    if (message == null) {
      return false;
    }
    // Аналог isForwardFromFiltered из obsidian-plugin
    // 1. Если есть forwarded user
    if (message.getForwardFrom() != null) {
      User fwdUser = message.getForwardFrom();
      String fullName = null;
      if (fwdUser.getFirstName() != null) {
        fullName = fwdUser.getFirstName();
        if (fwdUser.getLastName() != null) {
          fullName += " " + fwdUser.getLastName();
        }
      }
      String userId = String.valueOf(fwdUser.getId());
      String username = fwdUser.getUserName();
      return operation.anyLeftMatch(List.of(username, userId, fullName), value);
    }
    // 2. Если есть forwarded chat (канал)
    if (message.getForwardFromChat() != null) {
      Chat fwdChat = message.getForwardFromChat();
      String chatId = String.valueOf(fwdChat.getId());
      String chatTitle = fwdChat.getTitle();
      String chatUsername = fwdChat.getUserName();
      return operation.anyLeftMatch(List.of(chatId, chatTitle, chatUsername), value);
    }
    // 3. Если есть forwardedSenderName
    if (message.getForwardSenderName() != null) {
      return operation.match(message.getForwardSenderName(), value);
    }
    // 4. Fallback: from (анонимные админы и т.д.)
    if (message.getFrom() != null) {
      User from = message.getFrom();
      String fullName = null;
      if (from.getFirstName() != null) {
        fullName = from.getFirstName();
        if (from.getLastName() != null) {
          fullName += " " + from.getLastName();
        }
      }
      String userId = String.valueOf(from.getId());
      String username = from.getUserName();
      return operation.anyLeftMatch(List.of(username, userId, fullName), value);
    }
    return false;
  })),

  CONTENT("content", ((message, operation, value) -> {
    if (message == null) {
      return false;
    }

    String text = message.getText();
    String caption = message.getCaption();
    return operation.anyLeftMatch(List.of(text, caption), value);
  })),

  VOICE_TRANSCRIPT("voiceTranscript", ((message, operation, value) -> {
    String transcript = Optional.ofNullable(message).map(Message::getVoiceTranscription).orElse(null);
    return operation.match(transcript, value);
  }));

  @Getter
  private final String name;
  private final TriFunction<Message, ConditionOperation, String, Boolean> matcher;

  ConditionType(String name, TriFunction<Message, ConditionOperation, String, Boolean> matcher) {
    this.name = name;
    this.matcher = matcher;
  }

  public static ConditionType from(String name) {
    return Arrays.stream(ConditionType.values())
        .filter(cd -> cd.getName().equals(name))
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }

  public Boolean match(Message message, ConditionOperation operation, String value) {
    return matcher.apply(message, operation, value);
  }
}
