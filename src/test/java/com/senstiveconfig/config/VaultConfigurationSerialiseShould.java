package com.senstiveconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class VaultConfigurationSerialiseShould {

  @Test
  public void serialiseFromJson() throws IOException {
    VaultConfiguration serializedObject = new ObjectMapper().readValue(json(), VaultConfiguration.class);

    assertThat(serializedObject.getAddress()).isEqualTo("https://localhost:8200");
    assertThat(serializedObject.getTokenPath()).isEqualTo("/path/url");
    assertThat(serializedObject.getOpenTimeout()).isEqualTo(10);
    assertThat(serializedObject.getReadTimeout()).isEqualTo(20);
    assertThat(serializedObject.getSslPemFilePath()).isEqualTo("/ssl/path");
    assertThat(serializedObject.getSslVerify()).isTrue();
  }

  private String json() {
    return "{ \"address\": \"https://localhost:8200\", \"tokenPath\": \"/path/url\", \"openTimeout\": 10, \"readTimeout\": 20, \"sslPemFilePath\": \"/ssl/path\", \"sslVerify\": true} ";
  }
}
