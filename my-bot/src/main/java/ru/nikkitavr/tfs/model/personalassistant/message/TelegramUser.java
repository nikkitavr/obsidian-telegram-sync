package ru.nikkitavr.tfs.model.personalassistant.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramUser {
  private Long id;
  private String firstName;
  private String lastName;
  private String userName;
}
