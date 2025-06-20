package ru.nikkitavr.tfs.infra.web.resource;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message-assistant-bot/config")
public class MessageAssistantConfigurationResource {

  @GetMapping
  public ResponseEntity<String> getConfig() {
    throw new NotImplementedException();
    //TODO: get config from data folder (AppConfig.MESSAGE_ASSISTANT_CONFIG_PATH)
  }

  @PostMapping
  public ResponseEntity<Void> updateConfig(@RequestBody String jsonBody) {
    throw new NotImplementedException();
    //TODO: replace config in data folder (AppConfig.MESSAGE_ASSISTANT_CONFIG_PATH)
  }

  @GetMapping("/default")
  public ResponseEntity<String> getDefaultConfig() {
    throw new NotImplementedException();
    //TODO: get default config from resource folder (AppConfig.MESSAGE_ASSISTANT_DEFAULT_CONFIG_PATH_RSX)
  }
}
