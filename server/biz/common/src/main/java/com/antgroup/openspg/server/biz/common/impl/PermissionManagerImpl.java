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

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.PermissionEnum;
import com.antgroup.openspg.common.util.enums.ResourceTagEnum;
import com.antgroup.openspg.server.api.facade.dto.common.request.PermissionRequest;
import com.antgroup.openspg.server.biz.common.AccountManager;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.permission.Permission;
import com.antgroup.openspg.server.common.model.project.AccountRoleInfo;
import com.antgroup.openspg.server.common.service.permission.PermissionRepository;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionManagerImpl implements PermissionManager {

  @Autowired private PermissionRepository permissionRepository;

  @Autowired private AccountManager accountManager;

  @Override
  public Integer create(PermissionRequest request) {
    List<Permission> permissionList = toModels(request);
    if (CollectionUtils.isEmpty(permissionList)) {
      return 0;
    }
    Set<String> exitsPermissions =
        permissionRepository
            .selectByResourceIdsAndResourceTag(request.getResourceIds(), request.getResourceTag())
            .stream()
            .map(permission -> permission.getUserNo())
            .collect(Collectors.toSet());
    int count = 0;
    for (Permission permission : permissionList) {
      if (exitsPermissions.contains(permission.getUserNo())) {
        count += permissionRepository.update(permission);
      } else {
        count += permissionRepository.save(permission);
      }
    }
    return count;
  }

  @Override
  public Integer update(PermissionRequest request) {
    int count = 0;
    List<Permission> permissionList = toModels(request);
    if (CollectionUtils.isEmpty(permissionList)) {
      return 0;
    }
    if (null != request.getId()) {
      count = permissionRepository.update(permissionList.get(0));
    } else {
      for (Permission permission : permissionList) {
        count += permissionRepository.update(permission);
      }
    }
    return count;
  }

  @Override
  public Integer removePermission(PermissionRequest request) {
    int count = 0;
    if (null != request.getId()) {
      count = permissionRepository.delete(new Permission(request.getId()));
    } else {
      List<Permission> permissionList = toModels(request);
      for (Permission permission : permissionList) {
        count += permissionRepository.delete(permission);
      }
    }
    return count;
  }

  @Override
  public List<Permission> getPermissionByUserRolesAndId(
      List<Long> resourceIds, String userNo, String roleType, String resourceTag) {
    return permissionRepository.getPermissionByUserRolesAndId(
        resourceIds, userNo, roleType, resourceTag);
  }

  @Override
  public List<Permission> getPermissionByUserNoAndResourceTag(String userNo, String resourceTag) {
    return permissionRepository.getPermissionByUserNoAndResourceTag(userNo, resourceTag);
  }

  @Override
  public boolean isSuper(String userNo) {
    List<Permission> superPermissions =
        getPermissionByUserRolesAndId(
            Lists.newArrayList(0L),
            userNo,
            PermissionEnum.SUPER.name(),
            ResourceTagEnum.PLATFORM.name());
    return CollectionUtils.isNotEmpty(superPermissions);
  }

  @Override
  public boolean hasPermission(
      String userNo, Long resourceId, String resourceType, String roleType) {
    if (isSuper(userNo)) {
      return true;
    }
    if (PermissionEnum.MEMBER.name().equals(roleType)) {
      return isRoleMembers(userNo, resourceId, resourceType);
    }
    List<Permission> permissionList =
        getPermissionByUserRolesAndId(
            Lists.newArrayList(resourceId), userNo, roleType, resourceType);
    return CollectionUtils.isNotEmpty(permissionList);
  }

  @Override
  public boolean isRoleMembers(String userNo, Long resourceId, String resourceType) {
    List<Permission> permissionList =
        getPermissionByUserRolesAndId(Lists.newArrayList(resourceId), null, null, resourceType);
    if (CollectionUtils.isEmpty(permissionList)) {
      return false;
    }
    return permissionList.stream()
        .anyMatch(permission -> StringUtils.equals(permission.getUserNo(), userNo));
  }

  @Override
  public List<String> getOwnerUserNameByResourceId(Long resourceId, String resourceType) {
    String defaultOwner = "there is no resource administrator for the project";
    List<Permission> permissionList =
        getPermissionByUserRolesAndId(
            Lists.newArrayList(resourceId), null, PermissionEnum.OWNER.name(), resourceType);
    List<String> userNoList =
        permissionList.stream()
            .filter(permission -> permission.getRoleId().equals(PermissionEnum.OWNER.getId()))
            .map(Permission::getUserNo)
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(userNoList)) {
      return Lists.newArrayList(defaultOwner);
    }
    List<String> userName =
        accountManager.getSimpleAccountByUserNoList(userNoList).stream()
            .map(
                account ->
                    StringUtils.isNotBlank(account.getNickName())
                        ? account.getNickName()
                        : account.getWorkNo())
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(userName)) {
      return Lists.newArrayList(defaultOwner);
    }
    return userName;
  }

  @Override
  public Permission selectByPrimaryKey(Long id) {
    return permissionRepository.selectByPrimaryKey(id);
  }

  @Override
  public List<Permission> selectLikeByUserNoAndRoleId(
      String userNo, String roleType, Long resourceId, String resourceTag, Long page, Long size) {
    Long roleId = null;
    if (StringUtils.isNotBlank(roleType)) {
      roleId = PermissionEnum.valueOf(roleType).getId();
    }
    List<Permission> permissionList =
        permissionRepository.selectLikeByUserNoAndRoleId(
            userNo, roleId, resourceId, resourceTag, page, size);
    if (CollectionUtils.isNotEmpty(permissionList)) {
      Set<String> userNos =
          permissionList.stream().map(Permission::getUserNo).collect(Collectors.toSet());
      Map<String, Account> userNameMap =
          accountManager.getSimpleAccountByUserNoList(userNos).stream()
              .collect(Collectors.toMap(Account::getWorkNo, account -> account));
      permissionList.forEach(
          permission -> {
            Account account = userNameMap.get(permission.getUserNo());
            if (null != account) {
              permission.setUserName(
                  StringUtils.isNotBlank(account.getNickName())
                      ? account.getNickName()
                      : account.getWorkNo());
            }
            permission.setAccountRoleInfo(
                new AccountRoleInfo(PermissionEnum.getRoleTypeById(permission.getRoleId()).name()));
          });
    }
    return permissionList;
  }

  @Override
  public long selectLikeCountByUserNoAndRoleId(
      String userNo, String roleType, Long resourceId, String resourceTag) {
    Long roleId = null;
    if (StringUtils.isNotBlank(roleType)) {
      roleId = PermissionEnum.valueOf(roleType).getId();
    }
    return permissionRepository.selectLikeCountByUserNoAndRoleId(
        userNo, roleId, resourceId, resourceTag);
  }

  @Override
  public int deleteByResourceId(Long resourceId, String resourceTag) {
    return permissionRepository.deleteByResourceId(resourceId, resourceTag);
  }

  private List<Permission> toModels(PermissionRequest request) {
    Long roleId = null;
    if (StringUtils.isNotBlank(request.getRoleType())) {
      roleId = PermissionEnum.valueOf(request.getRoleType()).getId();
    }
    List<Permission> permissionList = new ArrayList<>();
    for (String userNo : request.getUserNos()) {
      Permission permission = new Permission();
      permission.setUserNo(userNo);
      permission.setResourceTag(request.getResourceTag());
      permission.setRoleId(roleId);
      permission.setRoleType(request.getRoleType());
      for (Long resourceId : request.getResourceIds()) {
        permission.setResourceId(resourceId);
        permissionList.add(permission);
      }
    }
    return permissionList;
  }
}
