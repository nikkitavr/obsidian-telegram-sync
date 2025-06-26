package ru.nikkitavr.notesassistant.model.personalassistant.message.files;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Document extends AbstractTelegramFile {
  private String fileName;
  private String mimeType;
}
