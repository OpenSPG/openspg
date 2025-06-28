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

package com.antgroup.openspg.server.common.service.ref;

import com.antgroup.openspg.server.common.model.ref.RefInfo;
import com.antgroup.openspg.server.common.model.ref.RefTypeEnum;
import com.antgroup.openspg.server.common.model.ref.RefedTypeEnum;
import java.util.List;

public interface RefRepository {

  /** insert reference info */
  Long insert(RefInfo refInfo);

  /** delete by primary key */
  int deleteById(Long id);

  /** delete by multiple primary keys */
  int deleteByIds(List<Long> ids);

  /** update refInfo by primary key */
  int update(RefInfo refInfo);

  /** get refInfo by primary key */
  RefInfo getById(Long id);

  /** select by unique key */
  RefInfo selectByUniqueKey(String refId, String refType, String refedId, String refedType);

  /** update by primary key selective */
  int updateByPrimaryKeySelective(RefInfo refInfo);

  /** update by unique key */
  int updateByUniqueKey(RefInfo refInfo);

  /** get refInfo */
  List<RefInfo> getRefInfoByRef(String refId, RefTypeEnum refType);

  /** get refInfo */
  List<RefInfo> getRefInfoByRefed(String refedId, RefedTypeEnum refedType);

  /** Get a list of RefInfo objects by right matching (suffix) the refedId. */
  List<RefInfo> getRefInfoByRightMatchRefedId(String refedId);

  /** Delete a RefInfo object by unique key. */
  int deleteByUniqueKey(RefInfo refInfo);
}
