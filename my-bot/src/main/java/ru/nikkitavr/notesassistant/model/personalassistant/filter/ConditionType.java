package ru.nikkitavr.notesassistant.model.personalassistant.filter;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.lang3.function.TriFunction;
import ru.nikkitavr.notesassistant.infra.telegram.exception.UIException;
import ru.nikkitavr.notesassistant.model.personalassistant.message.Message;
import ru.nikkitavr.notesassistant.model.personalassistant.message.TelegramChat;
import ru.nikkitavr.notesassistant.model.personalassistant.message.TelegramUser;
import ru.nikkitavr.notesassistant.model.personalassistant.message.files.VideoCircle;
import ru.nikkitavr.notesassistant.model.personalassistant.message.files.Voice;
import static ru.nikkitavr.notesassistant.utils.CommonUtils.mergeToFullName;

public enum ConditionType {
  ALL("all", ((message, operation, value) -> true)),

  USER("user", ((message, operation, value) -> {
    TelegramUser user = Optional.ofNullable(message).map(Message::getFrom).orElse(null);
    if (user == null) {
      return false;
    }

    String firstName = user.getFirstName();
    String lastName = user.getLastName();
    String fullName = mergeToFullName(firstName, lastName);
    String userId = String.valueOf(user.getId());
    String username = user.getUserName();
    return operation.anyLeftMatch(Arrays.asList(username, userId, firstName, lastName, fullName), value);
  })),

  CHAT("chat", ((message, operation, value) -> {
    TelegramChat chat = Optional.ofNullable(message).map(Message::getChat).orElse(null);
    if (chat == null) {
      return false;
    }
    String chatId = String.valueOf(chat.getId());
    String chatTitle = chat.getTitle();
    String chatUsername = chat.getUserName();
    return operation.anyLeftMatch(Arrays.asList(chatId, chatTitle, chatUsername), value);
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
    // 1. Если есть forwarded user
    if (message.getForwardFrom() != null) {
      TelegramUser fwdUser = message.getForwardFrom();
      String firstName = fwdUser.getFirstName();
      String lastName = fwdUser.getLastName();
      String fullName = mergeToFullName(firstName, lastName);
      String userId = String.valueOf(fwdUser.getId());
      String username = fwdUser.getUserName();
      return operation.anyLeftMatch(Arrays.asList(username, userId, firstName, lastName,  fullName), value);
    }
    // 2. Если есть forwarded chat (канал)
    if (message.getForwardFromChat() != null) {
      TelegramChat fwdChat = message.getForwardFromChat();
      String chatId = String.valueOf(fwdChat.getId());
      String chatTitle = fwdChat.getTitle();
      String chatTitleWithForwardSignature = chatTitle + (message.getForwardSignature() != null ? " (%s)".formatted(message.getForwardSignature()) : "");
      String chatUsername = fwdChat.getUserName();
      return operation.anyLeftMatch(Arrays.asList(chatId, chatTitle, chatTitleWithForwardSignature, chatUsername), value);
    }
    // 3. Если есть forwardedSenderName
    if (message.getForwardSenderName() != null) {
      return operation.match(message.getForwardSenderName(), value);
    }
    // 4. Fallback: from (анонимные админы и т.д.)
    if (message.getFrom() != null) {
      TelegramUser from = message.getFrom();
      String firstName = from.getFirstName();
      String lastName = from.getLastName();
      String fullName = mergeToFullName(firstName, lastName);
      String userId = String.valueOf(from.getId());
      String username = from.getUserName();
      return operation.anyLeftMatch(Arrays.asList(username, userId, firstName, lastName, fullName), value);
    }
    return false;
  })),

  CONTENT("content", ((message, operation, value) -> {
    if (message == null) {
      return false;
    }

    String text = message.getText();
    String caption = message.getCaption();
    return operation.anyLeftMatch(Arrays.asList(text, caption), value);
  })),

  VOICE_TRANSCRIPT("voiceTranscript", ((message, operation, value) -> {
    if (message == null) {
      return false;
    }
    String voiceTranscript = Optional.ofNullable(message.getVoice()).map(Voice::getTranscription).orElse(null);
    String videoCircleTranscript = Optional.ofNullable(message.getVideoCircle()).map(VideoCircle::getTranscription).orElse(null);

    return operation.anyLeftMatch(Arrays.asList(voiceTranscript, videoCircleTranscript), value);
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
    if ((operation == null || value == null) && this != ALL) {
      throw new UIException("OperationConfig and value must be not null for condition type: " + this.name);
    }
    return matcher.apply(message, operation, value);
  }
}
