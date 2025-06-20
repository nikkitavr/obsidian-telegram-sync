package ru.nikkitavr.tfs.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nikkitavr.tfs.fs.FileService;

public class TelegramFsBot extends TelegramLongPollingBot {
    private final String token;
    private final FileService fileService;

    public TelegramFsBot(String token, FileService fileService) {
        this.token = token;
        this.fileService = fileService;
    }

    public void start() throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                fileService.saveMessage(update.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
