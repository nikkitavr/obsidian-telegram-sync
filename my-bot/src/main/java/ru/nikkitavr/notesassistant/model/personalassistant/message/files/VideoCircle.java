package ru.nikkitavr.notesassistant.model.personalassistant.message.files;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoCircle extends AbstractTelegramFile {
  private String transcription;
}
