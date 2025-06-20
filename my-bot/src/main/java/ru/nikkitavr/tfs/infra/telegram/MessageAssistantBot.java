package ru.nikkitavr.tfs.infra.telegram;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nikkitavr.tfs.model.Message;
import ru.nikkitavr.tfs.service.MessageAssistantService;

@Component
public class MessageAssistantBot extends TelegramLongPollingBot {
    private final MessageAssistantService messageAssistantService;

    @Autowired
    public MessageAssistantBot(@Value("${telegram.bot.token}") String token, MessageAssistantService messageAssistantService) {
        super(token);
        this.messageAssistantService = messageAssistantService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
                messageAssistantService.processMessage(toAssistantMessage(update.getMessage()));
            } catch (Exception e) {
                //todo: send exception message to chat or group or etc. where message comes from
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    public Message toAssistantMessage(org.telegram.telegrambots.meta.api.objects.Message botMessage) {
        throw new NotImplementedException();
    }
}
