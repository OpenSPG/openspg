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

package com.antgroup.openspg.server.biz.common;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.PermissionRequest;
import com.antgroup.openspg.server.common.model.permission.Permission;
import java.util.List;

/** permission manager */
public interface PermissionManager {

  /**
   * create permission of some resource
   *
   * @param request
   * @return
   */
  Integer create(PermissionRequest request);

  /**
   * update permission of some resource
   *
   * @param request
   * @return
   */
  Integer update(PermissionRequest request);

  /**
   * query page permission or query by condition
   *
   * @param userNo
   * @param roleType
   * @param resourceId
   * @param resourceTag
   * @param page
   * @param size
   * @return
   */
  Paged<Permission> query(
      String userNo,
      String roleType,
      Long resourceId,
      String resourceTag,
      Integer page,
      Integer size);

  /**
   * remove permission
   *
   * @param request
   * @return
   */
  Integer removePermission(PermissionRequest request);

  /**
   * get user has permission permission
   *
   * @param resourceIds
   * @param userNo
   * @param roleType
   * @param resourceTag
   * @return
   */
  List<Permission> getPermissionByUserRolesAndId(
      List<Long> resourceIds, String userNo, String roleType, String resourceTag);

  /**
   * get by userNo and resourceTag
   *
   * @param userNo
   * @param resourceTag
   * @return
   */
  List<Permission> getPermissionByUserNoAndResourceTag(String userNo, String resourceTag);

  boolean isSuper(String userNo);

  /**
   * is project role
   *
   * @param userNo
   * @param projectId
   * @return
   */
  boolean isProjectRole(String userNo, Long projectId);

  /**
   * get owner user name by project id
   *
   * @param projectId
   * @return
   */
  List<String> getOwnerUserNameByProjectId(Long projectId);

  /**
   * get by id
   *
   * @param id
   * @return
   */
  Permission selectByPrimaryKey(Long id);
}
