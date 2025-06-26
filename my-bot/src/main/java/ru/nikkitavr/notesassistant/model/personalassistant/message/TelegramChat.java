package ru.nikkitavr.notesassistant.model.personalassistant.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramChat {
  private Long id;
  private String title;
  private String userName;
}
