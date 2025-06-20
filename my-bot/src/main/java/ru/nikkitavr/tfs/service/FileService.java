package ru.nikkitavr.tfs.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.telegram.telegrambots.meta.api.objects.Message;

public class FileService {

    public void saveMessage(Path root, Message message) throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        LocalDate date = Instant.ofEpochSecond(message.getDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        Path chatDir = root.resolve(String.valueOf(message.getChatId()));
        if (!Files.exists(chatDir)) {
            Files.createDirectories(chatDir);
        }

        Path note = chatDir.resolve(date.format(DateTimeFormatter.ISO_DATE) + ".md");
        String user = message.getFrom() != null ? message.getFrom().getFirstName() : "unknown";
        String text = message.hasText() ? message.getText() : "";
        String line = String.format("%s: %s%n", user, text);
        Files.writeString(note, line, StandardCharsets.UTF_8,
                Files.exists(note) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
    }
}
