package com.senstiveconfig.client;

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
