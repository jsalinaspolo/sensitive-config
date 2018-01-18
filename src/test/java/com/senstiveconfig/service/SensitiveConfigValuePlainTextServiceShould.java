package com.senstiveconfig.service;

import com.senstiveconfig.config.DecryptedValue;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SensitiveConfigValuePlainTextServiceShould {

  private static final String PATH = "passedThrough";
  private SensitiveConfigValuePlainTextService underTest = new SensitiveConfigValuePlainTextService();

  @Test
  public void matchesIsAlwaysTrue() throws Exception {
    assertThat(underTest.matches("anything")).isTrue();
    assertThat(underTest.matches(null)).isTrue();
  }

  @Test
  public void passesThrough() throws Exception {
    DecryptedValue secret = underTest.retrieveSecret(PATH);
    assertThat(secret.value()).isEqualTo(PATH.toCharArray());
  }
}
