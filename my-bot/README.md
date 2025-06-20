# Telegram FS Sync

Simple Telegram bot that writes received text messages to Markdown files.

## Configuration

Edit `src/main/resources/application.yml` or provide a path to another YAML file as the first program argument.

```
bot:
  token: "YOUR_BOT_TOKEN"
fs:
  rootDir: "notes"
```

`rootDir` is the directory where Markdown files will be created. Messages from
each chat are stored in a subfolder named after the chat id.

## Run

```
mvn package
java -cp target/my-bot-1.0-SNAPSHOT.jar ru.nikkitavr.tfs.App
```
