package ru.nikkitavr.tfs.model.personalassistant.message.files;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Voice extends AbstractFile {
  private String transcription;
}
