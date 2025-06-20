package ru.nikkitavr.tfs;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.nikkitavr.tfs.bot.BotProperties;
import ru.nikkitavr.tfs.service.FileService;
import ru.nikkitavr.tfs.service.MyBotService;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.nio.file.Path;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MyBotServiceTest {

    @Test
    public void ignoresMessagesWithoutText() {
        BotProperties props = new BotProperties().setRootDirectory("notes");
        FileService fs = mock(FileService.class);
        MyBotService svc = new MyBotService(props, fs);

        Message msg = new Message();
        msg.setDate((int) Instant.now().getEpochSecond());
        msg.setChatId(1L);

        svc.processMessage(msg);
        verify(fs, never()).saveMessage(any(), any());
    }

    @Test
    public void savesTextMessage() throws Exception {
        BotProperties props = new BotProperties().setRootDirectory("notes");
        FileService fs = mock(FileService.class);
        MyBotService svc = new MyBotService(props, fs);

        Message msg = new Message();
        msg.setDate((int) Instant.now().getEpochSecond());
        msg.setChatId(1L);
        msg.setText("hi");
        User user = new User();
        user.setFirstName("tester");
        msg.setFrom(user);

        svc.processMessage(msg);
        verify(fs).saveMessage(Path.of("notes"), msg);
    }
}
