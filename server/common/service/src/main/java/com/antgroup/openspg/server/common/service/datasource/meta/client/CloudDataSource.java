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
package com.antgroup.openspg.server.common.service.datasource.meta.client;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.DozerBeanMapperUtil;
import com.antgroup.openspg.common.util.ECBUtil;
import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.server.common.model.CommonEnum.DataSourceCategory;
import com.antgroup.openspg.server.common.model.CommonEnum.DataSourceType;
import com.antgroup.openspg.server.common.model.CommonEnum.Status;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudDataSource extends BaseModel {

  private static final long serialVersionUID = -4123605193544774990L;

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String createUser;
  private String updateUser;
  private Status status;
  private String remark;
  private DataSourceType type;
  private String dbName;
  private String dbUrl;
  private String dbUser;
  private String dbPassword;
  private String encrypt;
  private String dbDriverName;
  private DataSourceCategory category;
  private JSONObject connectionInfo;

  public static CloudDataSource toCloud(DataSource dataSource) {
    if (dataSource == null) {
      return null;
    }
    CloudDataSource cloudDataSource = DozerBeanMapperUtil.map(dataSource, CloudDataSource.class);
    cloudDataSource.setDbPassword(
        ECBUtil.decrypt(dataSource.getEncrypt(), CommonConstant.ECB_PASSWORD_KEY));
    return cloudDataSource;
  }
}
