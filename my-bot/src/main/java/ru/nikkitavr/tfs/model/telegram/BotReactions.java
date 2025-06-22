package ru.nikkitavr.tfs.model.telegram;

import lombok.Getter;

@Getter
public enum BotReactions {
  NEW_MESSAGE_PROCESSED("🌚"),
  EDITED_MESSAGE_PROCESSED("🦄");

  private final String emoji;

  BotReactions(String emoji) {
    this.emoji = emoji;
  }
}
