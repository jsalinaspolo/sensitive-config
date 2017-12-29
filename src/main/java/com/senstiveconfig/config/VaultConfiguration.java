package com.senstiveconfig.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VaultConfiguration {

  @JsonProperty
  private String address;

  @JsonProperty
  private String tokenPath;

  @JsonProperty
  private Integer openTimeout;

  @JsonProperty
  private Integer readTimeout;

  @JsonProperty
  private String sslPemFilePath;

  @JsonProperty
  private Boolean sslVerify;

  /**
   * If null defaults to "VAULT_ADDR" environment variable
   *
   * @return address of vault server
   */
  public String getAddress() {
    return address;
  }

  /**
   * If null defaults to "VAULT_TOKEN" environment variable (if set)
   *
   * @return path to token file for vault server
   */
  public String getTokenPath() {
    return tokenPath;
  }

  /**
   * If null defaults to "VAULT_OPEN_TIMEOUT" environment variable
   *
   * @return open timeout in seconds
   */
  public Integer getOpenTimeout() {
    return openTimeout;
  }

  /**
   * If null Defaults to "VAULT_READ_TIMEOUT" environment variable
   *
   * @return read timeout in seconds
   */
  public Integer getReadTimeout() {
    return readTimeout;
  }

  /**
   * If defaults to "VAULT_SSL_CERT" environment variable
   *
   * @return file path to cert file
   */
  public String getSslPemFilePath() {
    return sslPemFilePath;
  }

  /**
   * If defaults to "VAULT_SSL_VERIFY" environment variable
   *
   * @return if we need to verify the ssl connection
   */
  public Boolean getSslVerify() {
    return sslVerify;
  }
}
