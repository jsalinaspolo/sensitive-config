package com.senstiveconfig.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class VaultConfigurationDeserializer extends JsonDeserializer<VaultConfiguration> {

  @Override
  public VaultConfiguration deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    TreeNode vault = jp.readValueAsTree().at("/vaultConfiguration");
    if (vault.isMissingNode()) {
      return null;
    }

    return new ObjectMapper().treeToValue(vault, VaultConfiguration.class);
  }
}
