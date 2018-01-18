package com.sensitiveconfig;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sensitiveconfig.vault.SSLUtils;
import com.sensitiveconfig.vault.VaultContainer;
import com.senstiveconfig.config.ConfigurationParser;
import com.senstiveconfig.config.ServiceConfiguration;
import com.senstiveconfig.config.VaultConfiguration;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;
import com.senstiveconfig.service.SensitiveConfigValuePlainTextService;
import com.senstiveconfig.service.SensitiveConfigValueVaultService;
import com.senstiveconfig.vault.VaultClient;
import com.senstiveconfig.vault.VaultConfigFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.senstiveconfig.service.SensitiveConfigValueVaultService.VALUE_KEY;
import static org.assertj.core.api.Assertions.assertThat;

public class ServiceConfigurationShould {

  @ClassRule
  public static final VaultContainer container = new VaultContainer();

  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @BeforeClass
  public static void setupClass() throws Exception {
    container.initAndUnsealVault();
    container.setupBackendUserPass();

    SSLUtils.createClientCertAndKey();
  }

  @Before
  public void initialise() {
    environmentVariables.set("VAULT_ADDR", container.getAddress());
    environmentVariables.set("VAULT_TOKEN", container.rootToken());
  }

  @Test
  public void shouldParseSensitiveConfiguration() throws IOException, VaultException {
    String secretPath = "secret/env/password";
    String value = "decryptedPassword";
    Vault vault = container.getRootVault();

    vault.logical().write(secretPath, Collections.singletonMap(VALUE_KEY, value));

    VaultConfiguration vaultConfiguration = ConfigurationParser.withVaultConfiguration().createConfiguration("service.yml", VaultConfiguration.class);
    VaultClient vaultClient = new VaultClient(vaultConfiguration, new VaultConfigFactory());

    SensitiveConfigValueVaultService sensitiveConfigValueVaultService = new SensitiveConfigValueVaultService(vaultClient);

    SensitiveConfigValueDelegatingService sensitiveConfigValueDelegatingService = new SensitiveConfigValueDelegatingService(Arrays.asList(sensitiveConfigValueVaultService, new SensitiveConfigValuePlainTextService()));

    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper.setInjectableValues(new InjectableValues.Std().addValue(SensitiveConfigValueDelegatingService.class, sensitiveConfigValueDelegatingService));

    ConfigurationParser configurationParser = new ConfigurationParser(objectMapper);
    ServiceConfiguration serviceConfiguration = configurationParser.createConfiguration("service.yml", ServiceConfiguration.class);

    assertThat(serviceConfiguration.getEncryptedText().getSensitive()).isEqualTo("VAULT(/" + secretPath + ")");
    assertThat(serviceConfiguration.getEncryptedText().getDecryptedValue().clearText()).isEqualTo(value);
  }
}
