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

package com.antgroup.openspg.server.common.model.project;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import lombok.Data;

/** @version UserRoleInfo.java, v 0.1 2024年11月26日 上午11:41 */
@Data
public class AccountRoleInfo extends BaseModel {

  /** Specific Role Name */
  String roleName;

  /** Permission Detail Information */
  String permissionDetail;

  public AccountRoleInfo() {}

  public AccountRoleInfo(String roleName) {
    this.roleName = roleName;
  }
}
