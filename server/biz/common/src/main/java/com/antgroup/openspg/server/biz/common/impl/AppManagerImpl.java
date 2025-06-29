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
import com.antgroup.openspg.common.util.enums.PermissionEnum;
import com.antgroup.openspg.common.util.enums.ResourceTagEnum;
import com.antgroup.openspg.server.api.facade.dto.common.request.AppRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.PermissionRequest;
import com.antgroup.openspg.server.biz.common.AppManager;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.RefManager;
import com.antgroup.openspg.server.common.model.app.App;
import com.antgroup.openspg.server.common.model.ref.RefInfo;
import com.antgroup.openspg.server.common.model.ref.RefTypeEnum;
import com.antgroup.openspg.server.common.model.ref.RefedTypeEnum;
import com.antgroup.openspg.server.common.service.app.AppRepository;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AppManagerImpl implements AppManager {

  @Autowired private AppRepository appRepository;
  @Autowired private PermissionManager permissionManager;
  @Autowired private RefManager refManager;

  @Override
  public Long create(AppRequest request) {
    App app =
        new App(
            null,
            request.getName(),
            request.getLogo(),
            request.getDescription(),
            request.getConfig(),
            request.getUserNo(),
            request.getAlias());
    appRepository.save(app);
    if (app != null && app.getId() != null) {
      PermissionRequest permissionRequest = new PermissionRequest();
      permissionRequest.setResourceIds(Lists.newArrayList(app.getId()));
      permissionRequest.setResourceTag(ResourceTagEnum.APP.name());
      permissionRequest.setUserNos(Lists.newArrayList(request.getUserNo()));
      permissionRequest.setRoleType(PermissionEnum.OWNER.name());
      permissionManager.create(permissionRequest);
    }
    return app.getId();
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public Integer update(AppRequest request) {
    App app =
        new App(
            request.getId(),
            request.getName(),
            request.getLogo(),
            request.getDescription(),
            request.getConfig(),
            request.getUserNo(),
            request.getAlias());
    Integer update = appRepository.update(app);

    if (request.getConfig() == null) {
      return update;
    }
    Map<String, Object> idMap = getKbIdsAndLLMIdByConfig(request.getConfig());
    List<Long> kbIds = (List<Long>) idMap.get("kbIds");
    String llmId = (String) idMap.get("llmId");
    Map<Long, Boolean> kbId2StatusMap = (Map<Long, Boolean>) idMap.get("kbId2StatusMap");
    List<RefInfo> refInfoByRef =
        refManager.getRefInfoByRef(String.valueOf(request.getId()), RefTypeEnum.APP);
    if (CollectionUtils.isNotEmpty(refInfoByRef)) {
      List<Long> ids =
          refInfoByRef.stream()
              .filter(
                  ref ->
                      Arrays.asList(RefTypeEnum.KNOWLEDGE_BASE.name(), RefedTypeEnum.LLM.name())
                          .contains(ref.getRefedType()))
              .map(RefInfo::getId)
              .collect(Collectors.toList());
      // Delete the association between the knowledge base or llm and the application
      refManager.deleteByIds(ids);
    }

    if (CollectionUtils.isNotEmpty(kbIds)) {
      for (Long kbId : kbIds) {
        RefInfo refInfo =
            new RefInfo(
                "APP_KNOWLEDGE_BASE",
                String.valueOf(request.getId()),
                RefTypeEnum.APP.name(),
                String.valueOf(kbId),
                RefTypeEnum.KNOWLEDGE_BASE.name(),
                Boolean.TRUE.equals(kbId2StatusMap.get(kbId)) ? 1 : 0);
        refManager.create(refInfo);
        log.info(
            "create app knowledge base ref info, refId: {}, refInfo: {}",
            kbId,
            JSON.toJSONString(refInfo));
      }
    }
    if (StringUtils.isNotBlank(llmId)) {
      RefInfo refInfo =
          new RefInfo(
              "APP_LLM",
              String.valueOf(request.getId()),
              RefTypeEnum.APP.name(),
              llmId,
              RefedTypeEnum.LLM.name(),
              1);
      Long id = refManager.create(refInfo);
      log.info("create app llm ref info, refId: {}, refInfo: {}", id, JSON.toJSONString(refInfo));
    }
    return update;
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public Integer deleteById(Long id) {
    Integer delete = appRepository.deleteById(id);
    permissionManager.deleteByResourceId(id, ResourceTagEnum.APP.name());
    List<RefInfo> refInfoByRef = refManager.getRefInfoByRef(String.valueOf(id), RefTypeEnum.APP);
    if (CollectionUtils.isNotEmpty(refInfoByRef)) {
      List<Long> ids = refInfoByRef.stream().map(RefInfo::getId).collect(Collectors.toList());
      // Delete the association between the knowledge base or llm and the application
      refManager.deleteByIds(ids);
    }
    return delete;
  }

  @Override
  public App queryById(Long id) {
    App app = appRepository.queryById(id);
    if (app == null) {
      return null;
    }
    return app;
  }

  @Override
  public List<App> queryPage(AppRequest request, Long page, Long size) {
    return appRepository.queryPage(request, page.intValue(), size.intValue());
  }

  @Override
  public Long selectCountByCondition(AppRequest request) {
    return appRepository.selectCountByCondition(request);
  }

  @Override
  public Map<String, Object> getKbIdsAndLLMIdByConfig(JSONObject config) {
    Map<String, Object> map = new HashMap<>(2);
    Map<Long, Boolean> kbId2StatusMap = new HashMap<>();
    map.put("kbIds", Lists.newArrayList());
    map.put("llmId", null);
    map.put("kbId2StatusMap", kbId2StatusMap);
    if (config == null) {
      return map;
    }
    JSONArray kbArray = config.getJSONArray("kb");
    if (CollectionUtils.isNotEmpty(kbArray)) {
      List<Long> kbIds = Lists.newArrayList();
      for (int i = 0; i < kbArray.size(); i++) {
        JSONObject jsonObject = kbArray.getJSONObject(i);
        Long id = jsonObject.getLong("id");
        kbId2StatusMap.put(id, jsonObject.getBooleanValue("enable"));
        kbIds.add(id);
      }
      map.put("kbIds", kbIds);
    }
    JSONObject llm = config.getJSONObject(SpgAppConstant.LLM);
    if (llm != null) {
      String modelId = llm.getString(SpgAppConstant.MODEL_ID);
      if (StringUtils.isNotBlank(modelId)) {
        map.put("llmId", modelId);
      }
    }
    return map;
  }

  @Override
  public App queryByName(String name) {
    return appRepository.queryByName(name);
  }

  @Override
  public List<App> queryByAlias(String alias) {
    AppRequest appRequest = new AppRequest();
    appRequest.setAlias(alias);
    return appRepository.queryByCondition(appRequest);
  }
}
