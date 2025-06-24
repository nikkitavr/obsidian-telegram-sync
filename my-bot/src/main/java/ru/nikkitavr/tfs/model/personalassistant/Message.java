package ru.nikkitavr.tfs.model.personalassistant;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.VideoNote;
import org.telegram.telegrambots.meta.api.objects.Voice;

@Getter
@Setter
@ToString
@Accessors(chain = true)
//TODO: change api entities to model objects
public class Message {

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
   * Date the message was sent in Unix time
   */
  private Instant date;

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
  private List<PhotoSize> photo;
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
  private VideoNote videoCircle;

  //meta info
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
   * Sender, can be empty for messages sent to channels
   */
  private User from;
  /**
   * Optional.
   * Date the message was last edited in Unix time
   */
  private Instant editDate;
  /**
   * Optional.
   * For forwarded channel posts, identifier of the original message in the channel
   */
  private Integer forwardFromMessageId;
  /**
   * Conversation the message belongs to
   */
  private Chat chat;
  /**
   * Optional.
   * For forwarded messages, sender of the original message
   */
  private User forwardFrom;
  /**
   * Optional.
   * For messages forwarded from channels or from anonymous administrators, information about the original sender chat
   */
  private Chat forwardFromChat;
  /**
   * Optional.
   * For forwarded messages, date the original message was sent
   */
  private Instant forwardDate;
  private Message replyToMessage;
  /**
   * Optional.
   * Sender's name for messages forwarded from users who disallow adding a link to their account in forwarded messages.
   */
  private String forwardSenderName;
  /**
   * Optional.
   * Sender of the message, sent on behalf of a chat. The channel itself for channel messages.
   * The supergroup itself for messages from anonymous group administrators.
   * The linked channel for messages automatically forwarded to the discussion group
   */
  private Chat senderChat;
  /**
   * Optional.
   * True, if the message is sent to a forum topic
   */
  private Boolean isTopicMessage;

  //additional //TODO: понять что еще нужно добавить и где взять
  private String voiceTranscription;
}
