package com.senstiveconfig.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.senstiveconfig.config.VaultConfiguration;
import com.senstiveconfig.config.VaultConfigurationDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ConfigurationParser {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);

  private final ObjectMapper objectMapper;

  public ConfigurationParser(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public <T> T createConfiguration(String configurationFileName, Class<T> configurationClass) throws IOException {
    try (InputStream fileStream = getFile(configurationFileName)) {
      return objectMapper.readValue(fileStream, configurationClass);
    }
  }

  private InputStream getFile(String filename) throws IOException {
    if (filename == null) {
      throw new FileNotFoundException("File not declared");
    }

    File file = new File(filename);
    if (!file.exists()) {
      InputStream resource = getClass().getClassLoader().getResourceAsStream(filename);
      if (resource != null) {
        logger.warn("Unable to load configuration from the filesystem at '" + filename + "', but was able to load the resource from the classpath instead.");
        return resource;
      }
      throw new FileNotFoundException("File " + file + " not found");
    }
    return new FileInputStream(file);
  }

  public static ConfigurationParser withVaultConfiguration() {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
      .registerModule(new SimpleModule()
        .addDeserializer(VaultConfiguration.class, new VaultConfigurationDeserializer()));

    return new ConfigurationParser(objectMapper);
  }
}
