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
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.PageModeEnum;
import com.antgroup.openspg.server.biz.common.ConfigManager;
import com.antgroup.openspg.server.biz.common.ModelDetailManager;
import com.antgroup.openspg.server.biz.common.ModelProviderManager;
import com.antgroup.openspg.server.biz.common.UserModelManager;
import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetail;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailQuery;
import com.antgroup.openspg.server.common.model.provider.ModelProvider;
import com.antgroup.openspg.server.common.model.usermodel.UserModel;
import com.antgroup.openspg.server.common.model.usermodel.UserModelDTO;
import com.antgroup.openspg.server.common.model.usermodel.UserModelQuery;
import com.antgroup.openspg.server.common.service.usermodel.UserModelRepository;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserModelManagerImpl implements UserModelManager {

  private static final List<String> sensitiveFields =
      Arrays.asList("secret_id", "secret_key", "access_key", "api_key", "key");

  @Autowired private UserModelRepository userModelRepository;

  @Autowired private ModelDetailManager modelDetailManager;

  @Autowired private ModelProviderManager modelProviderManager;

  @Autowired private ConfigManager configManager;

  @Override
  public Long insert(UserModelDTO record, Map<String, Object> modelTypeMap) {
    ModelProvider provider = modelProviderManager.getByProvider(record.getProvider());
    JSONObject config = record.getConfig();
    removeNullValue(config);
    if (StringUtils.equals(provider.getPageMode(), PageModeEnum.SINGLE.name())) {
      String modelType = config.getString(SpgAppConstant.MODEL_TYPE);
      config.put(
          SpgAppConstant.TYPE,
          modelTypeMap.get(provider.getProvider() + StringUtils.UNDERLINE_SEPARATOR + modelType));
    }

    String uuid = UUID.randomUUID().toString().replace("-", "");

    UserModel addUserModel =
        new UserModel(
            uuid,
            record.getVisibility(),
            record.getProvider(),
            record.getName(),
            record.getUserNo(),
            record.getUserNo());

    if (StringUtils.equals(provider.getPageMode(), PageModeEnum.ALL.name())) {
      ModelDetailQuery modelDetailQuery = new ModelDetailQuery();
      modelDetailQuery.setProvider(provider.getProvider());
      List<ModelDetail> modelDetailList = modelDetailManager.query(modelDetailQuery);

      JSONArray jsonArray = new JSONArray();
      for (ModelDetail modelDetail : modelDetailList) {
        JSONObject params =
            modelDetail.getParams() != null ? modelDetail.getParams() : new JSONObject();
        if (!params.containsKey(SpgAppConstant.MODEL)) {
          params.put(SpgAppConstant.MODEL, modelDetail.getName());
        }
        if (!params.containsKey(SpgAppConstant.MODEL_TYPE)) {
          params.put(SpgAppConstant.MODEL_TYPE, modelDetail.getType());
        }
        JSONObject completeConfig = new JSONObject();
        completeConfig.put(
            SpgAppConstant.TYPE,
            modelTypeMap.get(
                provider.getProvider() + StringUtils.UNDERLINE_SEPARATOR + modelDetail.getType()));
        completeConfig.putAll(config);
        completeConfig.putAll(params);
        generateModelId(uuid, completeConfig);
        jsonArray.add(completeConfig);
      }
      addUserModel.setConfig(
          JSON.toJSONString(jsonArray, SerializerFeature.DisableCircularReferenceDetect));
      return userModelRepository.insert(addUserModel);
    } else if (StringUtils.equals(provider.getPageMode(), PageModeEnum.SINGLE.name())) {
      List<UserModel> userModels = getByProviderAndName(provider.getProvider(), record.getName());
      UserModel userModel =
          userModels.stream()
              .filter(m -> StringUtils.equals(m.getVisibility(), addUserModel.getVisibility()))
              .findFirst()
              .orElse(null);
      if (userModel != null) {
        JSONArray modelList = userModel.getModelList();
        generateModelId(userModel.getInstanceId(), config);
        modelList.add(config);
        userModel.setConfig(
            JSON.toJSONString(modelList, SerializerFeature.DisableCircularReferenceDetect));
        userModelRepository.update(userModel);
        return userModel.getId();
      } else {
        JSONArray jsonArray = new JSONArray();
        generateModelId(uuid, config);
        jsonArray.add(config);
        addUserModel.setConfig(
            JSON.toJSONString(jsonArray, SerializerFeature.DisableCircularReferenceDetect));
        return userModelRepository.insert(addUserModel);
      }
    }
    return 0L;
  }

  private void removeNullValue(JSONObject config) {
    List<String> removeKey = new ArrayList<>();
    for (String key : config.keySet()) {
      Object value = config.get(key);
      if (value instanceof Integer) {
        if (value == null) {
          removeKey.add(key);
        }
      } else if (value instanceof String && StringUtils.isBlank((String) value)) {
        removeKey.add(key);
      }
    }
    removeKey.forEach(key -> config.remove(key));
  }

  @Override
  public int deleteById(Long id) {
    return userModelRepository.deleteById(id);
  }

  @Override
  public int deleteByIds(List<Long> ids) {
    return userModelRepository.deleteByIds(ids);
  }

  @Override
  public Long update(UserModel record) {
    return userModelRepository.update(record);
  }

  @Override
  public UserModel getById(Long id) {
    return userModelRepository.getById(id);
  }

  @Override
  public List<UserModel> query(UserModelQuery record) {
    return userModelRepository.query(record);
  }

  @Override
  public List<UserModel> getByProviderAndName(String provider, String name) {
    return userModelRepository.getByProviderAndName(provider, name);
  }

  public void generateModelId(String uuid, JSONObject config) {
    if (config == null) {
      return;
    }
    if (!config.containsKey(SpgAppConstant.MODEL_ID)
        || !StringUtils.equals(uuid, config.getString(SpgAppConstant.MODEL_ID))) {
      String model = config.getString(SpgAppConstant.MODEL);
      config.put(SpgAppConstant.MODEL_ID, uuid + "@" + model);
    }
  }

  @Override
  public Long updateApiKey(UserModelDTO request) {
    UserModel userModel = getById(request.getId());
    if (userModel == null) {
      return 0L;
    }
    userModel.setModifier(request.getUserNo());
    JSONObject configJson = request.getConfig();
    JSONArray modelList = userModel.getModelList();
    for (int i = 0; i < modelList.size(); i++) {
      JSONObject modelJson = modelList.getJSONObject(i);
      modelJson.putAll(configJson);
      userModel.setConfig(JSON.toJSONString(modelList));
    }
    return update(userModel);
  }

  @Override
  public List<Map<String, Object>> list(
      String modelType, String queryStr, String modelId, String userNo) {
    List<UserModel> userModelList = userModelRepository.selectUserPrivateOrPublic(userNo);
    Map<String, UserModel> modelMap =
        userModelList.stream()
            .collect(Collectors.toMap(UserModel::getInstanceId, userModel -> userModel));
    if (StringUtils.isNotBlank(modelId) && modelId.split("@").length == 2) {
      String[] split = modelId.split("@");
      UserModel addModel = getByInstanceId(split[0]);
      if (addModel != null) {
        JSONArray modelList = addModel.getModelList();
        for (int i = 0; i < modelList.size(); i++) {
          JSONObject jsonObject = modelList.getJSONObject(i);
          if (StringUtils.equals(modelId, jsonObject.getString(SpgAppConstant.MODEL_ID))) {
            UserModel userModel = modelMap.get(addModel.getInstanceId());
            if (userModel != null
                && StringUtils.equals(userModel.getInstanceId(), addModel.getInstanceId())) {
              break;
            }
            JSONArray modelModelList =
                userModel != null ? userModel.getModelList() : new JSONArray();
            modelModelList.add(jsonObject);
            addModel.setModelList(modelModelList);
            modelMap.put(addModel.getInstanceId(), addModel);
            break;
          }
        }
      }
    }
    List<UserModel> userModels = new ArrayList<>(modelMap.values());
    if (CollectionUtils.isEmpty(userModels)) {
      return Collections.emptyList();
    }
    List<String> providerList =
        userModels.stream().map(UserModel::getProvider).collect(Collectors.toList());
    List<ModelProvider> providers = modelProviderManager.selectByProviders(providerList);
    if (CollectionUtils.isEmpty(providers)) {
      return Collections.emptyList();
    }
    Map<String, ModelProvider> providerMap =
        providers.stream()
            .collect(
                Collectors.toMap(
                    ModelProvider::getProvider,
                    provider -> ModelProvider.modelProvider(provider),
                    (v1, v2) -> v1));

    Map<String, Map<String, Object>> map = new HashMap<>();
    for (UserModel userModel : userModels) {
      String onlyName =
          userModel.getProvider() + StringUtils.UNDERLINE_SEPARATOR + userModel.getName();
      ModelProvider provider = providerMap.get(userModel.getProvider());
      JSONArray modelList = userModel.getModelList();

      JSONArray resultModelList = new JSONArray();
      if (map.containsKey(onlyName)) {
        Map<String, Object> providerInfo = map.get(onlyName);
        if (providerInfo.containsKey(SpgAppConstant.MODEL)) {
          resultModelList = (JSONArray) providerInfo.get(SpgAppConstant.MODEL);
        }
      }

      if (StringUtils.isNotBlank(modelType)) {
        modelList =
            modelList.stream()
                .filter(
                    model ->
                        StringUtils.equals(
                            modelType, ((JSONObject) model).getString(SpgAppConstant.MODEL_TYPE)))
                .collect(Collectors.toCollection(JSONArray::new));
      }
      if (StringUtils.isNotBlank(queryStr)) {
        modelList =
            modelList.stream()
                .filter(
                    model ->
                        StringUtils.contains(userModel.getName(), queryStr)
                            || StringUtils.contains(
                                ((JSONObject) model).getString(SpgAppConstant.MODEL), queryStr))
                .collect(Collectors.toCollection(JSONArray::new));
      }
      for (int i = 0; i < modelList.size(); i++) {
        JSONObject modelJson = modelList.getJSONObject(i);
        desensitizedFieldSensitive(modelJson);
        modelJson.put(SpgAppConstant.VISIBILITY, userModel.getVisibility());
        resultModelList.add(modelJson);
      }
      if (CollectionUtils.isEmpty(resultModelList)) {
        continue;
      }
      Map<String, Object> providerModel = new HashMap<>();
      providerModel.put("model", resultModelList);
      providerModel.putAll(JSON.parseObject(JSON.toJSONString(provider)));
      providerModel.put("id", userModel.getId());
      providerModel.put("visibility", userModel.getVisibility());
      providerModel.put("userNo", userModel.getUserNo());
      providerModel.put("name", userModel.getName());
      if (StringUtils.equals(provider.getPageMode(), PageModeEnum.ALL.name())) {
        JSONObject jsonObject = resultModelList.getJSONObject(0);
        providerModel.put("config", jsonObject);
      }
      map.put(onlyName, providerModel);
    }
    List<Map<String, Object>> list = map.values().stream().collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(list)) {
      list.sort(
          (mapA, mapB) -> {
            Long idA = (Long) mapA.get("id");
            Long idB = (Long) mapB.get("id");
            return idB.compareTo(idA); // 降序排序
          });
    }
    return list;
  }

  @Override
  public JSONObject getByModelId(String modelId) {
    return userModelRepository.getByModelId(modelId);
  }

  @Override
  public Boolean deleteModel(Long id, String modelId) {
    UserModel userModel = getById(id);
    JSONArray modelList = userModel.getModelList();
    for (int i = 0; i < modelList.size(); i++) {
      JSONObject modelJson = modelList.getJSONObject(i);
      if (StringUtils.equals(modelId, modelJson.getString(SpgAppConstant.MODEL_ID))) {
        modelList.remove(i);
        if (modelList.size() <= 0) {
          return deleteById(id) > 0;
        } else {
          userModel.setConfig(JSON.toJSONString(modelList));
          return update(userModel) > 0;
        }
      }
    }
    return false;
  }

  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  @Override
  public Boolean updateModelVisibility(String modelId, String visibility, JSONObject customize) {
    String[] split = modelId.split("@");
    if (split.length < 2) {
      return false;
    }
    String instanceId = split[0];
    String configModel = split[1];
    UserModel userModel = userModelRepository.getByInstanceId(instanceId);
    if (userModel == null) {
      return false;
    }
    if (StringUtils.equals(userModel.getVisibility(), visibility)) {
      JSONArray modelList = userModel.getModelList();
      for (int i = 0; i < modelList.size(); i++) {
        JSONObject modelConfig = modelList.getJSONObject(i);
        modelConfig.remove(SpgAppConstant.CUSTOMIZE);
        if (customize != null) {
          modelConfig.put(SpgAppConstant.CUSTOMIZE, customize);
        }
      }
      userModel.setConfig(
          JSON.toJSONString(modelList, SerializerFeature.DisableCircularReferenceDetect));
      return update(userModel) > 0;
    }
    JSONObject modelInfo = null;
    JSONArray modelList = userModel.getModelList();
    for (int i = 0; i < modelList.size(); i++) {
      JSONObject jsonObject = modelList.getJSONObject(i);
      if (jsonObject.containsKey(SpgAppConstant.MODEL)
          && StringUtils.equals(configModel, jsonObject.getString(SpgAppConstant.MODEL))) {
        modelInfo = jsonObject;
      }
    }
    if (modelInfo == null) {
      return false;
    }
    modelList.remove(modelInfo);
    modelInfo.remove(SpgAppConstant.CUSTOMIZE);
    if (customize != null) {
      modelInfo.put(SpgAppConstant.CUSTOMIZE, customize);
    }
    List<UserModel> oldUserModelList =
        getByProviderAndName(userModel.getProvider(), userModel.getName());
    Optional<UserModel> first =
        oldUserModelList.stream()
            .filter(model -> StringUtils.equals(visibility, model.getVisibility()))
            .findFirst();
    if (first.isPresent()) {
      UserModel model = first.get();
      JSONArray addModelList = model.getModelList();
      if (CollectionUtils.isNotEmpty(addModelList)
          && addModelList.toJavaList(Map.class).stream()
              .noneMatch(
                  map -> StringUtils.equals((String) map.get(SpgAppConstant.MODEL), configModel))) {
        modelInfo.put(
            SpgAppConstant.MODEL_ID,
            model.getInstanceId() + "@" + modelInfo.getString(SpgAppConstant.MODEL));
        addModelList.add(modelInfo);
        UserModel updateModel = new UserModel();
        updateModel.setId(model.getId());
        updateModel.setConfig(JSON.toJSONString(addModelList));
        userModelRepository.update(updateModel);
      }
    } else {
      String uuid = UUID.randomUUID().toString().replace("-", "");
      UserModel addUserModel =
          new UserModel(
              uuid,
              visibility,
              userModel.getProvider(),
              userModel.getName(),
              userModel.getUserNo(),
              userModel.getUserNo());
      modelInfo.put(
          SpgAppConstant.MODEL_ID, uuid + "@" + modelInfo.getString(SpgAppConstant.MODEL));
      addUserModel.setConfig(JSON.toJSONString(Arrays.asList(modelInfo)));
      userModelRepository.insert(addUserModel);
    }
    if (CollectionUtils.isNotEmpty(modelList)) {
      UserModel updateModel = new UserModel();
      updateModel.setId(userModel.getId());
      updateModel.setConfig(JSON.toJSONString(modelList));
      updateModel.setModifier(userModel.getUserNo());
      update(updateModel);
    } else {
      userModelRepository.deleteById(userModel.getId());
    }
    return true;
  }

  @Override
  public UserModel getByInstanceId(String instanceId) {
    return userModelRepository.getByInstanceId(instanceId);
  }

  @Override
  public Long updateBaseInfoByIds(
      List<Long> ids, String name, String visibility, String userNo, String config) {
    return userModelRepository.updateBaseInfoByIds(ids, name, visibility, userNo, config);
  }

  @Override
  public List<UserModel> selectUserPrivateOrPublic(String userNo) {
    return userModelRepository.selectUserPrivateOrPublic(userNo);
  }

  @Override
  public JSONObject desensitizedFieldSensitive(JSONObject config) {
    if (config == null) {
      return null;
    }
    for (String key : config.keySet()) {
      if (sensitiveFields.contains(key)) {
        config.put(key, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY);
      }
    }
    return config;
  }

  @Override
  public JSONObject sensitiveFieldRecovery(JSONObject config, JSONObject oldConfig) {
    if (config == null || oldConfig == null) {
      return null;
    }
    List<String> recoveryFields = Lists.newArrayList();
    for (Map.Entry<String, Object> entry : config.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue().toString();
      if (StringUtils.equals(value, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
        recoveryFields.add(key);
      }
    }
    if (CollectionUtils.isEmpty(recoveryFields)) {
      return config;
    }
    for (Map.Entry<String, Object> entry : oldConfig.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue().toString();
      if (recoveryFields.contains(key)) {
        config.put(key, value);
      }
    }
    return config;
  }

  @Override
  public Map<String, Object> getModelTypeMap() {
    Config providerBaseInfo = configManager.query("PROVIDER_BASE_INFO", "1");
    if (providerBaseInfo == null) {
      return null;
    }
    JSONObject providerInfo = (JSONObject) providerBaseInfo.getConfig();
    return providerInfo.getJSONObject(SpgAppConstant.MODEL_TYPE);
  }

  @Override
  public JSONObject getModelByProviderAndModel(
      String provider, String name, String visibility, String model) {
    List<UserModel> userModelList = getByProviderAndName(provider, name);
    if (CollectionUtils.isEmpty(userModelList)) {
      return null;
    }
    Optional<UserModel> first =
        userModelList.stream()
            .filter(userModel -> StringUtils.equals(userModel.getVisibility(), visibility))
            .findFirst();
    if (!first.isPresent()) {
      return null;
    }
    UserModel userModel = first.get();
    JSONArray modelList = userModel.getModelList();
    for (int i = 0; i < modelList.size(); i++) {
      JSONObject jsonObject = modelList.getJSONObject(i);
      if (StringUtils.equals(model, jsonObject.getString(SpgAppConstant.MODEL))) {
        return jsonObject;
      }
    }
    return null;
  }
}
