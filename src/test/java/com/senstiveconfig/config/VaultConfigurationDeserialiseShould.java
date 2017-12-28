package com.senstiveconfig.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class VaultConfigurationDeserialiseShould {

  @Test
  public void deserialiseFromJson() throws IOException {
    VaultConfiguration deserialisedObject = new ObjectMapper().readValue(json(), VaultConfiguration.class);

    assertThat(deserialisedObject.getAddress()).isEqualTo("https://localhost:8200");
    assertThat(deserialisedObject.getTokenPath()).isEqualTo("/path/url");
    assertThat(deserialisedObject.getOpenTimeout()).isEqualTo(10);
    assertThat(deserialisedObject.getReadTimeout()).isEqualTo(20);
    assertThat(deserialisedObject.getSslPemFilePath()).isEqualTo("/ssl/path");
    assertThat(deserialisedObject.getSslVerify()).isTrue();
  }

  @Test
  public void deserialiseFromJsonUsingCustomDeserialiser() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
      .registerModule(new SimpleModule()
        .addDeserializer(VaultConfiguration.class, new VaultConfigurationDeserializer()));

    InputStream resource = getClass().getClassLoader().getResourceAsStream("service.yml");
    VaultConfiguration deserialisedObject = objectMapper.readValue(resource, VaultConfiguration.class);

    assertThat(deserialisedObject.getAddress()).isEqualTo("http://127.0.0.1:8200");
    assertThat(deserialisedObject.getTokenPath()).isEqualTo("/tmp/token");
    assertThat(deserialisedObject.getOpenTimeout()).isNull();
    assertThat(deserialisedObject.getReadTimeout()).isNull();
    assertThat(deserialisedObject.getSslPemFilePath()).isNull();
    assertThat(deserialisedObject.getSslVerify()).isNull();
  }

  private String json() {
    return "{ \"address\": \"https://localhost:8200\", \"tokenPath\": \"/path/url\", \"openTimeout\": 10, \"readTimeout\": 20, \"sslPemFilePath\": \"/ssl/path\", \"sslVerify\": true} ";
  }
}
