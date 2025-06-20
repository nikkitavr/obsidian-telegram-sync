package ru.nikkitavr.tfs.fs;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileService {
    private final Path root;

    public FileService(Path root) {
        this.root = root;
    }

    public void saveMessage(Message message) throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
        LocalDate date = LocalDate.now();
        Path note = root.resolve(date.format(DateTimeFormatter.ISO_DATE) + ".md");
        String user = message.getFrom() != null ? message.getFrom().getFirstName() : "unknown";
        String line = String.format("%s: %s%n", user, message.getText());
        Files.writeString(note, line, StandardCharsets.UTF_8, Files.exists(note) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
    }
}
