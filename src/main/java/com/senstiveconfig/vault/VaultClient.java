package com.senstiveconfig.vault;


import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.senstiveconfig.config.VaultConfiguration;

public class VaultClient {
  private VaultConfiguration vaultConfiguration;
  private final VaultConfigFactory vaultConfigFactory;

  public VaultClient(VaultConfiguration vaultConfiguration, VaultConfigFactory vaultConfigFactory) {
    this.vaultConfiguration = vaultConfiguration;
    this.vaultConfigFactory = vaultConfigFactory;
  }

  public void setVaultConfiguration(VaultConfiguration vaultConfiguration) {
    this.vaultConfiguration = vaultConfiguration;
  }

  protected Vault createVaultAPI() throws VaultException {
    // The Vault config builder appears to do some caching. If we get a dropped connection or vault is down
    // it does not reconnect. Therefore create a fresh config and vault object for each read.
    return new Vault(vaultConfigFactory.createConfigFrom(vaultConfiguration));
  }

  public String read(String path, String key) {
    try {
      return createVaultAPI().logical().read(path).getData().get(key);
    } catch (VaultException e) {
      throw handleStatusCodeError(path, e);
    }
  }

  private RuntimeException handleStatusCodeError(String secret, VaultException e) {
    int httpStatusCode = e.getHttpStatusCode();
    switch (httpStatusCode) {
      case 0:
        return new RuntimeException("Unable to connect to vault '" + vaultConfiguration.getAddress() + "': the path '" + secret + "'", e);
      case 404: // Fallthrough
      case 403:
        return new RuntimeException("Connected to '" + vaultConfiguration.getAddress() + "': unable to read secret '" + secret + "'", e);
      default:
        return new RuntimeException("HTTP status " + httpStatusCode + ". Could not retrieve from Vault '" + vaultConfiguration.getAddress() + "': the path '" + secret + "'", e);
    }
  }
}
