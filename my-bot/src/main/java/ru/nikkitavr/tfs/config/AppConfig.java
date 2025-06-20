package ru.nikkitavr.tfs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.nikkitavr.tfs.model.MessageAssistantConfiguration;
import ru.nikkitavr.tfs.service.MessageAssistantConfigurationProvider;
import ru.nikkitavr.tfs.service.MessageAssistantService;

@Configuration
@Import({
    MessageAssistantService.class,
})
public class AppConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

  public static final String MESSAGE_ASSISTANT_CONFIG_PATH = "data/message-assistant-config.json";
  public static final String MESSAGE_ASSISTANT_DEFAULT_CONFIG_PATH_RSX = "message-assistant-default-config.json";


  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    //objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return objectMapper;
  }

  @Bean
  public MessageAssistantConfigurationProvider messageAssistantConfigurationProvider(ObjectMapper objectMapper) throws IOException {
    MessageAssistantConfiguration initialConfiguration;
    try (InputStream is = Files.newInputStream(Path.of(MESSAGE_ASSISTANT_CONFIG_PATH))) {
      initialConfiguration = objectMapper.readValue(is, MessageAssistantConfiguration.class);
    } catch (NoSuchFileException | FileNotFoundException e) {
      LOGGER.warn("Configuration file not found: {}, default config will be used", MESSAGE_ASSISTANT_CONFIG_PATH);
      try (InputStream isFallback = getClass().getClassLoader().getResourceAsStream(MESSAGE_ASSISTANT_DEFAULT_CONFIG_PATH_RSX)) {
        if (isFallback == null) {
          throw new FileNotFoundException("Default configuration file not found");
        }
        initialConfiguration = objectMapper.readValue(isFallback, MessageAssistantConfiguration.class);
      }
    }

    MessageAssistantConfigurationProvider configurationProvider = new MessageAssistantConfigurationProvider();
    configurationProvider.update(initialConfiguration);
    return configurationProvider;
  }

}
