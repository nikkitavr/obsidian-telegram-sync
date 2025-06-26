package ru.nikkitavr.notesassistant.model.personalassistant.message.files;

import java.io.InputStream;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractTelegramFile {
  private String fileId;
  private String fileUniqueId;
  private InputStream file;


}
