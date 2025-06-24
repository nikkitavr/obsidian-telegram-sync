package ru.nikkitavr.tfs.model.personalassistant.message.files;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractFile {
  @Getter @Setter
  private InputStream file;
  private final Map<String, String> headers = new HashMap<>();

  private void addHeader(String headerKey, String headerValue) {
    headers.put(headerKey, headerValue);
  }

  private String getHeader(String headerKey) {
    return headers.get(headerKey);
  }
}
