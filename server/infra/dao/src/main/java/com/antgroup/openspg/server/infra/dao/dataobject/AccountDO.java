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
import lombok.Data;

/** This model corresponds to the database table: kg_user Database Table Remarks: */
@Data
public class AccountDO {
  /** primary key */
  private Long id;

  /** create time */
  private Date gmtCreate;

  /** update time */
  private Date gmtModified;

  /** userNo */
  private String userNo;

  /** token */
  private String token;

  /** Modified Token Before Modification */
  private String lastToken;

  /** Random String */
  private String salt;

  /** token update time */
  private Date gmtLastTokenDisable;

  /** data userId */
  private String dwAccessId;

  /** data userKey */
  private String dwAccessKey;

  /** account real name */
  private String realName;

  /** account nick name */
  private String nickName;

  /** account email */
  private String email;

  /** account login account */
  private String domainAccount;

  /** account mobile */
  private String mobile;

  /** account wechat id */
  private String wxAccount;

  /** config */
  private String config;
}
