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
package com.antgroup.openspg.server.infra.dao;

import org.jasypt.util.text.BasicTextEncryptor;

public class JasyptUtils {

  private static final String ENC_PREFIX = "ENC(";
  private static final String ENC_SUFFIX = ")";

  public static String decryptIfEncrypted(String value, String secretKey) {
    if (value != null && value.startsWith(ENC_PREFIX) && value.endsWith(ENC_SUFFIX)) {
      String encryptedValue =
          value.substring(ENC_PREFIX.length(), value.length() - ENC_SUFFIX.length());
      BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
      basicTextEncryptor.setPassword(secretKey);
      return basicTextEncryptor.decrypt(encryptedValue);
    }
    return value;
  }
}
