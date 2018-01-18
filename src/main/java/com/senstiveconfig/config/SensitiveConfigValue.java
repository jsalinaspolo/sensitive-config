package com.senstiveconfig.config;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.senstiveconfig.client.DecryptedValue;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;

import java.util.concurrent.atomic.AtomicReference;

public class SensitiveConfigValue {

  private final SensitiveConfigValueDelegatingService sensitiveConfigValueService;
  private final String sensitive;

  private transient AtomicReference<DecryptedValue> retrieved = new AtomicReference<>();

  public String getSensitive() {
    return sensitive;
  }

  public SensitiveConfigValue(@JacksonInject SensitiveConfigValueDelegatingService sensitiveConfigValueService,
                              @JsonProperty String sensitive) {
    this.sensitiveConfigValueService = sensitiveConfigValueService;
    this.sensitive = sensitive;
  }

  @JsonIgnore
  public DecryptedValue getDecryptedValue() {
    return retrieved.updateAndGet(cur -> cur == null ? sensitiveConfigValueService.retrieveSecret(sensitive) : cur);
  }
}
