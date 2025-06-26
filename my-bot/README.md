# Telegram FS Sync

Simple Telegram bot that writes received text messages to Markdown files.

## Configuration

Edit `src/main/resources/application.yml` or provide a path to another YAML file as the first program argument.

```
telegram:
  bot:
    token: "YOUR_BOT_TOKEN"
    rootDirectory: "notes"
```

`rootDirectory` is the folder where Markdown files will be created. Messages from
each chat are stored in a subfolder named after the chat id.

## Run

```
mvn package
java -cp target/my-bot-1.0-SNAPSHOT.jar ru.nikkitavr.notesassistant.App
```
