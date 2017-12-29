package com.sensitiveconfig;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sensitiveconfig.vault.SSLUtils;
import com.sensitiveconfig.vault.VaultContainer;
import com.senstiveconfig.client.ConfigurationParser;
import com.senstiveconfig.config.ServiceConfiguration;
import com.senstiveconfig.config.VaultConfiguration;
import com.senstiveconfig.service.SensitiveConfigValueDelegatingService;
import com.senstiveconfig.service.SensitiveConfigValuePlainTextService;
import com.senstiveconfig.service.SensitiveConfigValueVaultService;
import com.senstiveconfig.vault.VaultClient;
import com.senstiveconfig.vault.VaultConfigFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceConfigurationShould {

  private static String tokenFileLocation;

  @ClassRule
  public static final VaultContainer container = new VaultContainer();

  @BeforeClass
  public static void setupClass() throws Exception {
    container.initAndUnsealVault();
    container.setupBackendUserPass();

    SSLUtils.createClientCertAndKey();

    File tokenFile = File.createTempFile("temp-tokenPath-", "");
    tokenFile.deleteOnExit();
    tokenFileLocation = tokenFile.getAbsolutePath();
    Files.write(Paths.get(tokenFileLocation), container.rootToken().getBytes());
  }

  @Test
  public void shouldParseSensitiveConfiguration() throws IOException, VaultException {
    String secretPath = "secret/env/password";
    String value = "decryptedPassword";
    Vault vault = container.getRootVault();

    vault.logical().write(secretPath, Collections.singletonMap("value", value));

    VaultConfiguration vaultConfiguration = ConfigurationParser.withVaultConfiguration().createConfiguration("service.yml", VaultConfiguration.class);
    VaultClient vaultClient = new VaultClient(vaultConfiguration, new VaultConfigFactory());

    hackConfiguration(vaultClient);
    SensitiveConfigValueVaultService sensitiveConfigValueVaultService = new SensitiveConfigValueVaultService(vaultClient);

    SensitiveConfigValueDelegatingService sensitiveConfigValueDelegatingService = new SensitiveConfigValueDelegatingService(Arrays.asList(sensitiveConfigValueVaultService, new SensitiveConfigValuePlainTextService()));

    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper.setInjectableValues(new InjectableValues.Std().addValue(SensitiveConfigValueDelegatingService.class, sensitiveConfigValueDelegatingService));

    ConfigurationParser configurationParser = new ConfigurationParser(objectMapper);
    ServiceConfiguration serviceConfiguration = configurationParser.createConfiguration("service.yml", ServiceConfiguration.class);

    //needs to be change as needs to get the address of the container, tokenLocation, sslPath
    assertThat(serviceConfiguration.getEncryptedText().getSensitive()).isEqualTo("VAULT(/" + secretPath + ")");
    assertThat(serviceConfiguration.getEncryptedText().getDecryptedValue().getClearText()).isEqualTo(value.toCharArray());
  }


  private void hackConfiguration(VaultClient vaultClient) {
    VaultConfiguration configuration = new VaultTestConfiguration(container.getAddress(), tokenFileLocation);
    vaultClient.setVaultConfiguration(configuration);
  }

  public class VaultTestConfiguration extends VaultConfiguration {

    private final String address;
    private final String tokenPath;

    public VaultTestConfiguration(String address, String tokenPath) {
      this.address = address;
      this.tokenPath = tokenPath;
    }

    @Override
    public String getAddress() {
      return address;
    }

    @Override
    public String getTokenPath() {
      return tokenPath;
    }

    @Override
    public String getSslPemFilePath() {
      return SSLUtils.CLIENT_CERT_PEMFILE;
    }
  }
}
