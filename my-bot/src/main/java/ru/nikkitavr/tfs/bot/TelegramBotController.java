package ru.nikkitavr.tfs.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nikkitavr.tfs.service.MyBotService;

@Component
public class TelegramBotController extends TelegramLongPollingBot {
    private final MyBotService myBotService;

    @Autowired
    public TelegramBotController(BotProperties botProperties, MyBotService myBotService) {
        super(botProperties.getToken());
        this.myBotService = myBotService;
    }

    //TODO: uncomment when tg integration will be ready
    //@PostConstruct
    public void start() throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
                myBotService.processMessage(update.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }
}
