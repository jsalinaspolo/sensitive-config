package com.senstiveconfig.service;


import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.senstiveconfig.client.DecryptedPassword;
import com.senstiveconfig.config.VaultConfiguration;
import com.senstiveconfig.vault.VaultApiFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveConfigValueVaultService implements SensitiveConfigValueService {
  public static final String VALUE_KEY = "value";
  private static final Pattern PATTERN = Pattern.compile("VAULT\\((.+)\\)");

  private VaultConfiguration vaultConfiguration;
  private final VaultApiFactory vaultApiFactory;

  public SensitiveConfigValueVaultService(VaultApiFactory vaultApiFactory) {
    this.vaultApiFactory = vaultApiFactory;
  }

  public void setConfiguration(VaultConfiguration vaultConfiguration) {
    this.vaultConfiguration = vaultConfiguration;
  }

  @Override
  public boolean matches(String secret) {
    return (vaultConfiguration != null) && PATTERN.matcher(secret).matches();
  }

  @Override
  public DecryptedPassword retrieveSecret(String secret) {
    Matcher matcher = PATTERN.matcher(secret);
    if ((vaultConfiguration != null) && matcher.matches()) {
      String vaultPath = matcher.group(1);
      // MVP - for now assume everything is secret/value rather than objects - DP 20170408

      try {
        String value = vaultAPI().logical().read(vaultPath).getData().get(VALUE_KEY);
        return new DecryptedPassword(value.toCharArray());
      } catch (VaultException e) {
        return handleStatusCodeError(secret, e);
      }
    } else {
      // Should never get here since we expect matches to have been called to select this service.
      throw new IllegalStateException("Secret: '" + secret + "' did not match pattern did you call matches(secret) first?");
    }
  }

  private DecryptedPassword handleStatusCodeError(String secret, VaultException e) {
    int httpStatusCode = e.getHttpStatusCode();
    switch (httpStatusCode) {
      case 0:
        throw new RuntimeException("Unable to connect to vault '" + vaultConfiguration.getAddress() + "': the path '" + secret + "'", e);
      case 404: // Fallthrough
      case 403:
        throw new RuntimeException("Connected to '" + vaultConfiguration.getAddress() + "': unable to read secret '" + secret + "'", e);
      default:
        throw new RuntimeException("HTTP status " + httpStatusCode + ". Could not retrieve from Vault '" + vaultConfiguration.getAddress() + "': the path '" + secret + "'", e);
    }
  }

  protected Vault vaultAPI() throws VaultException {
    return vaultApiFactory.createVaultAPI(vaultConfiguration);
  }
}
