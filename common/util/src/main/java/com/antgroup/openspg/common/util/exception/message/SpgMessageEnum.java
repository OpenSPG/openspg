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
package com.antgroup.openspg.common.util.exception.message;

public enum SpgMessageEnum implements Message {
  KAG_ERROR("KAG_0001", "Some error, please check /home/admin/logs/antspg/common-error.log"),
  LOGIN_USER_NOT_EXIST("LOGIN_0001", "User does not exist"),
  LOGIN_USER_NOT_LOGIN("LOGIN_0002", "User not logged in"),
  LOGIN_SUPER_PASSWORD_NOT_CHANGE(
      "LOGIN_0003", "The default password of the system needs to be changed"),

  PROJECT_MEMBER_NOT_EXIST("PRO_0001", "User does not have project member permission"),
  PROJECT_NOT_EXIST("PRO_0002", "Project does not exist"),
  PROJECT_NAMESPACE_NOT_EXIST("PRO_0003", "Namespace does not exist"),

  SCHEMA_CHANGE_NODE_TYPE("SCHEMA_0001", "Schema advanced node type cannot be changed"),
  SCHEMA_CHANGE_PROPERTY_TYPE("SCHEMA_0001", "Schema property type cannot be changed"),
  SCHEMA_CHANGE_HYPERNYM_PREDICATE("SCHEMA_0001", "Schema hypernymPredicate cannot be changed"),
  ;

  SpgMessageEnum(String code, String message) {
    this.code = code;
    this.message = message;
  }

  String code;

  String message;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
