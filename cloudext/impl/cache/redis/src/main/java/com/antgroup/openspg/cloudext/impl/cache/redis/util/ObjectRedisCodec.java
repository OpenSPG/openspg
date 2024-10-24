package com.antgroup.openspg.cloudext.impl.cache.redis.util;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/** The type Object redis codec. */
public class ObjectRedisCodec implements RedisCodec<String, Object> {
  private final ByteArrayCodec byteArrayCodec = new ByteArrayCodec();

  @Override
  public String decodeKey(ByteBuffer byteBuffer) {
    return StandardCharsets.UTF_8.decode(byteBuffer).toString();
  }

  @Override
  public Object decodeValue(ByteBuffer bytes) {
    try (ObjectInputStream is =
        new ObjectInputStream(new ByteArrayInputStream(byteArrayCodec.decodeValue(bytes)))) {
      return is.readObject();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public ByteBuffer encodeKey(String s) {
    return StandardCharsets.UTF_8.encode(s);
  }

  @Override
  public ByteBuffer encodeValue(Object o) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos)) {
      os.writeObject(o);
      return byteArrayCodec.encodeValue(bos.toByteArray());
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
