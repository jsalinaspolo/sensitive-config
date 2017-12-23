package com.senstiveconfig.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class DecryptedPassword {
  private static final String CLEAR_TEXT = "clearText";
  private final char[] clearText;

  public DecryptedPassword(@JsonProperty(CLEAR_TEXT) char[] clearText) {
    this.clearText = clearText;
  }

  public boolean matches(DecryptedPassword otherPassword) {
    if (clearText.length != otherPassword.clearText.length) {
      return false;
    }

    for (int index = 0; index < clearText.length; index++) {
      if (clearText[index] != otherPassword.clearText[index]) {
        return false;
      }
    }

    return true;
  }

  public boolean matches(DecryptedPassword partialPassword, Integer... matchAgainstIndexes) {
    return matches(partialPassword, Arrays.asList(matchAgainstIndexes));
  }

  public boolean matches(DecryptedPassword partialPassword, List<Integer> matchAgainstIndexes) {
    if (matchAgainstIndexes.isEmpty()) {
      throw new IllegalArgumentException("No password character indexes provided.");
    }

    if (partialPassword.clearText.length != matchAgainstIndexes.size()) {
      throw new IllegalArgumentException("Partial password length is different from the number of character indexes provided.");
    }

    // NOTE: Avoid storing any of the characters in temporary variables, to prevent them sticking around in memory longer than needed
    for (int otherIndex = 0; otherIndex < matchAgainstIndexes.size(); otherIndex++) {
      int index = matchAgainstIndexes.get(otherIndex);
      if ((index < 0) || (index >= clearText.length)) {
        throw new IllegalArgumentException("The specified password character index, " + index + ", is out of bounds for this password.");
      } else if (partialPassword.clearText[otherIndex] != clearText[index]) {
        return false;
      }
    }

    return true;
  }

  public char[] getClearText() {
    return clearText;
  }

  public void erase() {
    erase(clearText);
  }

  public static void erase(char[] clearText) {
    if (clearText != null) {
      for (int index = 0; index < clearText.length; index++) {
        // TODO: Do multiple writes of random characters first? How strict do we need to be here?
        clearText[index] = '\0';
      }
    }
  }
}
