package ru.nikkitavr.notesassistant.service;

import java.util.concurrent.atomic.AtomicReference;
import ru.nikkitavr.notesassistant.model.personalassistant.PersonalAssistantConfiguration;

public class PersonalAssistantConfigurationProvider {

  private final AtomicReference<PersonalAssistantConfiguration> config = new AtomicReference<>();

  public PersonalAssistantConfiguration get() {
    return config.get();
  }

  public void update(PersonalAssistantConfiguration newConfig) {
    config.set(newConfig);
  }
}
