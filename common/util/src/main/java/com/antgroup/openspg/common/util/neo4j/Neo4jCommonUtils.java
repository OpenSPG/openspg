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
package com.antgroup.openspg.common.util.neo4j;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

public class Neo4jCommonUtils {

  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String DATABASE = "database";
  public static final String ID = "id";
  public static final String TYPE = "type";
  public static final String SCORE = "score";
  public static final String LABELS_KEY = "__labels__";
  public static final String GENERIC_ENTITY_LABEL = "Entity";
  public static final int DEFAULT_VECTOR_DIMENSIONS = 768;
  public static final String DEFAULT_METRIC_TYPE = "cosine";

  private static final String SIMPLE_IDENTIFIER = "[A-Za-z_][A-Za-z0-9_]*";
  private static final Pattern SIMPLE_IDENTIFIER_PATTERN = Pattern.compile(SIMPLE_IDENTIFIER);

  private static final String PASCAL_WORD = "[A-Za-z][a-z0-9]*";
  private static final Pattern PASCAL_WORD_PATTERN = Pattern.compile(PASCAL_WORD);

  private static final String LUCENE_SPECIAL_CHARS = "\\+-!():^[]\"{}~*?|&/";
  private static final Pattern LUCENE_SPECIAL_CHARS_PATTERN =
      getLuceneSpecialCharsPattern(LUCENE_SPECIAL_CHARS);

  public static String escapeNeo4jIdentifier(@NonNull String name) {
    Matcher matcher = SIMPLE_IDENTIFIER_PATTERN.matcher(name);
    boolean isSimpleIdentifier = matcher.matches();
    if (isSimpleIdentifier) return name;
    StringBuilder sb = new StringBuilder();
    sb.append('`');
    int[] codepoints = getUtf16Codepoints(name);
    for (int ch : codepoints) {
      if (ch == '`') sb.append("``");
      else if (' ' <= ch && ch <= 0x7e) sb.append((char) ch);
      else sb.append(String.format("\\u%04X", ch));
    }
    sb.append('`');
    return sb.toString();
  }

  public static String escapeNeo4jStringLiteral(@NonNull String value) {
    StringBuilder sb = new StringBuilder();
    sb.append('"');
    int[] codepoints = getUtf16Codepoints(value);
    for (int ch : codepoints) {
      switch (ch) {
        case '\t':
          sb.append("\\t");
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\'':
          sb.append("'");
          break;
        case '"':
          sb.append("\\\"");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        default:
          if (' ' <= ch && ch <= 0x7e) sb.append((char) ch);
          else sb.append(String.format("\\u%04X", ch));
          break;
      }
    }
    sb.append('"');
    return sb.toString();
  }

  private static int[] getUtf16Codepoints(@NonNull String str) {
    byte[] data = str.getBytes(StandardCharsets.UTF_16LE);
    int[] result = new int[data.length / 2];
    for (int i = 0; i < result.length; i++) {
      int lowByte = data[2 * i] & 0xff;
      int highByte = data[2 * i + 1] & 0xff;
      result[i] = highByte << 8 | lowByte;
    }
    return result;
  }

  public static String toSnakeCase(String name) {
    StringBuilder sb = new StringBuilder();
    Matcher matcher = PASCAL_WORD_PATTERN.matcher(name);
    while (matcher.find()) {
      if (sb.length() > 0) sb.append('_');
      sb.append(matcher.group().toLowerCase());
    }
    return sb.toString();
  }

  private static String escapeRegexSpecialChars(String str) {
    str = str.replace("\\", "\\\\");
    str = str.replace("[", "\\[");
    str = str.replace("]", "\\]");
    str = str.replace("-", "\\-");
    str = str.replace("^", "\\^");
    return str;
  }

  private static Pattern getLuceneSpecialCharsPattern(String str) {
    str = escapeRegexSpecialChars(str);
    str = "([" + str + "])";
    return Pattern.compile(str);
  }

  private static String escapeLuceneSpecialChars(String str) {
    Matcher matcher = LUCENE_SPECIAL_CHARS_PATTERN.matcher(str);
    str = matcher.replaceAll("\\\\$1");
    return str;
  }

  public static String makeLuceneQuery(@NonNull String str) {
    str = escapeLuceneSpecialChars(str);
    return str.toLowerCase();
  }

  public static String addPrefix(@NonNull String original, @NonNull String prefix) {
    // Check if the original string starts with the specified prefix
    if (original.startsWith(prefix)) {
      // If the original string starts with the prefix, return it as is
      return original;
    }
    // Return string with prefix
    return prefix + original;
  }

  public static String removePrefix(@NonNull String original, @NonNull String prefix) {
    // Check if the original string starts with the specified prefix
    if (original.startsWith(prefix)) {
      // Return the substring starting from the length of the prefix
      return original.substring(prefix.length());
    }
    // If the original string does not start with the prefix, return it as is
    return original;
  }
}
