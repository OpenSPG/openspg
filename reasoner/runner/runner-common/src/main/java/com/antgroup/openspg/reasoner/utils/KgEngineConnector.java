/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import com.antgroup.openspg.reasoner.common.http.AkgHttpClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengqiang.cq
 * @version $Id: KgEngineMetrics.java, v 0.1 2021-06-08 3:09 下午 chengqiang.cq Exp $$
 */
@Slf4j(topic = "userlogger")
public class KgEngineConnector {
    /**
     * save result
     * @param serverUrl
     * @param queryId
     * @param result
     */
    public static void saveResult(String serverUrl, String queryId, String result) {
        // construct with params
        Map<String, Object> params = new HashMap<>();
        params.put("key", queryId);
        params.put("result", result);
        try {
            // do post
            AkgHttpClient.HttpResult ret = HttpInvoker.doPostWithJSON(serverUrl + "/dispatch/task/result/save", params, "UTF-8", 3000, 3000);
            if (ret.code != 200) {
                log.error("reportEvtDelay error, {}", ret.content);
            }
            log.info("saveResult queryId={},response={}, post={}", queryId, ret.content, JSONObject.toJSONString(params));
        } catch (IOException ex) {
            log.error("reportEvtDelay error, ", ex);
        }
    }

    /**
     * report mertics info
     * @param metricsName
     * @param args
     */
    public static void report(String serverUrl, String metricsName, Object... args) {
        Map<String, String> params = new HashMap<>();
        List<Object> content = new ArrayList<>(args.length + 1);
        content.add(metricsName);
        for (int l = 0; l < args.length; l++) {
            content.add(args[l]);
        }
        params.put("metrics", JSONObject.toJSONString(content));
        try {
            AkgHttpClient.HttpResult result = HttpInvoker.doPostWithParameters(serverUrl+"/dispatch/task/metrics", params, "UTF-8",
                    3000, 3000);
            if (result.code != 200) {
                log.error("reportEvtDelay error, {}", result.content);
            }
            log.info("post={}, response={}", JSONObject.toJSONString(params), result.content);
        } catch (Exception ex) {
            log.error("reportEvtDelay error, ", ex);
        }
    }
}