package com.senstiveconfig.service;

import com.senstiveconfig.client.DecryptedPassword;
import com.senstiveconfig.vault.VaultClient;
import org.junit.Test;

import static com.senstiveconfig.service.SensitiveConfigValueVaultService.VALUE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SensitiveConfigValueVaultServiceShould {
  private static final String SOME_PATH = "/some/path";
  private static final String MATCHING_PATH = String.format("VAULT(%s)", SOME_PATH);
  private static final String NON_MATCHING_PATH = "not-a-vault-path";
  private static final String SECRET_VALUE = "BobbyP";

  private final VaultClient client = mock(VaultClient.class);
  private final SensitiveConfigValueVaultService underTest = new SensitiveConfigValueVaultService(client);

  @Test
  public void matchWhenCorrectPath() {
    assertThat(underTest.matches(MATCHING_PATH)).isTrue();
  }

  @Test
  public void notMatchWhenSecretPathIsNotVault() {
    assertThat(underTest.matches(NON_MATCHING_PATH)).isFalse();
  }

  @Test
  public void readSecretFromVault() {
    when(client.read(SOME_PATH, VALUE_KEY)).thenReturn(SECRET_VALUE);

    DecryptedPassword decryptedPassword = underTest.retrieveSecret(MATCHING_PATH);

    assertThat(decryptedPassword.getClearText()).isEqualTo(SECRET_VALUE.toCharArray());
  }
}
