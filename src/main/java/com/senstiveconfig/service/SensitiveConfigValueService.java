package com.senstiveconfig.service;

import com.senstiveconfig.client.DecryptedValue;

public interface SensitiveConfigValueService {

  DecryptedValue retrieveSecret(String secret);

  boolean matches(String secret);
}
