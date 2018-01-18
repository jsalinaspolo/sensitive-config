package com.senstiveconfig.config;

public final class DecryptedValue {

  private char[] value;

  public DecryptedValue(char[] value) {
    this.value = value;
  }

  public char[] value() {
    return value;
  }

  public String clearText() {
    return new String(value);
  }
}
