package com.antgroup.openspgapp.common.util.utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/utils/AESUtils.class */
public final class AESUtils {
  private static final String ALGORITHM = "AES";
  private static final String CTR_NO_PADDING = "AES/CTR/NoPadding";
  private static final byte[] IV = {
    -100, -13, 3, -75, -61, 80, -45, 65, -56, 28, 51, 62, 16, -9, 109, -2
  };

  private AESUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String encryptWithCTR(String content, String secretKey) {
    if (StringUtils.isBlank(content)) {
      return content;
    }
    try {
      Cipher cipher = Cipher.getInstance(CTR_NO_PADDING);
      IvParameterSpec ivSpec = new IvParameterSpec(IV);
      cipher.init(1, getSecretKey(secretKey), ivSpec);
      byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
      byte[] result = cipher.doFinal(byteContent);
      return Base64.getEncoder().encodeToString(result);
    } catch (Exception ex) {
      throw new RuntimeException("ctr encrypt error", ex);
    }
  }

  public static String decryptWithCTR(String content, String secretKey) {
    if (StringUtils.isBlank(content)) {
      return content;
    }
    try {
      Cipher cipher = Cipher.getInstance(CTR_NO_PADDING);
      IvParameterSpec ivSpec = new IvParameterSpec(IV);
      cipher.init(2, getSecretKey(secretKey), ivSpec);
      byte[] decode = Base64.getDecoder().decode(content);
      byte[] result = cipher.doFinal(decode);
      return new String(result, StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new RuntimeException("ctr decrypt error", ex);
    }
  }

  public static SecretKeySpec getSecretKey(final String password) {
    if (StringUtils.isBlank(password)) {
      return null;
    }
    try {
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(password.getBytes());
      KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
      kg.init(128, random);
      SecretKey secretKey = kg.generateKey();
      return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException("ecb getSecretKey error password:" + password, ex);
    }
  }
}
