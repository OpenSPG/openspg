/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

/**
 * @author kejian
 * @version MumUtil.java, v 0.1 2024年03月01日 4:47 PM kejian
 */
public class MumUtil {

  private MumUtil() {}

  /**
   * 根据 id 和 label hash
   *
   * @param bizId
   * @param label
   * @return
   */
  public static long toGlobalHashId(String bizId, String label) {
    String s = bizId + "_" + label.toLowerCase();
    return hash64(s);
  }

  /**
   * 从字节数组生成64位散列默认种子值.
   *
   * @param data byte array to hash
   * @param length length of the array to hash
   * @return 64 bit hash of the given string
   */
  public static long hash64(final byte[] data, int length) {
    return hash64(data, length, 0xc70f6907);
  }

  /**
   * Generates 64 bit hash from a string.
   *
   * @param text string to hash
   * @return 64 bit hash of the given string
   */
  public static long hash64(final String text) {
    final byte[] bytes = text.getBytes();
    return hash64(bytes, bytes.length);
  }

  /**
   * hash biz id
   *
   * @param text
   * @return
   */
  public static long hashBizId(final String text) {
    final byte[] bytes = text.getBytes();
    return hash64(bytes, bytes.length);
  }

  /**
   * 根据hashBizIdEnable计算hash值
   *
   * @return
   */
  public static long generateInternalId(String bizId, String label, boolean hashBizIdEnable) {
    // 干掉hashBizIdEnable,固定为true
    return hashBizId(bizId);
  }

  /**
   * @param bizId
   * @param seqId
   * @return
   */
  public static long generateInternalId(String bizId, Long seqId) {
    return null == seqId ? hashBizId(bizId) : seqId;
  }

  /**
   * 生成64位哈希子字符串
   *
   * @param text string to hash
   * @param from starting index
   * @param length length of the substring to hash
   * @return 64 bit hash of the given array
   */
  public static long hash64(final String text, int from, int length) {
    return hash64(text.substring(from, from + length));
  }

  /**
   * 生成64位从给定长度的字节数组和散列的种子.
   *
   * @param data byte array to hash
   * @param length length of the array to hash
   * @param seed initial seed value
   * @return 64 bit hash of the given array
   */
  @SuppressWarnings("all")
  public static long hash64(final byte[] data, int length, int seed) {
    final long m = 0xc6a4a7935bd1e995L;
    final int r = 47;

    long h = (seed & 0xffffffffL) ^ (length * m);

    int length8 = length / 8;

    for (int i = 0; i < length8; i++) {
      final int i8 = i * 8;
      long k =
          ((long) data[i8] & 0xff)
              + (((long) data[i8 + 1] & 0xff) << 8)
              + (((long) data[i8 + 2] & 0xff) << 16)
              + (((long) data[i8 + 3] & 0xff) << 24)
              + (((long) data[i8 + 4] & 0xff) << 32)
              + (((long) data[i8 + 5] & 0xff) << 40)
              + (((long) data[i8 + 6] & 0xff) << 48)
              + (((long) data[i8 + 7] & 0xff) << 56);

      k *= m;
      k ^= k >>> r;
      k *= m;

      h ^= k;
      h *= m;
    }

    // 位运算逻辑
    switch (length % 8) {
      case 7:
        h ^= (long) (data[(length & ~7) + 6] & 0xff) << 48;
      case 6:
        h ^= (long) (data[(length & ~7) + 5] & 0xff) << 40;
      case 5:
        h ^= (long) (data[(length & ~7) + 4] & 0xff) << 32;
      case 4:
        h ^= (long) (data[(length & ~7) + 3] & 0xff) << 24;
      case 3:
        h ^= (long) (data[(length & ~7) + 2] & 0xff) << 16;
      case 2:
        h ^= (long) (data[(length & ~7) + 1] & 0xff) << 8;
      case 1:
        h ^= (long) (data[length & ~7] & 0xff);
        h *= m;
      default:
    }

    h ^= h >>> r;
    h *= m;
    h ^= h >>> r;

    return h;
  }
}
