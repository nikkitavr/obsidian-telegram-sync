package ru.nikkitavr.tfs;

import org.yaml.snakeyaml.Yaml;
import ru.nikkitavr.tfs.bot.TelegramFsBot;
import ru.nikkitavr.tfs.fs.FileService;

import java.io.InputStream;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws Exception {
        String configPath = args.length > 0 ? args[0] : "application.yml";
        Yaml yaml = new Yaml();
        AppConfig config;
        try (InputStream in = App.class.getClassLoader().getResourceAsStream(configPath)) {
            if (in == null) {
                try (InputStream fileIn = java.nio.file.Files.newInputStream(Path.of(configPath))) {
                    config = yaml.loadAs(fileIn, AppConfig.class);
                }
            } else {
                config = yaml.loadAs(in, AppConfig.class);
            }
        }
        FileService fs = new FileService(Path.of(config.fs.rootDir));
        TelegramFsBot bot = new TelegramFsBot(config.bot.token, fs);
        bot.start();
    }
}
