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

package com.antgroup.openspg.server.common.model.permission;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.project.AccountRoleInfo;
import lombok.Data;

@Data
public class Permission extends BaseModel {

  private Long id;
  private String userNo;
  private Long resourceId;
  private String resourceTag;
  private Long roleId;
  private String roleType;
  private String userName;
  private AccountRoleInfo accountRoleInfo;

  public Permission() {}

  public Permission(Long id) {
    this.id = id;
  }

  public Permission(Long id, String userNo, Long resourceId, String resourceTag, Long roleId) {
    this.id = id;
    this.userNo = userNo;
    this.resourceId = resourceId;
    this.resourceTag = resourceTag;
    this.roleId = roleId;
  }
}
