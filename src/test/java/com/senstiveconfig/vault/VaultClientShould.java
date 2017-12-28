package com.senstiveconfig.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.Logical;
import com.bettercloud.vault.response.LogicalResponse;
import com.senstiveconfig.config.VaultConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static com.senstiveconfig.service.SensitiveConfigValueVaultService.VALUE_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VaultClientShould {

  private static final String SOME_PATH = "/some/path";
  private static final String SECRET_VALUE = "BobbyP";

  private final Vault vault = mock(Vault.class);
  private final VaultConfiguration vaultConfiguration = mock(VaultConfiguration.class);
  private final VaultConfigFactory vaultConfigFactory = mock(VaultConfigFactory.class);

  private final VaultClient underTest = new VaultClientStub(vaultConfiguration, vaultConfigFactory);

  @Test
  public void readsSecretFromVault() throws VaultException {
    Logical logical = mock(Logical.class);
    LogicalResponse logicalResponse = mock(LogicalResponse.class);
    Map<String, String> expectedResponseData = Collections.singletonMap(VALUE_KEY, SECRET_VALUE);

    when(vault.logical()).thenReturn(logical);
    when(logical.read(SOME_PATH)).thenReturn(logicalResponse);
    when(logicalResponse.getData()).thenReturn(expectedResponseData);

    String decryptedPassword = underTest.read(SOME_PATH, VALUE_KEY);
    Assertions.assertThat(decryptedPassword).isEqualTo(SECRET_VALUE);
  }

  @Test
  public void wrapsVaultException() throws VaultException {
    Logical logical = mock(Logical.class);
    when(vault.logical()).thenReturn(logical);
    VaultException expected = new VaultException("expected");
    when(logical.read(SOME_PATH)).thenThrow(expected);

    assertThatThrownBy(() -> underTest.read(SOME_PATH, VALUE_KEY))
      .hasCause(expected);
  }

  class VaultClientStub extends VaultClient {

    public VaultClientStub(VaultConfiguration vaultConfiguration, VaultConfigFactory vaultConfigFactory) {
      super(vaultConfiguration, vaultConfigFactory);
    }

    @Override
    protected Vault createVaultAPI() throws VaultException {
      return vault;
    }
  }
}
