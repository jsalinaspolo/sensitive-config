package com.senstiveconfig.vault;


import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.senstiveconfig.config.VaultConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VaultConfigFactory {

  public VaultConfig createConfigFrom(VaultConfiguration vaultConfiguration) throws VaultException {
    // TODO add a test for it
    // The Vault config builder appears to do some caching. If we get a dropped connection or vault is down
    // it does not reconnect. Therefore create a fresh config and vault object for each read.
    return buildConfiguration(vaultConfiguration);
  }

  private VaultConfig buildConfiguration(VaultConfiguration vaultConfiguration) throws VaultException {
    String tokenValue = null;

    if (vaultConfiguration.getTokenPath() != null) {
      tokenValue = readTokenFile(vaultConfiguration.getTokenPath());
    }

    VaultConfig vaultConfig = new VaultConfig()
      .address(vaultConfiguration.getAddress())                                // Defaults to "VAULT_ADDR" environment variable
      .token(tokenValue)                                                       // Defaults to "VAULT_TOKEN" environment variable
      .openTimeout(vaultConfiguration.getOpenTimeout())                        // Defaults to "VAULT_OPEN_TIMEOUT" environment variable
      .readTimeout(vaultConfiguration.getReadTimeout());                       // Defaults to "VAULT_READ_TIMEOUT" environment variable

    if (vaultConfiguration.getSslPemFilePath() != null) {
      SslConfig sslConfig = new SslConfig()
        .pemFile(new File(vaultConfiguration.getSslPemFilePath()))          // Defaults to "VAULT_SSL_CERT" environment variable
        .verify(vaultConfiguration.getSslVerify());                         // Defaults to "VAULT_SSL_VERIFY" environment variable
      vaultConfig.sslConfig(sslConfig);
    }

    return vaultConfig.build();
  }

  private String readTokenFile(String path) throws VaultException {
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8).trim();
    } catch (IOException e) {
      throw new VaultException(e);
    }
  }
}
