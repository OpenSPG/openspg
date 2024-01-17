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

package com.antgroup.openspg.cloudext.interfaces.graphstore.cmd;

import static com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.LPGRecordStructEnum.GRAPH;
import static com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.LPGRecordStructEnum.TABLE;

import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.LPGRecordStructEnum;
import com.antgroup.openspg.server.common.model.base.BaseQuery;
import lombok.Getter;

@Getter
public abstract class BaseLPGRecordQuery extends BaseQuery {

  @Getter
  public enum LpgRecordQueryType {
    // 基于脚本的查询方式底层存储返回 TableLpgRecordStruct
    SCRIPT(TABLE),

    // 查询点详情，或者查询一跳子图方式，底层存储返回 GraphLpgRecordStruct
    VERTEX(GRAPH),
    SCAN(GRAPH),
    ONE_HOP_SUBGRAPH(GRAPH),
    ;

    private final LPGRecordStructEnum struct;

    LpgRecordQueryType(LPGRecordStructEnum struct) {
      this.struct = struct;
    }
  }

  private final LpgRecordQueryType queryType;

  public BaseLPGRecordQuery(LpgRecordQueryType queryType) {
    this.queryType = queryType;
  }

  public abstract String toScript(LPGTypeNameConvertor lpgTypeNameConvertor);
}
