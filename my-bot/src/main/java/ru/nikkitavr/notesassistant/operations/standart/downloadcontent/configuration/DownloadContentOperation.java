package ru.nikkitavr.notesassistant.operations.standart.downloadcontent.configuration;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import ru.nikkitavr.notesassistant.model.personalassistant.Operation;
import ru.nikkitavr.notesassistant.model.personalassistant.OperationConfig;

public class DownloadContentOperation implements OperationConfig {
  private List<DownloadableContent> contentTypesToDownload;
  private Map<DownloadableContent, Configurable> contentTypeDownloadConfiguration;

  @Override
  public Operation getOperation() {
    return null;
  }

  @Getter
  public enum DownloadableContent {
    PHOTO(null),
    VIDEO(LargeContentDownloadConfiguration.class),
    AUDIO(null),
    DOCUMENT(null),
    VOICE(VoiceDownloadConfiguration.class),
    VIDEO_CIRCLE(null),
    WEB_PAGE(null);

    private final Class<? extends Configurable> configurationClass;

    DownloadableContent(Class<? extends Configurable> configurationClass) {
      this.configurationClass = configurationClass;
    }
  }
}
