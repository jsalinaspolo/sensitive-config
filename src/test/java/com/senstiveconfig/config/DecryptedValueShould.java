package com.senstiveconfig.config;

import com.senstiveconfig.config.DecryptedValue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DecryptedValueShould {
  private static final String PASSWORD = "secret";

  @Test
  public void matchValueWithClearText() {
    DecryptedValue decryptedValue = new DecryptedValue(PASSWORD.toCharArray());
    assertThat(decryptedValue.value()).isEqualTo(decryptedValue.clearText().toCharArray());
  }
}
