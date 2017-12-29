package com.senstiveconfig.config;

import com.fasterxml.jackson.annotation.JsonProperty;

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
