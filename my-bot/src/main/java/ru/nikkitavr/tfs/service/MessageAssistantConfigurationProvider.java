package ru.nikkitavr.tfs.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import ru.nikkitavr.tfs.model.MessageAssistantConfiguration;

public class MessageAssistantConfigurationProvider {

  private final AtomicReference<MessageAssistantConfiguration> config = new AtomicReference<>();

  public MessageAssistantConfiguration get() {
    return config.get();
  }

  public void update(MessageAssistantConfiguration newConfig) {
    config.set(newConfig);
  }
}
