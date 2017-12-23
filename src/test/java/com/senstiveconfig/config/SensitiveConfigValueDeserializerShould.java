package com.senstiveconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SensitiveConfigValueDeserializerShould {

  @Test
  public void shouldBeIgnoreDecryptedPassword() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    SensitiveConfigValue sensitiveValue = new SensitiveConfigValue(
      mock(SensitiveConfigValueDelegatingService.class),
      "sensitiveValue");
    new ObjectMapper().writeValue(out, sensitiveValue);
    assertThat(out.toString()).isEqualTo("{\"sensitive\":\"sensitiveValue\"}");
  }
}
