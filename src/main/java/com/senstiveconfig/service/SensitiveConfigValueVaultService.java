package com.senstiveconfig.service;


import com.senstiveconfig.client.DecryptedValue;
import com.senstiveconfig.vault.VaultClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveConfigValueVaultService implements SensitiveConfigValueService {
  public static final String VALUE_KEY = "value";
  private static final Pattern PATTERN = Pattern.compile("VAULT\\((.+)\\)");

  private VaultClient vaultClient;

  public SensitiveConfigValueVaultService(VaultClient vaultClient) {
    this.vaultClient = vaultClient;
  }

  @Override
  public boolean matches(String secret) {
    return PATTERN.matcher(secret).matches();
  }

  @Override
  public DecryptedValue retrieveSecret(String secret) {
    Matcher matcher = PATTERN.matcher(secret);
    if (!matcher.matches()) {
      // Should never get here since we expect matches to have been called to select this service.
      throw new IllegalStateException("Secret: '" + secret + "' did not match pattern did you call matches(secret) first?");
    }
    String vaultPath = matcher.group(1);
    return new DecryptedValue(vaultClient.read(vaultPath, VALUE_KEY).toCharArray());
  }
}
