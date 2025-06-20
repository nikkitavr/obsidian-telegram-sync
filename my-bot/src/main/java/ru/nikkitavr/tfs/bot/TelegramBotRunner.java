package ru.nikkitavr.tfs.bot;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBotRunner {
    private final TelegramBotController controller;

    public TelegramBotRunner(TelegramBotController controller) {
        this.controller = controller;
    }

    @PostConstruct
    public void start() throws Exception {
        String token = controller.getBotToken();
        if (token == null || token.isBlank() || token.equals("YOUR_BOT_TOKEN")) {
            System.out.println("Bot token not provided, bot not started");
            return;
        }
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(controller);
    }
}
