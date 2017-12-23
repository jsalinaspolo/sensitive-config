package com.sensitiveconfig.parser;

import com.sensitiveconfig.ServiceConfiguration;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class ConfigurationParserShould {

  private final ConfigurationParser underTest = ConfigurationParser.forSensitiveConfigValue(mock(SensitiveConfigValueDelegatingService.class));

  @Test
  public void canReadAndParseConfigurationFromResource() throws Exception {
    ServiceConfiguration configuration = underTest.createConfiguration("service.yml", ServiceConfiguration.class);

    assertThat(configuration.getEncryptedText().getSensitive()).isEqualTo("VAULT(/secret/env/password)");
    assertThat(configuration.getVaultConfiguration().getAddress()).isEqualTo("http://127.0.0.1:8200");
    assertThat(configuration.getVaultConfiguration().getTokenPath()).isEqualTo("/tmp/token");
  }
}
