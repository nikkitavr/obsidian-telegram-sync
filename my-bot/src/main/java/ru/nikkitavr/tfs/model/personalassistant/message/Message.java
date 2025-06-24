package ru.nikkitavr.tfs.model.personalassistant.message;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Audio;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Document;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Photo;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Video;
import ru.nikkitavr.tfs.model.personalassistant.message.files.VideoCircle;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Voice;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Message {

  //message content
  /**
   * Optional.
   * For text messages, the actual UTF-8 text of the message
   */
  private String text;
  /**
   * Optional.
   * Message is a general file, information about the file
   */
  private Document document;
  /**
   * Optional.
   * Message is a photo, available sizes of the photo
   */
  private Photo photo;
  /**
   * Optional.
   * Message is a video, information about the video
   */
  private Video video;
  /**
   * Optional.
   * Message is an audio file, information about the file
   */
  private Audio audio;
  /**
   *Caption for the document, photo or video, 0-200 characters
   */
  private String caption;
  /**
   * Optional.
   * Message is a voice, information about the voice message
   */
  private Voice voice;
  /**
   * Optional.
   * Message is a video note, information about the video message
   */
  private VideoCircle videoCircle;
  /**
   * Date the message was sent in Unix time
   */
  private Instant date;

  //tg meta info
  /**
   * Integer	Unique message identifier
   */
  private Integer messageId;
  /**
   * Optional.
   * The unique identifier of a media message group this message belongs to
   */
  private String mediaGroupId;
  /**
   * Optional.
   * Sender, can be empty for messages sent to channels
   */
  private TelegramUser from;
  /**
   * Conversation the message belongs to
   */
  private TelegramChat chat;
  /**
   * Optional.
   * True, if the message is sent to a forum topic
   */
  private Boolean isTopicMessage;
  /**
   * Optional.
   * Unique identifier of a message thread or a forum topic to which the message belongs;
   * for supergroups only
   */
  private Integer messageThreadId;
  //Подставим из базы при чтении сообщения
  private String messageThreadTitle;
  /**
   * Optional.
   * Date the message was last edited in Unix time
   */
  private Instant editDate;
  private Message replyToMessage;

  /**
   * Optional.
   * For forwarded messages, sender of the original message
   */
  private TelegramUser forwardFrom;
  /**
   * Optional.
   * Sender's name for messages forwarded from users who disallow adding a link to their account in forwarded messages.
   */
  private String forwardSenderName;
  /**
   * Optional.
   * Post author signature for messages forwarded from channel chats
   */
  private String forwardSignature;
  /**
   * Optional.
   * For messages forwarded from channels or from anonymous administrators, information about the original sender chat
   */
  private TelegramChat forwardFromChat;
  /**
   * Optional.
   * For forwarded channel posts, identifier of the original message in the channel
   */
  private Integer forwardFromMessageId;
  /**
   * Optional.
   * For forwarded messages, date the original message was sent
   */
  private Instant forwardDate;

  /**
   * Optional.
   * Sender of the message, sent on behalf of a chat. The channel itself for channel messages.
   * The supergroup itself for messages from anonymous group administrators.
   * The linked channel for messages automatically forwarded to the discussion group
   */
  private TelegramChat senderChat;

}
