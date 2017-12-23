package com.senstiveconfig.client;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class DecryptedPasswordShould {
  private static final String PASSWORD = "secret";
  private char[] clearText;

  private DecryptedPassword decryptedPassword;

  @Before
  public void initialise() {
    clearText = PASSWORD.toCharArray();
    decryptedPassword = new DecryptedPassword(clearText);
  }

  @Test
  public void testMatchSuccess() {
    DecryptedPassword matchingPassword = new DecryptedPassword(PASSWORD.toCharArray());
    assertThat(decryptedPassword.matches(matchingPassword)).isTrue();
  }

  @Test
  public void testMatchFailureWithDifferentSizedPasswords() {
    DecryptedPassword nonMatchingPassword = new DecryptedPassword("badPassword".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPassword)).isFalse();
  }

  @Test
  public void testMatchFailureWithSameSizedPasswords() {
    DecryptedPassword nonMatchingPassword = new DecryptedPassword("secreT".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPassword)).isFalse();
  }

  @Test
  public void testPartialMatchSuccess() {
    DecryptedPassword matchingPartialPassword1 = new DecryptedPassword("sec".toCharArray());
    assertThat(decryptedPassword.matches(matchingPartialPassword1, 0, 1, 2)).isTrue();

    DecryptedPassword matchingPartialPassword2 = new DecryptedPassword("ret".toCharArray());
    assertThat(decryptedPassword.matches(matchingPartialPassword2, 3, 4, 5)).isTrue();

    DecryptedPassword matchingPartialPassword3 = new DecryptedPassword("ecr".toCharArray());
    assertThat(decryptedPassword.matches(matchingPartialPassword3, 1, 2, 3)).isTrue();

    DecryptedPassword matchingPartialPassword4 = new DecryptedPassword("sct".toCharArray());
    assertThat(decryptedPassword.matches(matchingPartialPassword4, 0, 2, 5)).isTrue();

    DecryptedPassword matchingPartialPassword5 = new DecryptedPassword("tcs".toCharArray());
    assertThat(decryptedPassword.matches(matchingPartialPassword5, 5, 2, 0)).isTrue();

    DecryptedPassword matchingPartialPassword6 = new DecryptedPassword("see".toCharArray());
    assertThat(decryptedPassword.matches(matchingPartialPassword6, 0, 1, 4)).isTrue();

    DecryptedPassword matchingPartialPassword7 = new DecryptedPassword("see".toCharArray());
    assertThat(decryptedPassword.matches(matchingPartialPassword7, 0, 4, 1)).isTrue();
  }

  @Test
  public void testPartialMatchFailure() {
    DecryptedPassword nonMatchingPartialPassword1 = new DecryptedPassword("sec".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPartialPassword1, 0, 1, 3)).isFalse();

    DecryptedPassword nonMatchingPartialPassword2 = new DecryptedPassword("xyz".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPartialPassword2, 0, 1, 2)).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartialMatchWithTooFewMatchIndexes() {
    DecryptedPassword nonMatchingPartialPassword1 = new DecryptedPassword("sec".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPartialPassword1, 0, 1)).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartialMatchWithTooManyMatchIndexes() {
    DecryptedPassword nonMatchingPartialPassword1 = new DecryptedPassword("sec".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPartialPassword1, 0, 1, 2, 3)).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartialMatchWithZeroMatchIndexes() {
    DecryptedPassword nonMatchingPartialPassword1 = new DecryptedPassword("".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPartialPassword1, new ArrayList<>())).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartialMatchWithNegativeMatchIndex() {
    DecryptedPassword nonMatchingPartialPassword1 = new DecryptedPassword("sec".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPartialPassword1, -1, 1, 2)).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartialMatchWithTooBigMatchIndex() {
    DecryptedPassword nonMatchingPartialPassword2 = new DecryptedPassword("sec".toCharArray());
    assertThat(decryptedPassword.matches(nonMatchingPartialPassword2, 0, 1, 6)).isTrue();
  }

  @Test
  public void erasePassword() {
    decryptedPassword.erase();
    for (char clearTextCharacter : clearText) {
      assertThat(clearTextCharacter).isEqualTo('\0');
    }
    for (char clearTextCharacter : decryptedPassword.getClearText()) {
      assertThat(clearTextCharacter).isEqualTo('\0');
    }
  }
}
