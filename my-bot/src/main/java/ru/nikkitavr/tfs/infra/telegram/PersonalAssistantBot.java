package ru.nikkitavr.tfs.infra.telegram;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nikkitavr.tfs.model.personalassistant.Message;
import ru.nikkitavr.tfs.model.telegram.BotCommand;
import ru.nikkitavr.tfs.service.PersonalAssistantService;
import ru.nikkitavr.tfs.service.telegram.TopicTitleService;

@Component
public class PersonalAssistantBot extends TelegramLongPollingBot {
    private BotSession botSession;
    private final String botUsername;
    private final PersonalAssistantService personalAssistantService;
    private final TopicTitleService topicTitleService;


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
        if (update.hasMessage()) {
            try {
                var message = update.getMessage();
                if (checkForCommands(message)) {
                    return;
                }

                toAssistantMessage(update.getMessage());
                //personalAssistantService.processMessage(toAssistantMessage(update.getMessage()));
            } catch (Exception e) {
                //todo: send exception message to chat or group or etc. where message comes from
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public Message toAssistantMessage(org.telegram.telegrambots.meta.api.objects.Message botMessage) {
        Message msg = new Message()
            .setMessageId(botMessage.getMessageId())
            .setMediaGroupId(botMessage.getMediaGroupId())
            .setDate(toInstant(botMessage.getDate()))


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
            .setEditDate(toInstant(botMessage.getEditDate()))
            .setForwardFromMessageId(botMessage.getForwardFromMessageId())
            //.setChat()
            //.setForwardFrom()
            //.setForwardFromChat()
            .setForwardDate(toInstant(botMessage.getForwardDate()))
            .setForwardSenderName(botMessage.getForwardSenderName())
            //.setSenderChat()
            .setIsTopicMessage(botMessage.getIsTopicMessage());

        System.out.println(msg);


        System.out.println("debug");
        return msg;
    }

    public Instant toInstant(Integer unixTime) {
        if (unixTime == null) {
            return null;
        }
        return Instant.ofEpochSecond(unixTime);
    }

    public boolean checkForCommands(org.telegram.telegrambots.meta.api.objects.Message message) {
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

    public void handleCommand(BotCommand command, org.telegram.telegrambots.meta.api.objects.Message message) {
        switch (PersonalAssistantBotCommand.fromValue(command.command())) {
            case SET_TOPIC_TITLE -> {
                if (!message.getIsTopicMessage()) {
                    throw new IllegalArgumentException("SET_TOPIC_TITLE only for topics");
                }
                topicTitleService.setTitleForTopic(message.getChat().getId(), message.getMessageThreadId(), command.arguments());
            }
        }
    }
}
