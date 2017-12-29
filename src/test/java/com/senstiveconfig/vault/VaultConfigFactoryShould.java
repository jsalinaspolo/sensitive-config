package com.senstiveconfig.vault;

import com.bettercloud.vault.VaultConfig;
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

public class VaultConfigFactoryShould {

  private static final String FILE_LOCATION_THAT_DOES_NOT_EXIST = "/tmp/file-does-not-exist";
  private static final String VAULT_SERVER_URL = "http://127.0.0.1:8200";
  private String tokenFileLocation;
  private final VaultConfiguration vaultConfiguration = mock(VaultConfiguration.class);
  private final VaultConfigFactory underTest = new VaultConfigFactory();

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
      underTest.createConfigFrom(vaultConfiguration);
      fail("Expected VaultException");
    } catch (VaultException e) {
      assertThat(e.getLocalizedMessage()).isEqualTo("java.nio.file.NoSuchFileException: /tmp/file-does-not-exist");
    }
  }

  @Test
  public void readTokenFileWhenItExists() throws VaultException {
    when(vaultConfiguration.getAddress()).thenReturn(VAULT_SERVER_URL);
    when(vaultConfiguration.getTokenPath()).thenReturn(tokenFileLocation);

    VaultConfig vaultConf = underTest.createConfigFrom(vaultConfiguration);

    assertThat(new File(tokenFileLocation).exists()).isTrue();
    assertThat(vaultConf).isNotNull();
    assertThat(vaultConf.getSslConfig().isVerify()).isTrue();
  }

  @Test
  public void readSSLConfigWhenItExists() throws VaultException {
    when(vaultConfiguration.getAddress()).thenReturn(VAULT_SERVER_URL);
    when(vaultConfiguration.getTokenPath()).thenReturn(tokenFileLocation);
    when(vaultConfiguration.getSslPemFilePath()).thenReturn(tokenFileLocation);
    when(vaultConfiguration.getSslVerify()).thenReturn(false);

    VaultConfig vaultConf = underTest.createConfigFrom(vaultConfiguration);

    assertThat(new File(tokenFileLocation).exists()).isTrue();
    assertThat(vaultConf).isNotNull();
    assertThat(vaultConf.getSslConfig().isVerify()).isFalse();
  }
}
