package ru.nikkitavr.tfs.bot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//later may be divided on bot-infrastructre properties and bot-application properties
@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class BotProperties {
  private String token;
  private String rootDirectory;

  public String getRootDirectory() {
    return rootDirectory;
  }

  public BotProperties setRootDirectory(String rootDirectory) {
    this.rootDirectory = rootDirectory;
    return this;
  }

  public String getToken() {
    return token;
  }

  public BotProperties setToken(String token) {
    this.token = token;
    return this;
  }
}
