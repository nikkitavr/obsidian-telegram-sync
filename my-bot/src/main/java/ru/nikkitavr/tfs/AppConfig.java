package ru.nikkitavr.tfs;

public class AppConfig {
    public BotConfig bot = new BotConfig();
    public FsConfig fs = new FsConfig();

    public static class BotConfig {
        public String token;
    }

    public static class FsConfig {
        public String rootDir = "notes";
    }
}
