package com.senstiveconfig.service;

import com.senstiveconfig.client.DecryptedValue;

public class SensitiveConfigValuePlainTextService implements SensitiveConfigValueService {

  @Override
  public DecryptedValue retrieveSecret(String secret) {
    return new DecryptedValue(secret.toCharArray());
  }

  @Override
  public boolean matches(String secret) {
    return true;
  }
}
