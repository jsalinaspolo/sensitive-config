package com.senstiveconfig.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.logcapture.junit.LogCaptureRule;
import com.senstiveconfig.config.ConfigurationParser;
import com.senstiveconfig.config.SensitiveVaultConfiguration;
import com.senstiveconfig.config.ServiceConfiguration;
import com.senstiveconfig.config.VaultConfiguration;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static com.logcapture.assertion.ExpectedLoggingMessage.aLog;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ConfigurationParserShould {


  private final ConfigurationParser underTest = new ConfigurationParser(new ObjectMapper(new YAMLFactory()));

  private final String ENCRYPT_VAULT_SERVICE = "service.yml";
  private final String SIMPLE_SERVICE_YAML = "simple-service.yml";

  @Rule
  public LogCaptureRule logCapture = new LogCaptureRule();

  @Test
  public void failWhenFileIsNull() throws IOException {
    assertThatThrownBy(() -> underTest.createConfiguration(null, SimpleConfiguration.class))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessage("File not declared");
  }

  @Test
  public void failWhenFileNotFound() {
    String fileNotFound = "notFoundFile";
    assertThatThrownBy(() -> underTest.createConfiguration(fileNotFound, SimpleConfiguration.class))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessage(String.format("File %s not found", fileNotFound));
  }

  @Test
  public void readAndParseConfigurationFile() throws Exception {
    String filePath = writeTempFileFromResource(SIMPLE_SERVICE_YAML);

    SimpleConfiguration configuration = underTest.createConfiguration(filePath, SimpleConfiguration.class);

    assertThat(configuration.getField()).isEqualTo("value");
    assertThat(configuration.getVaultConfiguration().getAddress()).isEqualTo("http://127.0.0.1:8200");
    assertThat(configuration.getVaultConfiguration().getTokenPath()).isEqualTo("/tmp/token");
  }

  @Test
  public void failWhenResourceNotFound() {
    String resourceNotFound = "resourceNotFound";

    assertThatThrownBy(() -> underTest.createConfiguration(resourceNotFound, SimpleConfiguration.class))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessage(String.format("File %s not found", resourceNotFound));
  }

  @Test
  public void readAndParseConfigurationResource() throws IOException {
    SimpleConfiguration configuration = underTest.createConfiguration(SIMPLE_SERVICE_YAML, SimpleConfiguration.class);

    assertThat(configuration.getField()).isEqualTo("value");
    assertThat(configuration.getVaultConfiguration().getAddress()).isEqualTo("http://127.0.0.1:8200");
    assertThat(configuration.getVaultConfiguration().getTokenPath()).isEqualTo("/tmp/token");

    logCapture.logged(aLog().warn()
      .withMessage("Unable to load configuration from the filesystem at '" + SIMPLE_SERVICE_YAML + "', but was able to load the resource from the classpath instead."));
  }

  @Test
  public void failParsingVaultSecretsWhenMissingDecryptionService() throws IOException {
    assertThatThrownBy(() -> underTest.createConfiguration(SIMPLE_SERVICE_YAML, ServiceConfiguration.class))
      .isInstanceOf(UnrecognizedPropertyException.class);
  }

  @Test
  public void parseVaultSecretsConfigurationsUsingDeserialiser() throws IOException {
    SensitiveConfigValueDelegatingService sensitiveConfigValueDelegatingService = new SensitiveConfigValueDelegatingService(Collections.emptyList());

    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper.setInjectableValues(new InjectableValues.Std().addValue(SensitiveConfigValueDelegatingService.class, sensitiveConfigValueDelegatingService));

    ServiceConfiguration configuration = new ConfigurationParser(objectMapper).createConfiguration(ENCRYPT_VAULT_SERVICE, ServiceConfiguration.class);

    assertThat(configuration.getEncryptedText().getSensitive()).isEqualTo("VAULT(/secret/env/password)");
    assertThat(configuration.getVaultConfiguration().getAddress()).isEqualTo("http://127.0.0.1:8200");
    assertThat(configuration.getVaultConfiguration().getTokenPath()).isEqualTo("/tmp/token");

    logCapture.logged(aLog().warn()
      .withMessage("Unable to load configuration from the filesystem at '" + ENCRYPT_VAULT_SERVICE + "', but was able to load the resource from the classpath instead."));
  }

  private String writeTempFileFromResource(String resourcePath) throws IOException {
    File configurationFile = File.createTempFile("ENCRYPT_VAULT_SERVICE", "yml");
    configurationFile.deleteOnExit();

    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(Paths.get(resourcePath).toString());
    byte[] buffer = new byte[resourceAsStream.available()];
    resourceAsStream.read(buffer);

    String serviceFilePath = configurationFile.getAbsolutePath();
    Files.write(Paths.get(serviceFilePath), buffer);
    return serviceFilePath;
  }

  static class SimpleConfiguration implements SensitiveVaultConfiguration {

    @JsonProperty
    private String field;

    @JsonProperty
    private VaultConfiguration vaultConfiguration;

    @Override
    public VaultConfiguration getVaultConfiguration() {
      return this.vaultConfiguration;
    }

    public String getField() {
      return field;
    }
  }
}
