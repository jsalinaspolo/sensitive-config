package com.senstiveconfig.service;

import com.senstiveconfig.client.DecryptedPassword;

public interface SensitiveConfigValueService {

  DecryptedPassword retrieveSecret(String secret);

  boolean matches(String secret);
}
