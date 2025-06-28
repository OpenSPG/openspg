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

import com.antgroup.openspg.server.common.model.ref.RefInfo;
import com.antgroup.openspg.server.common.model.ref.RefTypeEnum;
import com.antgroup.openspg.server.common.model.ref.RefedTypeEnum;
import java.util.List;

public interface RefManager {

  /**
   * create a new reference
   *
   * @param refInfo
   * @return
   */
  Long create(RefInfo refInfo);

  /** Get a list of RefInfo objects by refId and refType. */
  List<RefInfo> getRefInfoByRef(String refId, RefTypeEnum refType);

  /** Get a list of RefInfo objects by refedId and refedType. */
  List<RefInfo> getRefInfoByRefed(String refedId, RefedTypeEnum refedType);

  /**
   * Get a list of RefInfo objects by right matching (suffix) the refedId.
   *
   * @param refedId The string to use for right matching
   * @return A list of matching RefInfo objects
   */
  List<RefInfo> getRefInfoByRightMatchRefedId(String refedId);

  /** select by unique key */
  RefInfo getByUniqueKey(
      String refId, RefTypeEnum refType, String refedId, RefedTypeEnum refedType);

  /** update by primary key */
  int updateByPrimaryKeySelective(RefInfo refInfo);

  /** update by unique key */
  int updateByUniqueKey(RefInfo refInfo);

  /** delete by primary key */
  int deleteById(Long id);

  /** delete by multiple primary keys */
  int deleteByIds(List<Long> ids);

  /** delete by unique key */
  int deleteByUniqueKey(RefInfo refInfo);

  /**
   * Record the usage information of an API key.
   *
   * @param appId The ID of the application using the API key
   * @param apiKey The API key being used
   * @param uri The URI of the API endpoint being accessed
   */
  void recordApiKeyUsageInfo(String appId, String apiKey, String uri);
}
