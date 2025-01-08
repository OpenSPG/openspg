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

package com.antgroup.openspg.server.common.model.account;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class Account extends BaseModel {

  private Long id;
  private String workNo;
  private String token;
  private String salt;
  private String realName;
  private String nickName;
  private String account;
  private String password;
  private String confirmPassword;
  private String email;
  private Date gmtCreate;
  private Date gmtModified;
  private String config;
  private String useCurrentLanguage;
  private List<String> roleNames;

  public Account() {}

  public Account(
      Long id,
      String workNo,
      String realName,
      String nickName,
      String account,
      String email,
      Date gmtCreate,
      Date gmtModified,
      String config,
      String useCurrentLanguage) {
    this.id = id;
    this.workNo = workNo;
    this.realName = realName;
    this.nickName = nickName;
    this.account = account;
    this.email = email;
    this.gmtCreate = gmtCreate;
    this.gmtModified = gmtModified;
    this.config = config;
    this.useCurrentLanguage = useCurrentLanguage;
  }

  public Account(
      Long id,
      String workNo,
      String realName,
      String nickName,
      String account,
      String email,
      String salt,
      String config,
      String useCurrentLanguage) {
    this.id = id;
    this.workNo = workNo;
    this.realName = realName;
    this.nickName = nickName;
    this.account = account;
    this.email = email;
    this.salt = salt;
    this.config = config;
    this.useCurrentLanguage = useCurrentLanguage;
  }
}
