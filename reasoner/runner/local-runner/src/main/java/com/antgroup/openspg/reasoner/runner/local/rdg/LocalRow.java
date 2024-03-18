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

package com.antgroup.openspg.reasoner.runner.local.rdg;

import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.lube.physical.rdg.Row;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import scala.collection.JavaConversions;

@Slf4j
public class LocalRow extends Row<LocalRDG> {
  private final List<String> columns;
  private final List<Object[]> rowList;
  private final LocalReasonerResult graphRst;

  /** row implement */
  public LocalRow(
      scala.collection.immutable.List<Var> orderedFields,
      LocalRDG rdg,
      scala.collection.immutable.List<String> as,
      List<Object[]> rows,
      LocalReasonerResult graphResult) {
    super(orderedFields, rdg);
    this.columns = new ArrayList<>();
    this.columns.addAll(Lists.newArrayList(JavaConversions.asJavaCollection(as)));
    this.rowList = rows;
    this.graphRst = graphResult;
  }

  @Override
  public void show(int rows) {
    log.info("###############GeaflowRowShow###############");
    for (int i = 0; i < rowList.size(); ++i) {
      Object[] row = rowList.get(i);
      log.info("(" + i + ") " + Arrays.toString(row));
    }
    log.info("###############GeaflowRowShow###############");
  }

  /** get select result */
  public LocalReasonerResult getResult() {
    return new LocalReasonerResult(
        columns,
        rowList,
        graphRst.getVertexList(),
        graphRst.getEdgeList(),
        graphRst.isGraphResult());
  }
}
