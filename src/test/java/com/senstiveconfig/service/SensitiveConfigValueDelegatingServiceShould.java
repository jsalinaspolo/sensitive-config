package com.senstiveconfig.service;

import com.senstiveconfig.config.DecryptedValue;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SensitiveConfigValueDelegatingServiceShould {
  private static final String SECRET = "BobbyP";
  private static final DecryptedValue DECRYPTED_PASSWORD = new DecryptedValue(SECRET.toCharArray());
  private final SensitiveConfigValueService sensitiveConfigValueService = mock(SensitiveConfigValueService.class);
  private final SensitiveConfigValueDelegatingService underTest = new SensitiveConfigValueDelegatingService(asList(sensitiveConfigValueService));

  @Test
  public void configuredSensitiveConfigValueDelegatingServiceMatches() {
    when(sensitiveConfigValueService.matches(SECRET)).thenReturn(true);
    when(sensitiveConfigValueService.retrieveSecret(SECRET)).thenReturn(DECRYPTED_PASSWORD);

    DecryptedValue result = underTest.retrieveSecret(SECRET);
    assertThat(result).isEqualTo(DECRYPTED_PASSWORD);
  }

  @Test(expected = IllegalArgumentException.class)
  public void noSensitiveConfigValueDelegatingServiceMatches() {
    underTest.retrieveSecret(SECRET);
  }
}
