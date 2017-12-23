package com.senstiveconfig.service;

import com.senstiveconfig.client.DecryptedPassword;
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
    DecryptedPassword secret = underTest.retrieveSecret(PATH);
    assertThat(secret.getClearText()).isEqualTo(PATH.toCharArray());
  }
}
