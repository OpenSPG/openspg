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

package com.antgroup.openspg.reasoner.common;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.exception.SystemError;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.table.Field;
import com.antgroup.openspg.reasoner.common.table.FieldType;
import com.antgroup.openspg.reasoner.common.types.KTArray;
import com.antgroup.openspg.reasoner.common.types.KTBoolean$;
import com.antgroup.openspg.reasoner.common.types.KTCharacter$;
import com.antgroup.openspg.reasoner.common.types.KTDate$;
import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTList;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import scala.collection.JavaConversions;

public class Utils {
  private static final Random RANDOM = new Random();

  /**
   * obj value to str
   *
   * @param value
   * @return
   */
  public static String objValue2Str(Object value) {
    if (value instanceof Integer || value instanceof Double || value instanceof Float) {
      return new BigDecimal(value.toString()).toPlainString();
    }
    return String.valueOf(value);
  }
  /** convert java type to KgType */
  public static KgType javaType2KgType(String typeName) {
    switch (typeName) {
      case "java.lang.Object":
      case "java.util.HashMap":
        return KTObject$.MODULE$;
      case "java.lang.String":
        return KTString$.MODULE$;
      case "java.lang.Integer":
      case "int":
        return KTInteger$.MODULE$;
      case "java.lang.Long":
      case "long":
        return KTLong$.MODULE$;
      case "java.lang.Double":
      case "double":
        return KTDouble$.MODULE$;
      case "java.lang.Boolean":
      case "boolean":
        return KTBoolean$.MODULE$;
      case "java.lang.Character":
        return KTCharacter$.MODULE$;
      case "java.util.Date":
        return KTDate$.MODULE$;
      case "java.util.List<java.lang.Object>":
      case "java.util.List":
        return new KTList(KTObject$.MODULE$);
      case "java.util.List<null>":
        return new KTList(null);
      case "java.util.List<java.lang.String>":
        return new KTList(KTString$.MODULE$);
      case "java.util.List<java.lang.Integer>":
        return new KTList(KTInteger$.MODULE$);
      case "java.util.List<java.lang.Long>":
        return new KTList(KTLong$.MODULE$);
      case "java.util.List<java.lang.Double>":
        return new KTList(KTDouble$.MODULE$);
      case "java.util.List<java.lang.Boolean>":
        return new KTList(KTBoolean$.MODULE$);
      case "java.util.List<java.lang.Character>":
        return new KTList(KTCharacter$.MODULE$);
      case "java.util.List<java.util.Date>":
        return new KTList(KTDate$.MODULE$);
      case "java.lang.Object[]":
      case "[Ljava.lang.Object;":
        return new KTArray(KTObject$.MODULE$);
      case "java.lang.String[]":
      case "[Ljava.lang.String;":
        return new KTArray(KTString$.MODULE$);
      case "java.lang.Integer[]":
      case "[Ljava.lang.Integer;":
        return new KTArray(KTInteger$.MODULE$);
      case "java.lang.Long[]":
      case "[Ljava.lang.Long;":
        return new KTArray(KTLong$.MODULE$);
      case "java.lang.Double[]":
      case "[Ljava.lang.Double;":
        return new KTArray(KTDouble$.MODULE$);
      case "java.lang.Boolean[]":
      case "[Ljava.lang.Boolean;":
        return new KTArray(KTBoolean$.MODULE$);
      case "java.lang.Character[]":
      case "[Ljava.lang.Character;":
        return new KTArray(KTCharacter$.MODULE$);
      case "java.util.Date[]":
      case "[Ljava.util.Date;":
        return new KTArray(KTDate$.MODULE$);
      default:
//        throw new RuntimeException("unsupported type " + typeName);
        return KTObject$.MODULE$;
    }
  }

  /** delete file */
  public static void deletePath(String path) {
    File file = new File(path);
    if (file.exists()) {
      if (file.isDirectory()) {
        try {
          FileUtils.deleteDirectory(file);
        } catch (Exception e) {
          throw new SystemError("delete path error, path=" + path, e);
        }
      } else {
        try {
          file.delete();
        } catch (Exception e) {
          throw new SystemError("delete file error, " + path, e);
        }
      }
    }
  }

  /** create new file */
  public static void createFile(String fileName) {
    File file = new File(fileName);
    File parent = file.getParentFile();
    if (!parent.exists()) {
      parent.mkdirs();
    }
    try {
      file.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException("create new file error, file name " + fileName, e);
    }
  }

  /** random log */
  public static boolean randomLog() {
    return Math.abs(RANDOM.nextInt()) % 10000 == 0;
  }

  /** random log */
  public static boolean randomLog(int probability) {
    return Math.abs(RANDOM.nextInt()) % probability == 0;
  }

  public static int randomInt(int min, int max) {
    return Math.abs(RANDOM.nextInt()) % (max - min) + min;
  }

  /** return reverse direction */
  public static Direction reverseDirection(Direction direction) {
    if (Direction.BOTH.equals(direction)) {
      throw new IllegalArgumentException("in or out", "both", "", null);
    }
    return Direction.IN.equals(direction) ? Direction.OUT : Direction.IN;
  }

  /** check two byte[] prefix is same */
  public static boolean compareByteArrayPrefix(byte[] shorter, byte[] longer) {
    if (shorter == null || longer == null) {
      return false;
    }
    if (shorter.length > longer.length) {
      byte[] temp = shorter;
      shorter = longer;
      longer = temp;
    }
    for (int i = 0; i < shorter.length; i++) {
      if (shorter[i] != longer[i]) {
        return false;
      }
    }
    return true;
  }

  /** sleep */
  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // pass
    }
  }

  /** murmur hash */
  public static long hash64(final byte[] data, int length) {
    return hash64(data, length, 0xc70f6907);
  }

  private static long hash64(final byte[] data, int length, int seed) {
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

  /** get table result columns */
  public static List<Field> getResultTableColumns(List<String> asList, List<KgType> typeList) {
    List<Field> result = new ArrayList<>(asList.size());
    for (int i = 0; i < asList.size(); ++i) {
      String columnName = asList.get(i);
      String fixedColumnName = fixColumnName(columnName);

      KgType type = getTypeFromTypeList(typeList, i);
      FieldType fieldType = FieldType.fromKgType(type);
      Field field = new Field(fixedColumnName, fieldType);
      result.add(field);
    }
    return result;
  }

  /** get table result columns */
  public static scala.collection.immutable.List<Field> getResultTableColumns(
      scala.collection.immutable.List<String> asList,
      scala.collection.immutable.List<KgType> typeList) {
    List<String> javaAsList = JavaConversions.seqAsJavaList(asList);
    List<KgType> javaTypeList = JavaConversions.seqAsJavaList(typeList);
    return JavaConversions.iterableAsScalaIterable(getResultTableColumns(javaAsList, javaTypeList))
        .toList();
  }

  private static KgType getTypeFromTypeList(List<KgType> typeList, int index) {
    if (CollectionUtils.isEmpty(typeList)) {
      return null;
    }
    if (index < typeList.size()) {
      return typeList.get(index);
    }
    return null;
  }

  private static String fixColumnName(String columnName) {
    return columnName.replace('.', '_').toLowerCase(Locale.ROOT);
  }

  /** force output string */
  public static boolean getForceOutputString(Map<String, Object> params) {
    if (null == params) {
      return false;
    }
    Object forceOutputString =
        params.getOrDefault(Constants.KG_REASONER_OUTPUT_COLUMN_FORCE_STRING, "false");
    return !"false".equalsIgnoreCase(String.valueOf(forceOutputString));
  }

  /** force output string */
  public static boolean getForceOutputString(
      scala.collection.immutable.Map<String, Object> params) {
    if (null == params) {
      return false;
    }
    return getForceOutputString(JavaConversions.mapAsJavaMap(params));
  }

  /** convert kvs to property map */
  public static Map<String, Object> convert2Property(Object... kvs) {
    Preconditions.checkArgument(kvs.length % 2 == 0, "The number of config kv should be even.");
    Map<String, Object> property = new HashMap<>();
    for (int i = 0; i < kvs.length; i = i + 2) {
      property.put(String.valueOf(kvs[i]), kvs[i + 1]);
    }
    return property;
  }
}
