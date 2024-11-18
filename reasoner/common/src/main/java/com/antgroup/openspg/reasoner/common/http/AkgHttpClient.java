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

package com.antgroup.openspg.reasoner.common.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class AkgHttpClient {
  public static final int MAX_RETRY_TIMES = 10;

  private AkgHttpClient() {};

  /**
   * 发送GET请求
   *
   * @param url
   * @param paramValues
   * @param encoding
   * @param connectTimeoutMs
   * @param readTimeoutMs
   * @return
   * @throws IOException
   */
  public static HttpResult doGet(
      String url,
      Map<String, String> headers,
      Map<String, String> paramValues,
      String encoding,
      int connectTimeoutMs,
      int readTimeoutMs) {
    try {
      String encodedContent = encodingParams(paramValues, encoding);
      url += (null == encodedContent) ? "" : ("?" + encodedContent);
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }

    HttpURLConnection conn = null;
    for (int i = 0; i < MAX_RETRY_TIMES; i++) {
      try {
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(connectTimeoutMs > 100 ? connectTimeoutMs : 100);
        conn.setReadTimeout(readTimeoutMs);
        if (headers != null && !headers.isEmpty()) {
          for (String key : headers.keySet()) {
            conn.setRequestProperty(key, headers.get(key));
          }
        }
        conn.connect();
        int respCode = conn.getResponseCode(); // 这里内部发送请求
        String resp = "";

        if (HttpURLConnection.HTTP_OK == respCode) {
          resp = IOUtils.toString(conn.getInputStream(), encoding);
        } else {
          if (conn.getErrorStream() != null) {
            resp = IOUtils.toString(conn.getErrorStream(), encoding);
          }
          throw new IOException(resp);
        }
        return new HttpResult(respCode, resp);
      } catch (IOException ex) {
        log.warn("http invoke " + url + "  error,", ex);
        try {
          Thread.sleep((i + 1) * 1000);
        } catch (InterruptedException e) {
        }
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
      }
    }
    log.error("AkgHttpClient doGet failed after {} times", MAX_RETRY_TIMES);
    throw new RuntimeException("http get invoke error.");
  }

  /**
   * 发送POST请求。
   *
   * @param url
   * @param paramValues 参数，会被转化为json
   * @param encoding URL编码使用的字符集
   * @param connectTimeoutMs 创建连接的超时时间
   * @param readTimeoutMs 响应超时
   * @return
   * @throws IOException
   */
  public static HttpResult doPost(
      String url,
      Map<String, String> paramValues,
      String encoding,
      int connectTimeoutMs,
      int readTimeoutMs)
      throws IOException {
    HttpURLConnection conn = null;
    for (int i = 0; i < MAX_RETRY_TIMES; i++) {
      try {
        String encodedContent = encodingParams(paramValues);
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(connectTimeoutMs > 3000 ? connectTimeoutMs : 3000);
        conn.setReadTimeout(readTimeoutMs);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        conn.getOutputStream().write(encodedContent.getBytes(encoding));

        int respCode = conn.getResponseCode(); // 这里内部发送请求
        String resp = null;

        if (HttpURLConnection.HTTP_OK == respCode) {
          resp = IOUtils.toString(conn.getInputStream(), encoding);
        } else {
          if (conn.getErrorStream() != null) {
            resp = IOUtils.toString(conn.getErrorStream(), encoding);
          }
          throw new IOException(resp);
        }
        return new HttpResult(respCode, resp);
      } catch (IOException ex) {
        log.warn("http invoke error, ", ex);
        try {
          Thread.sleep((i + 1) * 1000);
        } catch (InterruptedException e) {
        }
      } finally {
        if (null != conn) {
          conn.disconnect();
        }
      }
    }
    log.error("AkgHttpClient doPost failed after {} times", MAX_RETRY_TIMES);
    throw new IOException("http get invoke error.");
  }

  private static String encodingParams(Map<String, String> params) {
    List<String> kvs = new ArrayList<>(params.size());
    for (String k : params.keySet()) {
      kvs.add(k + "=" + URLEncoder.encode(params.get(k)));
    }
    return StringUtils.join(kvs, "&");
  }

  private static String encodingParams(Map<String, String> paramValues, String encoding)
      throws UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder();
    if (null == paramValues) {
      return null;
    }

    for (Iterator<Entry<String, String>> iter = paramValues.entrySet().iterator();
        iter.hasNext(); ) {
      Entry<String, String> entry = iter.next();
      sb.append(entry.getKey()).append("=");
      sb.append(URLEncoder.encode(entry.getValue(), encoding));
      if (iter.hasNext()) {
        sb.append("&");
      }
    }
    return sb.toString();
  }

  public static class HttpResult {

    /** 返回码 */
    public final int code;

    /** 返回内容 */
    public final String content;

    /**
     * 构造方法
     *
     * @param code
     * @param content
     */
    public HttpResult(int code, String content) {
      this.code = code;
      this.content = content;
    }
  }
}
