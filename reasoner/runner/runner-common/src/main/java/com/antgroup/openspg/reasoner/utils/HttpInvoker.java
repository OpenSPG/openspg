/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.antgroup.openspg.reasoner.common.http.AkgHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


@Slf4j(topic = "userlogger")
public class HttpInvoker {
    /**
     * do post content
     * @param url
     * @param encodedContentByte
     * @param encoding
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @param isJson
     * @return
     * @throws IOException
     */
    public static AkgHttpClient.HttpResult doPostWithContent(String url, byte encodedContentByte[],
                                                             String encoding, int connectTimeoutMs,
                                                             int readTimeoutMs, boolean isJson) throws IOException {

        HttpURLConnection conn = null;
        try {
            // http config
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeoutMs > 3000 ? connectTimeoutMs : 3000);
            conn.setReadTimeout(readTimeoutMs);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // header config
            conn.setRequestProperty("Accept", "application/json");
            if (isJson) {
                conn.setRequestProperty("Content-Type", "application/json");
            } else {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }

            conn.getOutputStream().write(encodedContentByte);

            int respCode = conn.getResponseCode(); // 这里内部发送请求
            String resp = null;

            // result check
            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IOUtils.toString(conn.getInputStream(), encoding);
            } else {
                if (conn.getErrorStream() != null) {
                    resp = IOUtils.toString(conn.getErrorStream(), encoding);
                } else {
                    resp = IOUtils.toString(conn.getInputStream(), encoding);
                }
            }
            return new AkgHttpClient.HttpResult(respCode, resp);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    /**
     * post with json format
     * @param url
     * @param paramValues
     * @param encoding
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     * @throws IOException
     */
    public static AkgHttpClient.HttpResult doPostWithJSON(String url, Map<String, Object> paramValues,
                                                  String encoding, int connectTimeoutMs,
                                                  int readTimeoutMs) throws IOException {

        String encodedContent = JSON.toJSONString(paramValues);
        return doPostWithContent(url, encodedContent.getBytes(), encoding, connectTimeoutMs, readTimeoutMs, true);
    }

    /**
     * post with url encode format
     * @param url
     * @param paramValues
     * @param encoding
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     * @throws IOException
     */
    public static AkgHttpClient.HttpResult doPostWithParameters(String url, Map<String, String> paramValues,
                                                  String encoding, int connectTimeoutMs,
                                                  int readTimeoutMs) throws IOException {

        String encodedContent = encodingUrlParameters(paramValues);
        return doPostWithContent(url, encodedContent.getBytes(StandardCharsets.UTF_8), encoding, connectTimeoutMs, readTimeoutMs, false);
    }

    /**
     * encoding url
     * @param params
     * @return
     */
    private static String encodingUrlParameters(Map<String, String> params) {
        List<String> kvs = new ArrayList<>(params.size());
        for (String k : params.keySet()) {
            kvs.add(k + "=" + URLEncoder.encode(params.get(k)));
        }
        return StringUtils.join(kvs, "&");
    }
}