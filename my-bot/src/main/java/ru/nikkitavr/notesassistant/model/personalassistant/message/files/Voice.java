package ru.nikkitavr.notesassistant.model.personalassistant.message.files;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Voice extends AbstractTelegramFile {
  private String transcription;
  private String mimeType;
}
