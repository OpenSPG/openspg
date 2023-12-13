/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.io;

import com.antgroup.openspg.reasoner.common.table.Field;
import com.antgroup.openspg.reasoner.common.table.FieldType;
import com.antgroup.openspg.reasoner.io.model.OdpsTableInfo;
import com.antgroup.openspg.reasoner.io.odps.OdpsUtils;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OdpsReadWriteTest {

  private String odpsAccessId = null;
  private String odpsAccessKey = null;
  private String odpsEndPoint = null;

  @Before
  public void init() {
    this.odpsAccessId = "";
    this.odpsAccessKey = "";
    this.odpsEndPoint = "";
  }

  @Test
  public void testWriteTable() throws Exception {
    if (StringUtils.isEmpty(this.odpsAccessId)) {
      return;
    }

    // do test need init odps access info

    OdpsTableInfo tableInfo = new OdpsTableInfo();
    tableInfo.setProject("alifin_jtest_dev");
    tableInfo.setTable("kg_reasoner_odps_test_table_" + Math.abs(UUID.randomUUID().hashCode()));
    Map<String, String> partitionMap = new HashMap<>();
    partitionMap.put("dt", "20230303");
    tableInfo.setPartition(partitionMap);

    if (StringUtils.isEmpty(odpsAccessId)
        || StringUtils.isEmpty(odpsAccessKey)
        || StringUtils.isEmpty(odpsEndPoint)) {
      return;
    }

    tableInfo.setAccessID(this.odpsAccessId);
    tableInfo.setAccessKey(this.odpsAccessKey);
    tableInfo.setEndPoint(this.odpsEndPoint);

    tableInfo.setColumns(
        Lists.newArrayList(new Field("id", FieldType.STRING), new Field("count", FieldType.LONG)));

    String sessionId = IoFactory.createWriterSession(tableInfo);

    ITableWriter writer0 = IoFactory.getTableWriter(sessionId, 0, 3, tableInfo);
    writer0.write(new Object[] {"id1", 100L});
    writer0.write(new Object[] {"id2", 200L});

    ITableWriter writer1 = IoFactory.getTableWriter(sessionId, 1, 3, tableInfo);
    writer1.write(new Object[] {"id3", 300L});

    ITableWriter writer2 = IoFactory.getTableWriter(sessionId, 2, 3, tableInfo);
    writer2.write(new Object[] {"id4", 400L});

    IoFactory.closeWriter(sessionId, 0);
    IoFactory.closeWriter(sessionId, 1);
    IoFactory.closeWriter(sessionId, 2);

    IoFactory.commitWriterSession(sessionId);

    Map<String, Long> allResult = new HashMap<>();
    ITableReader reader0 = IoFactory.getTableReader(0, 3, tableInfo, null);
    while (reader0.hasNext()) {
      Object[] row = reader0.next();
      allResult.put((String) row[0], (Long) row[1]);
    }
    reader0.close();

    ITableReader reader1 = IoFactory.getTableReader(1, 3, tableInfo, null);
    while (reader1.hasNext()) {
      Object[] row = reader1.next();
      allResult.put((String) row[0], (Long) row[1]);
    }
    reader1.close();

    ITableReader reader2 =
        IoFactory.getTableReader(2, 3, 0, 2, Lists.newArrayList(tableInfo), null);
    while (reader2.hasNext()) {
      Object[] row = reader2.next();
      allResult.put((String) row[0], (Long) row[1]);
    }
    reader2.close();

    ITableReader reader3 =
        IoFactory.getTableReader(2, 3, 1, 2, Lists.newArrayList(tableInfo), null);
    while (reader3.hasNext()) {
      Object[] row = reader3.next();
      allResult.put((String) row[0], (Long) row[1]);
    }
    reader3.close();

    // drop table
    try {
      OdpsUtils.dropOdpsTable(tableInfo);
    } catch (Exception e) {
      System.out.println("drop table error, " + tableInfo + ",errorMessage=" + e.getMessage());
    }

    Assert.assertEquals((long) allResult.get("id1"), 100L);
    Assert.assertEquals((long) allResult.get("id2"), 200L);
    Assert.assertEquals((long) allResult.get("id3"), 300L);
    Assert.assertEquals((long) allResult.get("id4"), 400L);
  }
}
