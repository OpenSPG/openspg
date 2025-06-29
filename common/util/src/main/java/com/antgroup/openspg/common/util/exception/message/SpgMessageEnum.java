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
  KAG_MISS_AUTH("KAG_0002", "Access is denied due to lack of authentication information"),
  KAG_NO_PERMISSION("KAG_0003", "Access is denied, no permission to access"),

  LOGIN_USER_NOT_EXIST("LOGIN_0001", "User does not exist"),
  LOGIN_USER_NOT_LOGIN("LOGIN_0002", "User not logged in"),
  LOGIN_SUPER_PASSWORD_NOT_CHANGE(
      "LOGIN_0003", "The default password of the system needs to be changed"),

  KB_MEMBER_NOT_EXIST("KB_0001", "User is not a member of the knowledge base"),
  KB_NOT_EXIST("KB_0002", "Knowledge base not exist"),
  KB_NAMESPACE_NOT_EXIST("KB_0003", "Namespace corresponds to a non-existent knowledge base"),
  KB_USED_ALREADY_CANNOT_DELETE("KB_0004", "Cannot delete used Knowledge base. Reference App:【%s】"),
  KB_NOT_OWNER("KB_0005", "User is not a owner of the knowledge base"),
  KB_NAME_EXIST("KB_0006", "Knowledge base name already exists"),

  SCHEMA_CHANGE_NODE_TYPE("SCHEMA_0001", "Schema advanced node type cannot be changed"),
  SCHEMA_CHANGE_PROPERTY_TYPE("SCHEMA_0002", "Schema property type cannot be changed"),
  SCHEMA_CHANGE_HYPERNYM_PREDICATE("SCHEMA_0003", "Schema hypernymPredicate cannot be changed"),
  SCHEMA_CHANGE_BASIC_TYPE(
      "SCHEMA_0004",
      "Schema advanced type[%s]'s property[%s] are built-in attributes, and currently only Text type is allowed"),

  MODEL_PROVIDER_NAME_EXIST("MODEL_0001", "Provider name already exists"),
  MODEL_NAME_EXIST("MODEL_0002", "Model name already exists"),
  MODEL_PARAM_TYPE_NOT_SUPPORT("MODEL_0003", "Param type not support"),
  MODEL_PUBLIC_ONLY_ONE_ALLOWED("MODEL_0004", "Public model only allowed one"),
  MODEL_USED_ALREADY_CANNOT_DELETE("MODEL_0005", "Cannot delete used model%s"),
  MODEL_NOT_PERMISSION("MODEL_0006", "No permission of the model"),

  APP_NAME_EXIST("APP_0001", "Application name already exists"),
  APP_NOT_FOUND("APP_0002", "Application not found"),
  APP_USED_ALREADY_CANNOT_DELETE("APP_0003", "Cannot delete used application"),
  APP_NOT_PERMISSION("APP_0004", "No permission of the application"),
  APP_NOT_OWNER("APP_0005", "User is not a owner of the application"),
  APP_NOT_SET_TEMPLATE("APP_0006", "The application has no session template"),
  APP_NOT_SET_KB("APP_0007", "The application has no knowledge base"),
  APP_USED_ALREADY_CANNOT_UPDATE_ALIAS(
      "APP_0008", "The alias cannot be modified while the app is in use"),
  APP_ALIAS_EXIST("APP_0009", "Application alias already exists"),

  ACCESS_TOKEN_INVALID("ACCESS_TOKEN_0001", "Access token is invalid"),
  ACCESS_TOKEN_FORBID("ACCESS_TOKEN_0002", "Access token is forbidden"),
  CHAT_ACCESS_TOKEN_INVALID("CHAT_0001", "Chat access token is invalid"),
  CHAT_ACCESS_TOKEN_FORBID("CHAT_0002", "Chat access token is forbidden"),
  SESSION_NOT_PERMISSION("SESSION_0001", "No permission of the session"),

  REACTION_TYPE_ERROR("FEED_BACK_0001", "No changes were found in the reaction types"),
  CANNOT_EDITED_ERROR("FEED_BACK_0002", "Feedback cannot be edited"),
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

  public String getMessage() {
    return message;
  }
}
