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

package com.antgroup.openspg.server.infra.dao.repository.common;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.PermissionEnum;
import com.antgroup.openspg.server.common.model.permission.Permission;
import com.antgroup.openspg.server.common.service.permission.PermissionRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.PermissionDO;
import com.antgroup.openspg.server.infra.dao.mapper.PermissionMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.PermissionConvertor;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepositoryImpl implements PermissionRepository {

  @Autowired private PermissionMapper permissionMapper;

  @Override
  public Integer save(Permission permission) {
    PermissionDO permissionDO = PermissionConvertor.toDO(permission);
    if (null == permissionDO) {
      return 0;
    }
    permissionDO.setGmtCreate(new Date());
    permissionDO.setGmtModified(new Date());
    permissionDO.setStatus("1");
    return permissionMapper.insert(permissionDO);
  }

  @Override
  public Integer update(Permission permission) {
    PermissionDO permissionDO = PermissionConvertor.toDO(permission);
    if (null == permissionDO) {
      return 0;
    }
    permissionDO.setGmtModified(new Date());
    if (null != permissionDO.getId()) {
      return permissionMapper.updateByPrimaryKeySelective(permissionDO);
    } else {
      return permissionMapper.updateResourceRole(permissionDO);
    }
  }

  @Override
  public List<Permission> queryByUserNoAndRoleId(
      String userNo, Long roleId, Long resourceId, String resourceTag, Long page, Long pageSize) {
    if (StringUtils.isBlank(userNo)) {
      return Lists.newArrayList();
    }
    return selectLikeByUserNoAndRoleId(userNo, roleId, resourceId, resourceTag, page, pageSize);
  }

  @Override
  public Integer delete(Permission permission) {
    int count = 0;
    if (null != permission.getId()) {
      count = permissionMapper.deleteByPrimaryKey(permission.getId());
    } else if (StringUtils.isNotBlank(permission.getUserNo())
        && StringUtils.isNotBlank(permission.getResourceTag())
        && null != permission.getResourceId()) {
      count = permissionMapper.deletePermission(PermissionConvertor.toDO(permission));
    }
    return count;
  }

  @Override
  public List<Permission> selectByResourceIdsAndResourceTag(
      List<Long> resourceIds, String resourceTag) {
    List<PermissionDO> permissionList =
        permissionMapper.selectByResourceIdsAndResourceTag(resourceIds, resourceTag);
    if (CollectionUtils.isNotEmpty(permissionList)) {
      return permissionList.stream().map(PermissionConvertor::toModel).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  @Override
  public List<Permission> getPermissionByUserRolesAndId(
      List<Long> resourceIds, String userNo, String roleType, String resourceTag) {
    if (CollectionUtils.isEmpty(resourceIds) || StringUtils.isBlank(resourceTag)) {
      return Lists.newArrayList();
    }
    Long roleId = null;
    if (StringUtils.isNotBlank(roleType)) {
      roleId = PermissionEnum.valueOf(roleType).getId();
    }
    List<PermissionDO> permissionList =
        permissionMapper.getPermissionByUserRolesAndId(resourceIds, userNo, roleId, resourceTag);
    if (CollectionUtils.isNotEmpty(permissionList)) {
      return permissionList.stream().map(PermissionConvertor::toModel).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  @Override
  public List<Permission> getPermissionByUserNoAndResourceTag(String userNo, String resourceTag) {
    if (StringUtils.isBlank(userNo) || StringUtils.isBlank(resourceTag)) {
      return Lists.newArrayList();
    }
    List<PermissionDO> permissionList =
        permissionMapper.getPermissionByUserNoAndResourceTag(userNo, resourceTag);
    if (CollectionUtils.isNotEmpty(permissionList)) {
      return permissionList.stream().map(PermissionConvertor::toModel).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  @Override
  public Permission selectByPrimaryKey(Long id) {
    PermissionDO permissionDO = permissionMapper.selectByPrimaryKey(id);
    return PermissionConvertor.toModel(permissionDO);
  }

  @Override
  public List<Permission> selectLikeByUserNoAndRoleId(
      String userNo, Long roleId, Long resourceId, String resourceTag, Long page, Long size) {
    if (null == resourceId || StringUtils.isBlank(resourceTag)) {
      return Lists.newArrayList();
    }
    List<PermissionDO> permissionList;
    if (null == size) {
      size = 10L;
    }
    page = page > 0 ? page : 1;
    Long start = (page - 1) * size;
    if (StringUtils.isNotBlank(userNo)) {
      permissionList =
          permissionMapper.selectLikeByUserNoAndRoleId(
              userNo, roleId, resourceId, resourceTag, start, size);
    } else {
      PermissionDO permissionDO = new PermissionDO();
      permissionDO.setResourceTag(resourceTag);
      permissionDO.setResourceId(resourceId);
      permissionDO.setRoleId(roleId);
      permissionList = permissionMapper.selectByCondition(permissionDO, start, size);
    }
    return PermissionConvertor.toModelList(permissionList);
  }

  @Override
  public long selectLikeCountByUserNoAndRoleId(
      String userNo, Long roleId, Long resourceId, String resourceTag) {
    if (null == resourceId || StringUtils.isBlank(resourceTag)) {
      return 0;
    }
    Long count;
    if (StringUtils.isNotBlank(userNo)) {
      count =
          permissionMapper.selectLikeCountByUserNoAndRoleId(
              userNo, roleId, resourceId, resourceTag);
    } else {
      PermissionDO permissionDO = new PermissionDO();
      permissionDO.setResourceTag(resourceTag);
      permissionDO.setResourceId(resourceId);
      permissionDO.setRoleId(roleId);
      count = permissionMapper.selectCountByCondition(permissionDO);
    }
    return count;
  }

  @Override
  public int deleteByResourceId(Long resourceId, String resourceTag) {
    return permissionMapper.deleteByResourceId(resourceId, resourceTag);
  }
}
