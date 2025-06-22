package ru.nikkitavr.tfs.infra.telegram;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
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
import ru.nikkitavr.tfs.model.personalassistant.Message;
import ru.nikkitavr.tfs.model.telegram.BotCommand;
import static ru.nikkitavr.tfs.model.telegram.BotReactions.EDITED_MESSAGE_PROCESSED;
import static ru.nikkitavr.tfs.model.telegram.BotReactions.NEW_MESSAGE_PROCESSED;
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
                    NEW_MESSAGE_PROCESSED.getEmoji() :
                    EDITED_MESSAGE_PROCESSED.getEmoji()
            );
        } catch (UIException e) {
            sendMessage(message.getChatId(), message.getMessageThreadId(), e.getMessage());
        } catch (Exception e) {
            sendMessage(message.getChatId(), message.getMessageThreadId(), Arrays.toString(e.getStackTrace()));
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
            //.setVoice(downloadFile())
            //.setVoiceTranscription()
            //.setVideoCircle()
            //.setDocument()
            //.setPhoto()
            //.setVideo()
            //.setAudio()

            .setMessageThreadId(botMessage.getMessageThreadId())
            //.setFrom()
            .setEditDate(TimeUtils.toInstant(botMessage.getEditDate()))
            .setForwardFromMessageId(botMessage.getForwardFromMessageId())
            //.setChat()
            //.setForwardFrom()
            //.setForwardFromChat()
            .setForwardDate(TimeUtils.toInstant(botMessage.getForwardDate()))
            .setForwardSenderName(botMessage.getForwardSenderName())
            //.setSenderChat()
            .setIsTopicMessage(botMessage.getIsTopicMessage());

        System.out.println(msg);


        System.out.println("debug");
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
