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
package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.app.App;
import com.antgroup.openspg.server.infra.dao.dataobject.AppDO;
import com.google.common.collect.Lists;
import java.util.List;

public class AppConvertor {

  public static AppDO toDO(App app) {
    if (app == null) {
      return null;
    }
    AppDO appDO = new AppDO();
    appDO.setId(app.getId());
    appDO.setModifier(app.getUserNo());
    appDO.setCreator(app.getUserNo());
    appDO.setName(app.getName());
    appDO.setLogo(app.getLogo());
    appDO.setDescription(app.getDescription());
    if (app.getConfig() != null) {
      String configStr = JSON.toJSONString(app.getConfig());
      appDO.setConfig(configStr);
    }
    appDO.setAlias(app.getAlias());
    return appDO;
  }

  public static App toModel(AppDO appDO) {
    if (appDO == null) {
      return null;
    }
    App app = new App();
    app.setId(appDO.getId());
    app.setUserNo(appDO.getCreator());
    app.setName(appDO.getName());
    app.setLogo(appDO.getLogo());
    app.setDescription(appDO.getDescription());
    if (StringUtils.isNotBlank(appDO.getConfig())) {
      JSONObject parseArray = JSON.parseObject(appDO.getConfig());
      app.setConfig(parseArray);
    }
    app.setAlias(appDO.getAlias());
    return app;
  }

  public static List<AppDO> toDoList(List<App> modelDetails) {
    if (modelDetails == null) {
      return null;
    }
    List<AppDO> dos = Lists.newArrayList();
    for (App provider : modelDetails) {
      dos.add(toDO(provider));
    }
    return dos;
  }

  public static List<App> toModelList(List<AppDO> modelDetailDOs) {
    if (modelDetailDOs == null) {
      return null;
    }
    List<App> details = Lists.newArrayList();
    for (AppDO providerDO : modelDetailDOs) {
      details.add(toModel(providerDO));
    }
    return details;
  }
}
