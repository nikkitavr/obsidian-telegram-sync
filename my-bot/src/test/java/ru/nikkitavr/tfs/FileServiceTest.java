package ru.nikkitavr.tfs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nikkitavr.tfs.service.FileService;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class FileServiceTest {
    @TempDir
    Path tempDir;

    @Test
    public void writesMessageToFile() throws Exception {
        FileService fs = new FileService();
        Message msg = new Message();
        msg.setDate((int) Instant.now().getEpochSecond());
        msg.setText("hello");
        User user = new User();
        user.setFirstName("tester");
        msg.setFrom(user);
        msg.setChatId(123L);

        fs.saveMessage(tempDir, msg);

        Path notePath = tempDir
            .resolve("123")
            .resolve(java.time.LocalDate.now().toString() + ".md");
        assertTrue(Files.exists(notePath), "note file created");
        String content = Files.readString(notePath);
        assertTrue(content.contains("tester: hello"));
    }
}
