package com.senstiveconfig.service;

import com.senstiveconfig.client.DecryptedPassword;

public class SensitiveConfigValuePlainTextService implements SensitiveConfigValueService {

  @Override
  public DecryptedPassword retrieveSecret(String secret) {
    return new DecryptedPassword(secret.toCharArray());
  }

  @Override
  public boolean matches(String secret) {
    return true;
  }
}
