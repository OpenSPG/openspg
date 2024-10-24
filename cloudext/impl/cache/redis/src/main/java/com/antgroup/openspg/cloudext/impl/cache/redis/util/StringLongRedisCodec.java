package com.antgroup.openspg.cloudext.impl.cache.redis.util;

import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/** The type String long redis codec. */
public class StringLongRedisCodec implements RedisCodec<String, Long> {

  @Override
  public String decodeKey(ByteBuffer bytes) {
    return StandardCharsets.UTF_8.decode(bytes).toString();
  }

  @Override
  public Long decodeValue(ByteBuffer bytes) {
    CharBuffer charSequence = StandardCharsets.UTF_8.decode(bytes);
    return Long.parseLong(charSequence.toString(), 10);
  }

  @Override
  public ByteBuffer encodeKey(String key) {
    return StandardCharsets.UTF_8.encode(key);
  }

  @Override
  public ByteBuffer encodeValue(Long value) {
    return ByteBuffer.wrap(Long.toString(value).getBytes());
  }
}
