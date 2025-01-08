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

import com.antgroup.openspg.server.common.model.permission.Permission;
import com.antgroup.openspg.server.infra.dao.dataobject.PermissionDO;

public class PermissionConvertor {

  public static PermissionDO toDO(Permission permission) {
    if (null == permission) {
      return null;
    }
    PermissionDO permissionDO = new PermissionDO();
    permissionDO.setId(permission.getId());
    permissionDO.setUserNo(permission.getUserNo());
    permissionDO.setResourceTag(permission.getResourceTag());
    permissionDO.setResourceId(permission.getResourceId());
    permissionDO.setRoleId(permission.getRoleId());
    return permissionDO;
  }

  public static Permission toModel(PermissionDO permissionDO) {
    if (null == permissionDO) {
      return null;
    }
    return new Permission(
        permissionDO.getId(),
        permissionDO.getUserNo(),
        permissionDO.getResourceId(),
        permissionDO.getResourceTag(),
        permissionDO.getRoleId());
  }
}
