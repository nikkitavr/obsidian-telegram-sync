package ru.nikkitavr.tfs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileService {

    public void saveMessage(Path path, String content) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        //TODO: rewrite logic below to match new FileService api...
        /*LocalDate date = LocalDate.now();
        Path note = path.resolve(date.format(DateTimeFormatter.ISO_DATE) + ".md");
        String user = message.getFrom() != null ? message.getFrom().getFirstName() : "unknown";
        String line = String.format("%s: %s%n", user, message.getText());
        Files.writeString(note, line, StandardCharsets.UTF_8, Files.exists(note) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);*/
    }
}
