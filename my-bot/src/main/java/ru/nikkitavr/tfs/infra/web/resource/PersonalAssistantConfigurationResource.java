package ru.nikkitavr.tfs.infra.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nikkitavr.tfs.config.AppConfig;
import ru.nikkitavr.tfs.model.personalassistant.PersonalAssistantConfiguration;
import ru.nikkitavr.tfs.service.PersonalAssistantConfigurationProvider;

@RestController
@RequestMapping("/personal-assistant/config")
public class PersonalAssistantConfigurationResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonalAssistantConfigurationResource.class);

  private final ObjectMapper objectMapper;
  private final PersonalAssistantConfigurationProvider configurationProvider;

  public PersonalAssistantConfigurationResource(ObjectMapper objectMapper,
      PersonalAssistantConfigurationProvider configurationProvider) {
    this.objectMapper = objectMapper;
    this.configurationProvider = configurationProvider;
  }

  /** Returns current configuration as json string. */
  @GetMapping
  public ResponseEntity<String> getConfig() {
    try {
      Path path = Path.of(AppConfig.MESSAGE_ASSISTANT_CONFIG_PATH);
      if (!Files.exists(path)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
      String json = Files.readString(path);
      return ResponseEntity.ok(json);
    } catch (IOException e) {
      LOGGER.error("Error reading configuration", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Replaces configuration with new json provided in request body.
   * The provider is updated with the parsed configuration.
   */
  @PostMapping
  public ResponseEntity<Void> updateConfig(@RequestBody String jsonBody) {
    try {
      PersonalAssistantConfiguration config =
          objectMapper.readValue(jsonBody, PersonalAssistantConfiguration.class);

      Path configPath = Path.of(AppConfig.MESSAGE_ASSISTANT_CONFIG_PATH);
      Files.createDirectories(configPath.getParent());
      Files.writeString(configPath, jsonBody);

      configurationProvider.update(config);
      return ResponseEntity.ok().build();
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      LOGGER.error("Invalid configuration JSON", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (IOException e) {
      LOGGER.error("Error writing configuration", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Returns default configuration bundled with the application. */
  @GetMapping("/default")
  public ResponseEntity<String> getDefaultConfig() {
    try (InputStream is =
             getClass().getClassLoader().getResourceAsStream(AppConfig.MESSAGE_ASSISTANT_DEFAULT_CONFIG_PATH_RSX)) {
      if (is == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
      String json = new String(is.readAllBytes());
      return ResponseEntity.ok(json);
    } catch (IOException e) {
      LOGGER.error("Error reading default configuration", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
