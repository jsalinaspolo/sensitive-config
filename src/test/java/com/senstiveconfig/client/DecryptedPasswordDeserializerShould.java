package com.senstiveconfig.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DecryptedPasswordDeserializerShould {

  @Test
  public void assertThatSerializedAndDeserializeObjectsAreEqual() {
    DecryptedPassword underTest = new DecryptedPassword("text".toCharArray());
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(out, underTest);
      DecryptedPassword serializedObject = objectMapper.readValue(new ByteArrayInputStream(out.toByteArray()), DecryptedPassword.class);

      assertThat(serializedObject.getClearText()).isEqualTo(underTest.getClearText());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
