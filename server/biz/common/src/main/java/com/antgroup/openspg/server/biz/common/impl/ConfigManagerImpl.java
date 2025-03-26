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

package com.antgroup.openspg.server.biz.common.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.ModelType;
import com.antgroup.openspg.server.api.facade.dto.common.request.ConfigRequest;
import com.antgroup.openspg.server.biz.common.ConfigManager;
import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.common.service.config.ConfigRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigManagerImpl implements ConfigManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManagerImpl.class);

  @Autowired private ConfigRepository configRepository;

  @Override
  public Config query(String configId, String version) {
    return configRepository.query(configId, version);
  }

  @Override
  public Config getById(Long id) {
    return configRepository.getById(id);
  }

  @Override
  public Integer create(ConfigRequest request) {
    Config config = toModel(request);
    return configRepository.save(config);
  }

  @Override
  public Integer update(ConfigRequest request) {
    Config oldConfig = getById(request.getId());
    if (oldConfig == null) {
      return 0;
    }
    Config config = toModel(request);
    return configRepository.update(config);
  }

  @Override
  public String setApiKeyDesensitization(String configStr) {
    if (StringUtils.isBlank(configStr)) {
      return configStr;
    }
    JSONObject config = JSON.parseObject(configStr);
    if (config == null) {
      return "";
    }
    JSONObject vectorizerJson = config.getJSONObject(SpgAppConstant.VECTORIZER);
    if (vectorizerJson != null && vectorizerJson.containsKey(SpgAppConstant.API_KEY)) {
      vectorizerJson.put(SpgAppConstant.API_KEY, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY);
      config.put(SpgAppConstant.VECTORIZER, vectorizerJson);
    }
    JSONArray llmArray = config.getJSONArray(SpgAppConstant.LLM_SELECT);
    if (CollectionUtils.isNotEmpty(llmArray)) {
      for (int i = 0; i < llmArray.size(); i++) {
        JSONObject llm = llmArray.getJSONObject(i);
        if (llm == null) {
          continue;
        }
        if (llm.containsKey(SpgAppConstant.API_KEY)) {
          llm.put(SpgAppConstant.API_KEY, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY);
        }
        if (llm.containsKey(SpgAppConstant.KEY)) {
          llm.put(SpgAppConstant.KEY, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY);
        }
      }
      config.put(SpgAppConstant.LLM_SELECT, llmArray);
    }
    JSONObject llm = config.getJSONObject(SpgAppConstant.LLM);
    if (llm != null) {
      if (llm.containsKey(SpgAppConstant.API_KEY)) {
        llm.put(SpgAppConstant.API_KEY, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY);
      }
      if (llm.containsKey(SpgAppConstant.KEY)) {
        llm.put(SpgAppConstant.KEY, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY);
      }
    }
    JSONObject graphStore = config.getJSONObject(SpgAppConstant.GRAPH_STORE);
    if (graphStore != null) {
      graphStore.put(SpgAppConstant.PASSWORD, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY);
      config.put(SpgAppConstant.GRAPH_STORE, graphStore);
    }
    return config.toJSONString();
  }

  @Override
  public void handleApiKey(JSONObject config, String oldConfig) {
    if (config == null || StringUtils.isBlank(oldConfig)) {
      return;
    }
    JSONObject oldConfigJson = JSON.parseObject(oldConfig);
    JSONObject vectorizer = config.getJSONObject(SpgAppConstant.VECTORIZER);
    if (vectorizer != null) {
      String apiKey = vectorizer.getString(SpgAppConstant.API_KEY);
      if (vectorizer.containsKey(SpgAppConstant.API_KEY)
          && StringUtils.equals(apiKey, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
        String oldApiKey =
            oldConfigJson
                .getJSONObject(SpgAppConstant.VECTORIZER)
                .getString(SpgAppConstant.API_KEY);
        vectorizer.put(SpgAppConstant.API_KEY, oldApiKey);
        config.put(SpgAppConstant.VECTORIZER, vectorizer);
      }
    }
    JSONArray oldLlmArray = oldConfigJson.getJSONArray(SpgAppConstant.LLM_SELECT);
    Map<String, JSONObject> oldLlmMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(oldLlmArray)) {
      for (int i = 0; i < oldLlmArray.size(); i++) {
        JSONObject oldLlm = oldLlmArray.getJSONObject(i);
        if (oldLlm == null) {
          continue;
        }
        oldLlmMap.put(oldLlm.getString(SpgAppConstant.LLM_ID), oldLlm);
      }
    }

    JSONArray llmArray = config.getJSONArray(SpgAppConstant.LLM_SELECT);
    if (CollectionUtils.isNotEmpty(llmArray)) {
      for (int i = 0; i < llmArray.size(); i++) {
        JSONObject llm = llmArray.getJSONObject(i);
        if (llm == null) {
          continue;
        }
        JSONObject oldLlm = oldLlmMap.get(llm.getString(SpgAppConstant.LLM_ID));
        if (null == oldLlm) {
          JSONObject oldLlmJson = oldConfigJson.getJSONObject(SpgAppConstant.LLM);
          if (oldLlmJson == null) {
            if (oldConfigJson.containsKey(SpgAppConstant.CHAT_LLM)) {
              oldLlmJson = oldConfigJson.getJSONObject(SpgAppConstant.CHAT_LLM);
            }
            if (oldLlmJson == null && oldConfigJson.containsKey(SpgAppConstant.OPENIE_LLM)) {
              oldLlmJson = oldConfigJson.getJSONObject(SpgAppConstant.OPENIE_LLM);
            }
          }
          backwardCompatibleLLM(oldLlmJson);
          if (oldLlmJson != null
              && StringUtils.equals(
                  oldLlmJson.getString(SpgAppConstant.TYPE), llm.getString(SpgAppConstant.TYPE))
              && StringUtils.equals(
                  oldLlmJson.getString(SpgAppConstant.MODEL),
                  llm.getString(SpgAppConstant.MODEL))) {
            oldLlm = oldLlmJson;
          }
        }
        if (oldLlm == null) {
          continue;
        }
        if (llm.containsKey(SpgAppConstant.API_KEY)
            && StringUtils.equals(
                llm.getString(SpgAppConstant.API_KEY), SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
          String llmApiKey = oldLlm.getString(SpgAppConstant.API_KEY);
          llm.put(SpgAppConstant.API_KEY, llmApiKey);
        }
        if (llm.containsKey(SpgAppConstant.KEY)
            && StringUtils.equals(
                llm.getString(SpgAppConstant.KEY), SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
          String llmApiKey = oldLlm.getString(SpgAppConstant.KEY);
          llm.put(SpgAppConstant.KEY, llmApiKey);
        }
      }
      config.put(SpgAppConstant.LLM_SELECT, llmArray);
    }

    JSONObject llm = config.getJSONObject(SpgAppConstant.LLM);
    if (llm != null) {
      JSONObject oldLlm = oldConfigJson.getJSONObject(SpgAppConstant.LLM);
      if (oldLlm == null) {
        oldLlm = oldLlmMap.get(llm.getString(SpgAppConstant.LLM_ID));
      }
      if (oldLlm != null) {
        if (llm.containsKey(SpgAppConstant.API_KEY)
            && StringUtils.equals(
                llm.getString(SpgAppConstant.API_KEY), SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
          String llmApiKey = oldLlm.getString(SpgAppConstant.API_KEY);
          llm.put(SpgAppConstant.API_KEY, llmApiKey);
        }
        if (llm.containsKey(SpgAppConstant.KEY)
            && StringUtils.equals(
                llm.getString(SpgAppConstant.KEY), SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
          String llmApiKey = oldLlm.getString(SpgAppConstant.KEY);
          llm.put(SpgAppConstant.KEY, llmApiKey);
        }
      }
    }

    JSONObject graphStore = config.getJSONObject(SpgAppConstant.GRAPH_STORE);
    if (graphStore != null) {
      String password = graphStore.getString(SpgAppConstant.PASSWORD);
      if (StringUtils.equals(password, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
        String oldPassword =
            oldConfigJson
                .getJSONObject(SpgAppConstant.GRAPH_STORE)
                .getString(SpgAppConstant.PASSWORD);
        graphStore.put(SpgAppConstant.PASSWORD, oldPassword);
        config.put(SpgAppConstant.GRAPH_STORE, graphStore);
      }
    }
  }

  @Override
  public void generateLLMIdCompletionLLM(JSONObject config) {
    if (config == null) {
      return;
    }
    JSONArray llmArray = config.getJSONArray(SpgAppConstant.LLM_SELECT);
    if (llmArray != null) {
      for (int i = 0; i < llmArray.size(); i++) {
        JSONObject llm = llmArray.getJSONObject(i);
        if (llm == null) {
          continue;
        }
        if (llm.getBooleanValue(SpgAppConstant.DEFAULT)) {
          config.put(SpgAppConstant.LLM, clearRedundantField(llm, SpgAppConstant.LLM));
        } else {
          llm.remove(SpgAppConstant.LLM);
        }
        if (StringUtils.isBlank(llm.getString(SpgAppConstant.LLM_ID))) {
          String llmId = UUID.randomUUID().toString();
          llm.put(SpgAppConstant.LLM_ID, llmId);
        }
      }
      config.put(SpgAppConstant.LLM_SELECT, llmArray);
    }
  }

  @Override
  public void backwardCompatible(JSONObject config) {
    if (config == null) {
      return;
    }
    // vectorizer 0.5 -> 0.6
    JSONObject vectorizerJson = config.getJSONObject(SpgAppConstant.VECTORIZER);
    if (vectorizerJson != null) {
      if (vectorizerJson.containsKey(SpgAppConstant.VECTORIZER)) {
        vectorizerJson.remove(SpgAppConstant.VECTORIZER);
      }
      if (!vectorizerJson.containsKey(SpgAppConstant.TYPE)) {
        vectorizerJson.put(SpgAppConstant.TYPE, SpgAppConstant.OPENAI);
      }
      String model = vectorizerJson.getString(SpgAppConstant.MODEL);
      if (StringUtils.equals(model, "bge-m3")) {
        vectorizerJson.put(SpgAppConstant.MODEL, "BAAI/bge-m3");
      }
      config.put(SpgAppConstant.VECTORIZER, vectorizerJson);
    }

    // llm 0.5 -> 0.6
    JSONObject llmJson = config.getJSONObject(SpgAppConstant.LLM);
    if (llmJson != null) {
      backwardCompatibleLLM(llmJson);
      config.put(SpgAppConstant.LLM, llmJson);
    } else {
      if (config.containsKey(SpgAppConstant.CHAT_LLM)) {
        llmJson =
            JSON.parseObject(JSON.toJSONString(config.getJSONObject(SpgAppConstant.CHAT_LLM)));
      }
      if (llmJson == null && config.containsKey(SpgAppConstant.OPENIE_LLM)) {
        llmJson =
            JSON.parseObject(JSON.toJSONString(config.getJSONObject(SpgAppConstant.OPENIE_LLM)));
      }
      if (llmJson != null) {
        backwardCompatibleLLM(llmJson);
        config.put(SpgAppConstant.LLM, llmJson);
      }
    }

    // llm_select 0.5 -> 0.6
    JSONArray llmSelectJson = config.getJSONArray(SpgAppConstant.LLM_SELECT);
    if (CollectionUtils.isNotEmpty(llmSelectJson)) {
      for (int i = 0; i < llmSelectJson.size(); i++) {
        JSONObject llm = llmSelectJson.getJSONObject(i);
        backwardCompatibleLLM(llm);
      }
      config.put(SpgAppConstant.LLM_SELECT, llmSelectJson);
    } else if (CollectionUtils.isEmpty(llmSelectJson) && llmJson != null) {
      llmSelectJson = new JSONArray();
      JSONObject llm = JSON.parseObject(llmJson.toJSONString());
      String llmId = UUID.randomUUID().toString();
      backwardCompatibleLLM(llm);
      llm.put(SpgAppConstant.LLM_ID, llmId);
      llm.put(SpgAppConstant.DEFAULT, true);
      llm.put(SpgAppConstant.DESC, "");
      llmSelectJson.add(llm);
      config.put(SpgAppConstant.LLM_SELECT, llmSelectJson);
    }
  }

  @Override
  public JSONObject clearRedundantField(JSONObject jsonObject, String configType) {
    if (jsonObject == null) {
      return new JSONObject();
    }
    List<String> llmKeyList;
    if (StringUtils.equals(configType, SpgAppConstant.LLM)) {
      llmKeyList =
          Arrays.asList(
              SpgAppConstant.TYPE,
              SpgAppConstant.BASE_URL,
              SpgAppConstant.API_KEY,
              SpgAppConstant.MODEL,
              SpgAppConstant.TEMPERATURE,
              SpgAppConstant.STREAM);
    } else if (StringUtils.equals(configType, SpgAppConstant.VECTORIZER)) {
      llmKeyList =
          Arrays.asList(
              SpgAppConstant.TYPE,
              SpgAppConstant.MODEL,
              SpgAppConstant.BASE_URL,
              SpgAppConstant.API_KEY,
              SpgAppConstant.VECTOR_DIMENSIONS);
    } else {
      return new JSONObject();
    }
    JSONObject llmHandle = JSON.parseObject(jsonObject.toString());
    if (llmHandle == null) {
      return new JSONObject();
    }
    Iterator<String> keys = jsonObject.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next();
      if (!llmKeyList.contains(key)) {
        llmHandle.remove(key);
      }
    }
    return llmHandle;
  }

  private static void backwardCompatibleLLM(JSONObject llmJson) {
    if (llmJson == null) {
      return;
    }
    String clientType = llmJson.getString(SpgAppConstant.CLIENT_TYPE);
    ModelType modelType = ModelType.getByCode(clientType);
    if (modelType == null) {
      modelType = ModelType.getByCode(llmJson.getString(SpgAppConstant.TYPE));
    }
    if (modelType == null) {
      return;
    }
    switch (modelType) {
      case MAAS:
        if (llmJson.containsKey(SpgAppConstant.CLIENT_TYPE)) {
          llmJson.remove(SpgAppConstant.CLIENT_TYPE);
        }
        if (!llmJson.containsKey(SpgAppConstant.TYPE)) {
          llmJson.put(SpgAppConstant.TYPE, clientType);
        }
        if (!llmJson.containsKey(SpgAppConstant.TEMPERATURE)) {
          llmJson.put(SpgAppConstant.TEMPERATURE, 0.7);
        }
        if (!llmJson.containsKey(SpgAppConstant.STREAM)) {
          llmJson.put(SpgAppConstant.STREAM, "False");
        }
        break;
      case OLLAMA:
      case VLLM:
        if (llmJson.containsKey(SpgAppConstant.CLIENT_TYPE)) {
          llmJson.remove(SpgAppConstant.CLIENT_TYPE);
        }
        if (!llmJson.containsKey(SpgAppConstant.TYPE)) {
          llmJson.put(SpgAppConstant.TYPE, clientType);
        }
        break;
      default:
        LOGGER.info("not support model type: {}", clientType);
        break;
    }
  }

  private Config toModel(ConfigRequest request) {
    Config config = new Config();
    config.setProjectId("0");
    config.setUserNo("admin");
    config.setId(request.getId());
    config.setConfigName(request.getConfigName());
    config.setConfigId(request.getConfigId());
    config.setVersion(request.getVersion());
    config.setConfig(request.getConfig());
    return config;
  }
}
