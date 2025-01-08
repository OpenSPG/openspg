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

package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/** This model corresponds to the database table: kg_user_resource_role Database Table Remarks: */
@Getter
@Setter
public class PermissionDO {
  /** primary key */
  private Long id;

  /** create time */
  private Date gmtCreate;

  /** update time */
  private Date gmtModified;

  /** userNo */
  private String userNo;

  /** resource id */
  private Long resourceId;

  /** role id */
  private Long roleId;

  /** resource tag */
  private String resourceTag;

  /** Status. -1: Rejected; 99: Under Approval; 1: Effective; 9: Deleted */
  private String status;

  /** expire date */
  private Date expireDate;
}
