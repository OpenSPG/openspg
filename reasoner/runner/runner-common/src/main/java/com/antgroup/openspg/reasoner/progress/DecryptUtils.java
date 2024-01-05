/*
 * Copyright 2023 Ant Group CO., Ltd.
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
package com.antgroup.openspg.reasoner.progress;

import java.io.Serializable;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecryptUtils implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(DecryptUtils.class);

  private static final String key = "triedthisfunctio";

  public static String encryptAccessInfo(String info) {
    try {
      Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES");
      // encrypt the text
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      byte[] encrypted = cipher.doFinal(info.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      log.error("encrypt error", e);
    }
    return null;
  }

  public static String decryptAccessInfo(String code) {
    // Create key and cipher
    try {
      byte[] codeBytes = Base64.getDecoder().decode(code);
      Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES");
      // decrypt the text
      cipher.init(Cipher.DECRYPT_MODE, aesKey);
      return new String(cipher.doFinal(codeBytes));
    } catch (Exception e) {
      log.error("decrypt error", e);
    }
    return null;
  }
}
