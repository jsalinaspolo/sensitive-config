package com.senstiveconfig.service;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.Logical;
import com.bettercloud.vault.response.LogicalResponse;
import com.senstiveconfig.client.DecryptedPassword;
import com.senstiveconfig.config.VaultConfiguration;
import com.senstiveconfig.vault.VaultApiFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static com.senstiveconfig.service.SensitiveConfigValueVaultService.VALUE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SensitiveConfigValueVaultServiceShould {
  private static final String SOME_PATH = "/some/path";
  private static final String MATCHING_PATH = String.format("VAULT(%s)", SOME_PATH);
  private static final String NON_MATCHING_PATH = "not-a-vault-path";
  private static final VaultConfiguration VAULT_CONFIGURATION = new VaultConfiguration();
  private static final String SECRET_VALUE = "BobbyP";

  private final VaultApiFactory factory = mock(VaultApiFactory.class);
  private final Vault vault = mock(Vault.class);

  private final SensitiveConfigValueVaultService underTest = new SensitiveConfigValueVaultService(factory);

  @Before
  public void initialise() throws Exception {
    when(factory.createVaultAPI(VAULT_CONFIGURATION)).thenReturn(vault);
  }

  @Test
  public void matchesWhenConfiguredAndCorrectPath() {
    underTest.setConfiguration(VAULT_CONFIGURATION);
    assertThat(underTest.matches(MATCHING_PATH)).isTrue();
  }

  @Test
  public void notConfiguredDoesNotMatch() {
    underTest.setConfiguration(null);
    assertThat(underTest.matches(MATCHING_PATH)).isFalse();
  }

  @Test
  public void secretPathDoesNotMatch() {
    underTest.setConfiguration(VAULT_CONFIGURATION);
    assertThat(underTest.matches(NON_MATCHING_PATH)).isFalse();
  }

  @Test(expected = IllegalStateException.class)
  public void unableToRetrieveIfNotConfigured() {
    underTest.retrieveSecret(MATCHING_PATH);
  }

  @Test(expected = IllegalStateException.class)
  public void unableToRetrieveIfNotAVaultSecretPath() {
    underTest.setConfiguration(VAULT_CONFIGURATION);
    underTest.retrieveSecret(NON_MATCHING_PATH);
  }

  @Test
  public void readsSecretFromVault() throws VaultException {
    underTest.setConfiguration(VAULT_CONFIGURATION);
    Logical logical = mock(Logical.class);
    LogicalResponse logicalResponse = mock(LogicalResponse.class);
    Map<String, String> expectedResponseData = Collections.singletonMap(VALUE_KEY, SECRET_VALUE);

    when(vault.logical()).thenReturn(logical);
    when(logical.read(SOME_PATH)).thenReturn(logicalResponse);
    when(logicalResponse.getData()).thenReturn(expectedResponseData);

    DecryptedPassword decryptedPassword = underTest.retrieveSecret(MATCHING_PATH);
    assertThat(decryptedPassword.getClearText()).isEqualTo(SECRET_VALUE.toCharArray());
  }

  @Test(expected = RuntimeException.class)
  public void wrapsVaultException() throws Exception {
    underTest.setConfiguration(VAULT_CONFIGURATION);
    when(vault.logical()).thenThrow(new VaultException("expected"));

    underTest.retrieveSecret(MATCHING_PATH);
  }
}
