package com.antgroup.openspgapp.core.reasoner.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/utils/Utils.class */
public class Utils {
  public static Map<String, String> parseParams(String params) {
    Map<String, String> rst = new HashMap<>();
    JSONObject jsonObject = JSON.parseObject(params);
    for (String key : jsonObject.keySet()) {
      rst.put(key, jsonObject.getString(key));
    }
    return rst;
  }

  public static String checkVectorizer(DefaultValue reasonerValue, JSONObject config) {
    if (config == null || !config.containsKey("vectorizer")) {
      return "";
    }
    JSONObject vectorizerConfig = config.getJSONObject("vectorizer");
    PemjaConfig pemjaConfig =
        new PemjaConfig(
            reasonerValue.getPythonExec(),
            reasonerValue.getPythonPaths(),
            reasonerValue.getPythonEnv(),
            reasonerValue.getSchemaUrlHost(),
            (Long) null,
            PythonInvokeMethod.BRIDGE_VECTORIZER_CHECKER,
            Maps.newHashMap());
    Object result =
        PemjaUtils.invoke(pemjaConfig, new Object[] {JSON.toJSONString(vectorizerConfig)});
    return result.toString();
  }

  public static String checkLLM(DefaultValue reasonerValue, JSONObject config) {
    if (config == null || !config.containsKey("llm")) {
      return "";
    }
    JSONObject llm = config.getJSONObject("llm");
    PemjaConfig pemjaConfig =
        new PemjaConfig(
            reasonerValue.getPythonExec(),
            reasonerValue.getPythonPaths(),
            reasonerValue.getPythonEnv(),
            reasonerValue.getSchemaUrlHost(),
            (Long) null,
            PythonInvokeMethod.BRIDGE_LLM_CHECKER,
            Maps.newHashMap());
    Object result = PemjaUtils.invoke(pemjaConfig, new Object[] {llm.toJSONString()});
    return result.toString();
  }

  public static String checkLLMSelect(DefaultValue reasonerValue, JSONObject config) {
    JSONArray llmSelect = config.getJSONArray("llm_select");
    if (llmSelect == null) {
      llmSelect = new JSONArray();
    }
    PemjaConfig pemjaConfig =
        new PemjaConfig(
            reasonerValue.getPythonExec(),
            reasonerValue.getPythonPaths(),
            reasonerValue.getPythonEnv(),
            reasonerValue.getSchemaUrlHost(),
            (Long) null,
            PythonInvokeMethod.BRIDGE_LLM_CHECKER,
            Maps.newHashMap());
    StringBuilder resultBuilder = new StringBuilder();
    for (int i = 0; i < llmSelect.size(); i++) {
      Object result =
          PemjaUtils.invoke(pemjaConfig, new Object[] {llmSelect.getJSONObject(i).toJSONString()});
      resultBuilder.append(result.toString());
    }
    return resultBuilder.toString();
  }
}
