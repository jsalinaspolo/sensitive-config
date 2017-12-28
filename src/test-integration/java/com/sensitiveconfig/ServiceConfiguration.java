package com.sensitiveconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senstiveconfig.config.SensitiveConfigValue;
import com.senstiveconfig.config.SensitiveVaultConfiguration;
import com.senstiveconfig.config.VaultConfiguration;

public class ServiceConfiguration implements SensitiveVaultConfiguration {

  @JsonProperty
  private SensitiveConfigValue encryptedText;

  @JsonProperty
  private VaultConfiguration vaultConfiguration;

  @Override
  public VaultConfiguration getVaultConfiguration() {
    return this.vaultConfiguration;
  }

  public SensitiveConfigValue getEncryptedText() {
    return encryptedText;
  }
}
