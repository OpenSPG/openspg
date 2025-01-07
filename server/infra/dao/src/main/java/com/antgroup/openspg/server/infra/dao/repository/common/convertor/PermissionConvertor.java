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
