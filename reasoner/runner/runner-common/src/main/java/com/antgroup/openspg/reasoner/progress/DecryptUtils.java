/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.progress;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.util.Base64;


@Slf4j(topic = "userlogger")
public class DecryptUtils implements Serializable {

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