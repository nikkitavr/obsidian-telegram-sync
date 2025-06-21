package ru.nikkitavr.tfs.model.telegram;


public record BotCommand (
  String command,
  String qualifier,
  String arguments
) {

  public static BotCommand fromText(String text) {
    String command = null, qualifier = null, arguments = null;
    String[] parts;

    parts = text.split(" ", 2);
    String commandBody = parts[0];
    if(parts.length == 2) {
      arguments = parts[1];
    }

    parts = commandBody.split("@", 2);
    command = parts[0];
    if(parts.length == 2) {
      qualifier = parts[1];
    }

    return new BotCommand(command, qualifier, arguments);
  }
}
