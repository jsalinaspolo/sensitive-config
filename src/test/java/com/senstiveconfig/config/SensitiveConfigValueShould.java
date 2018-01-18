package com.senstiveconfig.config;

import com.senstiveconfig.client.DecryptedValue;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SensitiveConfigValueShould {

  private static final String SENSITIVE = "secret/value";
  private final DecryptedValue decryptedValue = new DecryptedValue("value".toCharArray());
  private final SensitiveConfigValueDelegatingService sensitiveConfigValueDelegatingService = mock(SensitiveConfigValueDelegatingService.class);

  private final SensitiveConfigValue underTest = new SensitiveConfigValue(sensitiveConfigValueDelegatingService, SENSITIVE);

  @Test
  public void checksCached() {
    when(sensitiveConfigValueDelegatingService.retrieveSecret(SENSITIVE)).thenReturn(decryptedValue);

    DecryptedValue retrievedFirstTime = underTest.getDecryptedValue();
    DecryptedValue retrievedSecondTime = underTest.getDecryptedValue();

    assertThat(retrievedFirstTime).isEqualTo(decryptedValue);
    assertThat(retrievedSecondTime).isEqualTo(decryptedValue);

    verify(sensitiveConfigValueDelegatingService, times(1)).retrieveSecret(SENSITIVE);
  }
}
