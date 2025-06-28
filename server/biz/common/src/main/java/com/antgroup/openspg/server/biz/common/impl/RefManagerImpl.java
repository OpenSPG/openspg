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

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.biz.common.RefManager;
import com.antgroup.openspg.server.common.model.ref.RefInfo;
import com.antgroup.openspg.server.common.model.ref.RefTypeEnum;
import com.antgroup.openspg.server.common.model.ref.RefedTypeEnum;
import com.antgroup.openspg.server.common.service.ref.RefRepository;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefManagerImpl implements RefManager {

  @Autowired private RefRepository refRepository;

  @Override
  public Long create(RefInfo refInfo) {
    // check type
    RefTypeEnum.valueOf(refInfo.getRefType());
    RefedTypeEnum.valueOf(refInfo.getRefedType());
    return refRepository.insert(refInfo);
  }

  @Override
  public List<RefInfo> getRefInfoByRef(String refId, RefTypeEnum refType) {
    return refRepository.getRefInfoByRef(refId, refType);
  }

  @Override
  public List<RefInfo> getRefInfoByRefed(String refedId, RefedTypeEnum refedType) {
    return refRepository.getRefInfoByRefed(refedId, refedType);
  }

  @Override
  public List<RefInfo> getRefInfoByRightMatchRefedId(String refedId) {
    return refRepository.getRefInfoByRightMatchRefedId(refedId);
  }

  @Override
  public RefInfo getByUniqueKey(
      String refId, RefTypeEnum refType, String refedId, RefedTypeEnum refedType) {
    return refRepository.selectByUniqueKey(refId, refType.name(), refedId, refedType.name());
  }

  @Override
  public int updateByPrimaryKeySelective(RefInfo refInfo) {
    return refRepository.updateByPrimaryKeySelective(refInfo);
  }

  @Override
  public int updateByUniqueKey(RefInfo refInfo) {
    return refRepository.updateByUniqueKey(refInfo);
  }

  @Override
  public int deleteById(Long id) {
    return refRepository.deleteById(id);
  }

  @Override
  public int deleteByIds(List<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0;
    }
    return refRepository.deleteByIds(ids);
  }

  @Override
  public int deleteByUniqueKey(RefInfo refInfo) {
    return refRepository.deleteByUniqueKey(refInfo);
  }

  @Override
  public void recordApiKeyUsageInfo(String appId, String apiKey, String uri) {
    try {
      RefInfo refInfo = new RefInfo();
      refInfo.setRefId(appId);
      refInfo.setRefType(RefTypeEnum.APP.name());
      refInfo.setRefedId(apiKey);
      refInfo.setRefedType(RefedTypeEnum.API_KEY.name());
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("lastRequestUrl", uri);
      jsonObject.put("lastRequestTime", DateTimeUtils.getDate2LongStr(new Date()));
      refInfo.setConfig(jsonObject.toJSONString());
      refRepository.updateByUniqueKey(refInfo);
    } catch (Exception e) {
      log.error("recordApiKeyUsageInfo error, appId:{}, apiKey:{}, uri:{}", appId, apiKey, uri, e);
    }
  }
}
