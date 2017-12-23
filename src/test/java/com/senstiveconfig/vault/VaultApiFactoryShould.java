package com.senstiveconfig.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.senstiveconfig.config.VaultConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VaultApiFactoryShould {

  private static final String FILE_LOCATION_THAT_DOES_NOT_EXIST = "/tmp/file-does-not-exist";
  private static final String VAULT_SERVER_URL = "http://127.0.0.1:8200";
  private String tokenFileLocation;
  private VaultConfiguration vaultConfiguration = mock(VaultConfiguration.class);

  private final VaultApiFactory underTest = new VaultApiFactory();

  @Before
  public void initialise() throws IOException {
    File tokenFile = File.createTempFile("temp-token-", "");
    tokenFile.deleteOnExit();
    tokenFileLocation = tokenFile.getAbsolutePath();

    Files.write(Paths.get(tokenFileLocation), "TOKEN-VALUE".getBytes());
  }

  @Test
  public void throwsExceptionWhenTokenFileNotFound() {
    when(vaultConfiguration.getAddress()).thenReturn(VAULT_SERVER_URL);
    when(vaultConfiguration.getTokenPath()).thenReturn(FILE_LOCATION_THAT_DOES_NOT_EXIST);

    try {
      underTest.createVaultAPI(vaultConfiguration);
      fail("Expected VaultException");
    } catch (VaultException ve) {
      assertThat(ve.getLocalizedMessage()).isEqualTo("java.nio.file.NoSuchFileException: /tmp/file-does-not-exist");
    }
  }

  @Test
  public void readsTokenFileWhenItExists() throws VaultException {
    when(vaultConfiguration.getAddress()).thenReturn(VAULT_SERVER_URL);
    when(vaultConfiguration.getTokenPath()).thenReturn(tokenFileLocation);

    Vault vaultAPI = underTest.createVaultAPI(vaultConfiguration);

    assertThat(new File(tokenFileLocation).exists()).isTrue();
    assertThat(vaultAPI).isNotNull();
  }
}
