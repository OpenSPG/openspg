/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
package com.antgroup.openspg.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ECBUtil {

  private static final String KEY_ALGORITHM = "AES";

  private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

  public static String encrypt(String content, String password) {
    if (StringUtils.isBlank(content)) {
      return content;
    }
    try {
      Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
      byte[] byteContent = content.getBytes("utf-8");
      cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
      byte[] result = cipher.doFinal(byteContent);
      return Base64.encodeBase64String(result);
    } catch (Exception ex) {
      log.error("ecb encrypt error", ex);
      throw new RuntimeException("ecb encrypt error", ex);
    }
  }

  public static String decrypt(String content, String password) {
    if (StringUtils.isBlank(content)) {
      return content;
    }
    try {
      Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
      byte[] result = cipher.doFinal(Base64.decodeBase64(content));
      return new String(result, "utf-8");
    } catch (Exception ex) {
      log.error("ecb decrypt error", ex);
      throw new RuntimeException("ecb decrypt error", ex);
    }
  }

  /**
   * 生成加密秘钥
   *
   * @return
   */
  private static SecretKeySpec getSecretKey(final String password) {
    if (StringUtils.isBlank(password)) {
      return null;
    }
    KeyGenerator kg;
    try {
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(password.getBytes());
      kg = KeyGenerator.getInstance(KEY_ALGORITHM);
      kg.init(128, random);
      SecretKey secretKey = kg.generateKey();
      return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    } catch (NoSuchAlgorithmException ex) {
      log.error("ecb getSecretKey error password:" + password, ex);
      throw new RuntimeException("ecb getSecretKey error password:" + password, ex);
    }
  }
}
