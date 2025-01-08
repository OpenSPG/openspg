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
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.permission.Permission;
import com.antgroup.openspg.server.common.service.permission.PermissionRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.PermissionDO;
import com.antgroup.openspg.server.infra.dao.mapper.PermissionMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.PermissionConvertor;
import com.antgroup.openspgapp.common.util.enums.PermissionEnum;
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
  public List<Permission> query(
      Long resourceId, String resourceTag, Integer page, Integer pageSize) {
    return query(null, null, resourceId, resourceTag, page, pageSize);
  }

  @Override
  public List<Permission> queryByUserNoAndRoleId(
      String userNo,
      Long roleId,
      Long resourceId,
      String resourceTag,
      Integer page,
      Integer pageSize) {
    if (StringUtils.isBlank(userNo)) {
      return Lists.newArrayList();
    }
    return query(userNo, roleId, resourceId, resourceTag, page, pageSize);
  }

  private List<Permission> query(
      String userNo,
      Long roleId,
      Long resourceId,
      String resourceTag,
      Integer page,
      Integer pageSize) {
    if (null == resourceId || StringUtils.isBlank(resourceTag)) {
      return Lists.newArrayList();
    }
    if (null == pageSize) {
      pageSize = 10;
    }
    page = page > 0 ? page : 1;
    int start = (page - 1) * pageSize;
    List<PermissionDO> permissionList;
    if (StringUtils.isBlank(userNo)) {
      PermissionDO permissionDO = new PermissionDO();
      permissionDO.setResourceId(resourceId);
      permissionDO.setResourceTag(resourceTag);
      permissionList = permissionMapper.selectByCondition(permissionDO, start, pageSize);
    } else {
      permissionList =
          permissionMapper.selectLikeByUserNoAndRoleId(
              userNo, roleId, resourceId, resourceTag, start, pageSize);
    }
    if (CollectionUtils.isNotEmpty(permissionList)) {
      return permissionList.stream().map(PermissionConvertor::toModel).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  @Override
  public Paged<Permission> queryPage(
      String userNo, Long roleId, Long resourceId, String resourceTag, Integer page, Integer size) {
    Paged<Permission> result = new Paged<>();
    result.setPageIdx(page);
    result.setPageSize(size);
    List<PermissionDO> permissionList;
    PermissionDO permissionDO = new PermissionDO();
    permissionDO.setResourceTag(resourceTag);
    permissionDO.setResourceId(resourceId);
    permissionDO.setRoleId(roleId);
    if (null == size) {
      size = 10;
    }
    page = page > 0 ? page : 1;
    int start = (page - 1) * size;
    if (StringUtils.isNotBlank(userNo)) {
      permissionDO.setUserNo(userNo);
      result.setTotal(
          (long)
              permissionMapper.selectLikeCountByUserNoAndRoleId(
                  userNo, roleId, resourceId, resourceTag));
      permissionList =
          permissionMapper.selectLikeByUserNoAndRoleId(
              userNo, roleId, resourceId, resourceTag, start, size);
    } else {
      result.setTotal((long) permissionMapper.selectCountByCondition(permissionDO));
      permissionList = permissionMapper.selectByCondition(permissionDO, start, size);
    }

    if (CollectionUtils.isNotEmpty(permissionList)) {
      result.setResults(
          permissionList.stream().map(PermissionConvertor::toModel).collect(Collectors.toList()));
    }
    return result;
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
}
