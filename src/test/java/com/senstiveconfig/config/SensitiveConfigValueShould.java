package com.senstiveconfig.config;

import com.senstiveconfig.client.DecryptedPassword;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SensitiveConfigValueShould {

  private static final String SENSITIVE = "secret/value";
  private final DecryptedPassword decryptedPassword = mock(DecryptedPassword.class);
  private final SensitiveConfigValueDelegatingService sensitiveConfigValueDelegatingService = mock(SensitiveConfigValueDelegatingService.class);

  private final SensitiveConfigValue underTest = new SensitiveConfigValue(sensitiveConfigValueDelegatingService, SENSITIVE);

  @Test
  public void checksCached() throws Exception {
    when(sensitiveConfigValueDelegatingService.retrieveSecret(SENSITIVE)).thenReturn(decryptedPassword);

    DecryptedPassword retrievedFirstTime = underTest.getDecryptedValue();
    DecryptedPassword retrievedSecondTime = underTest.getDecryptedValue();

    assertThat(retrievedFirstTime).isEqualTo(decryptedPassword);
    assertThat(retrievedSecondTime).isEqualTo(decryptedPassword);

    verify(sensitiveConfigValueDelegatingService, times(1)).retrieveSecret(SENSITIVE);
  }
}
