package com.antgroup.openspgapp.common.util.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/utils/SimpleHttpClient.class */
public class SimpleHttpClient {
  private static final Logger log = LoggerFactory.getLogger(SimpleHttpClient.class);
  private static final int CONNECT_TIMEOUT = 15000;
  private static final int READ_TIMEOUT = 15000;
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String COOKIE = "Cookie";
  public static final String REFERER = "Referer";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String APPLICATION_JSON = "application/json";
  public static final String JSON_UTF8 = "application/json;charset=UTF-8";

  public static HttpResult doGet(
      String url, Map<String, String> headers, Map<String, String> params, String encoding)
      throws IOException {
    String encodedContent = encodingParams(params, encoding);
    String url2 =
        url
            + ((null == encodedContent || StringUtils.isBlank(encodedContent))
                ? ""
                : "?" + encodedContent);
    log.debug("[begin doGet send http],url={}", url2);
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) new URL(url2).openConnection();
      conn.setRequestMethod(GET);
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      if (!headers.isEmpty()) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
      }
      conn.connect();
      int respCode = conn.getResponseCode();
      String resp = "";
      if (200 == respCode) {
        resp = IOUtils.toString(conn.getInputStream(), encoding);
      } else if (null != conn.getErrorStream()) {
        resp = IOUtils.toString(conn.getErrorStream(), encoding);
      }
      log.debug("[end doGet send http],respCode={}", Integer.valueOf(respCode));
      HttpResult httpResult = new HttpResult(respCode, resp);
      if (conn != null) {
        conn.disconnect();
      }
      return httpResult;
    } catch (Throwable th) {
      if (conn != null) {
        conn.disconnect();
      }
      throw th;
    }
  }

  public static HttpResult doPost(
      String url, Map<String, String> headers, String body, String encoding) throws IOException {
    String resp;
    log.debug("[begin doPost send http],url={}", body);
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setRequestMethod(POST);
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      if (!headers.isEmpty()) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
      }
      OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), encoding);
      Throwable th = null;
      try {
        try {
          out.write(body);
          out.flush();
          if (out != null) {
            if (0 != 0) {
              try {
                out.close();
              } catch (Throwable th2) {
                th.addSuppressed(th2);
              }
            } else {
              out.close();
            }
          }
          int respCode = conn.getResponseCode();
          log.debug("[end doPost send http],respCode={}", Integer.valueOf(respCode));
          if (200 == respCode) {
            resp = IOUtils.toString(conn.getInputStream(), encoding);
          } else {
            resp = IOUtils.toString(conn.getErrorStream(), encoding);
          }
          HttpResult httpResult = new HttpResult(respCode, resp);
          if (null != conn) {
            conn.disconnect();
          }
          return httpResult;
        } finally {
        }
      } finally {
      }
    } catch (Throwable th3) {
      if (null != conn) {
        conn.disconnect();
      }
      throw th3;
    }
  }

  private static String encodingParams(Map<String, String> paramValues, String encoding)
      throws UnsupportedEncodingException {
    if (null == paramValues) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    Iterator<Map.Entry<String, String>> iter = paramValues.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, String> entry = iter.next();
      sb.append(entry.getKey()).append("=");
      sb.append(URLEncoder.encode(entry.getValue(), encoding));
      if (iter.hasNext()) {
        sb.append("&");
      }
    }
    return sb.toString();
  }

  /* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/utils/SimpleHttpClient$HttpResult.class */
  public static class HttpResult {
    public final int code;
    public final String content;

    public HttpResult(int code, String content) {
      this.code = code;
      this.content = content;
    }

    public boolean isOk() {
      return this.code == 200;
    }
  }
}
