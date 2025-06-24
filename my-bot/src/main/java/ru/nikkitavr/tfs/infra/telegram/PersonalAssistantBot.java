package ru.nikkitavr.tfs.infra.telegram;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nikkitavr.tfs.infra.telegram.exception.UIException;
import ru.nikkitavr.tfs.infra.telegram.exception.WrongCommandUsageException;
import ru.nikkitavr.tfs.model.personalassistant.message.Message;
import ru.nikkitavr.tfs.model.personalassistant.message.TelegramChat;
import ru.nikkitavr.tfs.model.personalassistant.message.TelegramUser;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Audio;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Document;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Photo;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Video;
import ru.nikkitavr.tfs.model.personalassistant.message.files.VideoCircle;
import ru.nikkitavr.tfs.model.personalassistant.message.files.Voice;
import ru.nikkitavr.tfs.model.telegram.BotCommand;
import static ru.nikkitavr.tfs.model.telegram.BotReactions.EDITED_MESSAGE_PROCESSED_EMOJI;
import static ru.nikkitavr.tfs.model.telegram.BotReactions.NEW_MESSAGE_PROCESSED_EMOJI;
import ru.nikkitavr.tfs.model.telegram.BotUpdateType;
import ru.nikkitavr.tfs.service.PersonalAssistantService;
import ru.nikkitavr.tfs.service.telegram.TopicTitleService;
import ru.nikkitavr.tfs.utils.TimeUtils;

@Component
public class PersonalAssistantBot extends TelegramLongPollingBot {
    private final static Logger LOGGER = LoggerFactory.getLogger(PersonalAssistantBot.class);

    private final String botUsername;
    private final PersonalAssistantService personalAssistantService;
    private final TopicTitleService topicTitleService;
    private BotSession botSession;


    @Autowired
    public PersonalAssistantBot(
        @Value("${telegram.bot.token}") String botToken,
        @Value("${telegram.bot.username}") String botUsername,
        PersonalAssistantService personalAssistantService, TopicTitleService topicTitleService
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.personalAssistantService = personalAssistantService;
        this.topicTitleService = topicTitleService;
    }

    @PostConstruct
    public void start() throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        this.botSession = api.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        org.telegram.telegrambots.meta.api.objects.Message message;
        BotUpdateType updateType;

        if (update.hasMessage()) {
            message = update.getMessage();
            updateType = BotUpdateType.NEW_MESSAGE;
        } else if (update.hasEditedMessage()) {
            message = update.getEditedMessage();
            updateType = BotUpdateType.MESSAGE_EDITED;
        } else if (update.hasEditedChannelPost()) {
            message = update.getEditedChannelPost();
            updateType = BotUpdateType.MESSAGE_EDITED;
        } else {
            return;
        }

        handleUpdate(message, updateType);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    private void handleUpdate(org.telegram.telegrambots.meta.api.objects.Message message, BotUpdateType updateType) {
        try {
            if (checkForCommands(message)) {
                return;
            }

            personalAssistantService.processMessage(toAssistantMessage(message));
            sendReaction(
                message.getChatId(),
                message.getMessageThreadId(),
                updateType == BotUpdateType.NEW_MESSAGE ?
                    NEW_MESSAGE_PROCESSED_EMOJI :
                    EDITED_MESSAGE_PROCESSED_EMOJI
            );
        } catch (UIException e) {
            sendMessage(message.getChatId(), message.getMessageThreadId(), e.getMessage());
        } catch (Exception e) {
            sendMessage(message.getChatId(), message.getMessageThreadId(), "Some unhandled error: \n\nMessage: %s \n\nCause: %s \n\nStackTrace: %s"
                .formatted(e.getMessage(), e.getCause(), Arrays.toString(e.getStackTrace())));
            LOGGER.error("Some unhandled error", e);
        }
    }


    public boolean checkForCommands(org.telegram.telegrambots.meta.api.objects.Message message) throws WrongCommandUsageException {
        if(message.getEntities() == null) {
            return false;
        }

        for (MessageEntity entity : message.getEntities()) {
            if (entity != null && entity.getOffset() == 0 && EntityType.BOTCOMMAND.equals(entity.getType())) {
                BotCommand command = BotCommand.fromText(message.getText());
                if(command.qualifier() == null || command.qualifier().equals(getBotUsername())) {
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

    public Message toAssistantMessage(org.telegram.telegrambots.meta.api.objects.Message botMessage) {
        Message msg = new Message()
            .setMessageId(botMessage.getMessageId())
            .setMediaGroupId(botMessage.getMediaGroupId())
            .setDate(TimeUtils.toInstant(botMessage.getDate()))
            .setText(botMessage.getText())
            .setCaption(botMessage.getCaption())
            .setMessageThreadId(botMessage.getMessageThreadId())
            .setEditDate(TimeUtils.toInstant(botMessage.getEditDate()))
            .setForwardFromMessageId(botMessage.getForwardFromMessageId())
            .setForwardDate(TimeUtils.toInstant(botMessage.getForwardDate()))
            .setForwardSenderName(botMessage.getForwardSenderName())
            .setIsTopicMessage(botMessage.getIsTopicMessage())
            .setForwardSignature(botMessage.getForwardSignature());

        // Обработка отправителя (from)
        if (botMessage.getFrom() != null) {
            TelegramUser from = new TelegramUser();
            from.setId(botMessage.getFrom().getId());
            from.setFirstName(botMessage.getFrom().getFirstName());
            from.setLastName(botMessage.getFrom().getLastName());
            from.setUserName(botMessage.getFrom().getUserName());
            msg.setFrom(from);
        }

        // Обработка чата
        if (botMessage.getChat() != null) {
            TelegramChat chat = new TelegramChat();
            chat.setId(botMessage.getChat().getId());
            chat.setTitle(botMessage.getChat().getTitle());
            chat.setUserName(botMessage.getChat().getUserName());
            msg.setChat(chat);
        }

        // Обработка пересланного отправителя
        if (botMessage.getForwardFrom() != null) {
            TelegramUser forwardFrom = new TelegramUser();
            forwardFrom.setId(botMessage.getForwardFrom().getId());
            forwardFrom.setFirstName(botMessage.getForwardFrom().getFirstName());
            forwardFrom.setLastName(botMessage.getForwardFrom().getLastName());
            forwardFrom.setUserName(botMessage.getForwardFrom().getUserName());
            msg.setForwardFrom(forwardFrom);
        }

        // Обработка пересланного чата
        if (botMessage.getForwardFromChat() != null) {
            TelegramChat forwardFromChat = new TelegramChat();
            forwardFromChat.setId(botMessage.getForwardFromChat().getId());
            forwardFromChat.setTitle(botMessage.getForwardFromChat().getTitle());
            forwardFromChat.setUserName(botMessage.getForwardFromChat().getUserName());
            msg.setForwardFromChat(forwardFromChat);
        }

        // Обработка отправителя-чата
        if (botMessage.getSenderChat() != null) {
            TelegramChat senderChat = new TelegramChat();
            senderChat.setId(botMessage.getSenderChat().getId());
            senderChat.setTitle(botMessage.getSenderChat().getTitle());
            senderChat.setUserName(botMessage.getSenderChat().getUserName());
            msg.setSenderChat(senderChat);
        }

        // Обработка ответного сообщения
        if (botMessage.getReplyToMessage() != null) {
            // Рекурсивно конвертируем ответное сообщение
            Message replyMessage = toAssistantMessage(botMessage.getReplyToMessage());
            msg.setReplyToMessage(replyMessage);
        }

        // Обработка голосовых сообщений
        if (botMessage.getVoice() != null) {
            Voice voice = new Voice();
            voice.setFileId(botMessage.getVoice().getFileId());
            voice.setFileUniqueId(botMessage.getVoice().getFileUniqueId());
            voice.setMimeType(botMessage.getVoice().getMimeType());
            msg.setVoice(voice);
        }

        // Обработка фотографий
        if (botMessage.getPhoto() != null && !botMessage.getPhoto().isEmpty()) {
            Photo photo = new Photo();
            // Берем последний элемент (наилучшее качество)
            org.telegram.telegrambots.meta.api.objects.PhotoSize bestPhoto = 
                botMessage.getPhoto().get(botMessage.getPhoto().size() - 1);
            photo.setFileId(bestPhoto.getFileId());
            photo.setFileUniqueId(bestPhoto.getFileUniqueId());
            msg.setPhoto(photo);
        }

        // Обработка документов
        if (botMessage.getDocument() != null) {
            Document document = new Document();
            document.setFileId(botMessage.getDocument().getFileId());
            document.setFileUniqueId(botMessage.getDocument().getFileUniqueId());
            document.setFileName(botMessage.getDocument().getFileName());
            document.setMimeType(botMessage.getDocument().getMimeType());
            msg.setDocument(document);
        }

        // Обработка видео
        if (botMessage.getVideo() != null) {
            Video video = new Video();
            video.setFileId(botMessage.getVideo().getFileId());
            video.setFileUniqueId(botMessage.getVideo().getFileUniqueId());
            video.setMimeType(botMessage.getVideo().getMimeType());
            video.setFileName(botMessage.getVideo().getFileName());
            msg.setVideo(video);
        }

        // Обработка аудио
        if (botMessage.getAudio() != null) {
            Audio audio = new Audio();
            audio.setFileId(botMessage.getAudio().getFileId());
            audio.setFileUniqueId(botMessage.getAudio().getFileUniqueId());
            audio.setMimeType(botMessage.getAudio().getMimeType());
            audio.setFileName(botMessage.getAudio().getFileName());
            msg.setAudio(audio);
        }

        // Обработка видеокружков (video note)
        if (botMessage.getVideoNote() != null) {
            VideoCircle videoCircle = new VideoCircle();
            videoCircle.setFileId(botMessage.getVideoNote().getFileId());
            videoCircle.setFileUniqueId(botMessage.getVideoNote().getFileUniqueId());
            msg.setVideoCircle(videoCircle);
        }

        // Обработка топиков
        if (botMessage.getIsTopicMessage() != null && botMessage.getIsTopicMessage()) {
            Optional<String> title = topicTitleService.getTitleForTopic(botMessage.getChatId(), botMessage.getMessageThreadId());
            if (title.isEmpty()) {
                sendMessage(botMessage.getChatId(), botMessage.getMessageThreadId(),
                    "Please specify the title for this topic using command %s <title>"
                        .formatted(PersonalAssistantBotCommand.SET_TOPIC_TITLE.getValue())
                );
            } else {
                msg.setMessageThreadTitle(title.get());
            }
        }

        return msg;
    }


    private void sendMessage(Long chatId, String text) {
        sendMessage(chatId, null, text, null);
    }

    private void sendMessage(Long chatId, String text, Integer replyToMessageId) {
        sendMessage(chatId, null, text, replyToMessageId);
    }

    private void sendMessage(Long chatId, Integer threadId, String text) {
        sendMessage(chatId, threadId, text, null);
    }

    private void sendMessage(Long chatId, Integer threadId, String text, Integer replyToMessageId) {
        try {
            execute(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .messageThreadId(threadId)
                .text(text)
                .replyToMessageId(replyToMessageId)
                .build()
            );
        } catch (TelegramApiException e) {
            LOGGER.error("Error on send message to chat: chatId={}, threadId={}, text={}, replyToMessageId={}",
                chatId,
                threadId,
                text,
                replyToMessageId,
                e
            );
        }
    }

    private void sendReaction(Long chatId, Integer messageId, String emoji) {
        try {
            execute(SetMessageReaction.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .reactionTypes(List.of(ReactionTypeEmoji.builder().emoji(emoji).build()))
                .build()
            );
        } catch (TelegramApiException e) {
            LOGGER.error("Error on send message to chat: chatId={}, messageId={}, emoji={}",
                chatId,
                messageId,
                emoji,
                e
            );
        }
    }
}
