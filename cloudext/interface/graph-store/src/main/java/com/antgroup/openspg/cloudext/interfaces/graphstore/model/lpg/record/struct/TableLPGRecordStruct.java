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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
public class TableLPGRecordStruct extends BaseLPGRecordStruct {

  private final List<String> columnNames;

  private final List<List<Object>> cells;

  public TableLPGRecordStruct(List<String> columnNames, List<List<Object>> cells) {
    super(LPGRecordStructEnum.TABLE);
    this.columnNames = columnNames;
    this.cells = cells;
  }

  public boolean isEmpty() {
    return CollectionUtils.isEmpty(columnNames);
  }

  public GraphLPGRecordStruct toGraphLpgRecordStruct() {
    List<VertexRecord> vertexRecords = new ArrayList<>();
    List<EdgeRecord> edgeRecords = new ArrayList<>();

    for (List<Object> objects : cells) {
      for (Object object : objects) {
        if (object instanceof VertexRecord) {
          vertexRecords.add((VertexRecord) object);
        } else if (object instanceof EdgeRecord) {
          edgeRecords.add((EdgeRecord) object);
        }
      }
    }
    return new GraphLPGRecordStruct(vertexRecords, edgeRecords);
  }
}
