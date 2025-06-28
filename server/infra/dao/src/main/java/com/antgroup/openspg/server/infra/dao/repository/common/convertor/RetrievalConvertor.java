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

package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.antgroup.openspg.common.util.DozerBeanMapperUtil;
import com.antgroup.openspg.server.common.model.retrieval.Retrieval;
import com.antgroup.openspg.server.infra.dao.dataobject.RetrievalDO;
import com.google.common.collect.Lists;
import java.util.List;

public class RetrievalConvertor {

  public static RetrievalDO toDO(Retrieval retrieval) {
    if (null == retrieval) {
      return null;
    }
    RetrievalDO retrievalDO = DozerBeanMapperUtil.map(retrieval, RetrievalDO.class);
    return retrievalDO;
  }

  public static Retrieval toModel(RetrievalDO retrievalDO) {
    if (null == retrievalDO) {
      return null;
    }
    Retrieval retrieval = DozerBeanMapperUtil.map(retrievalDO, Retrieval.class);
    return retrieval;
  }

  public static List<RetrievalDO> toDoList(List<Retrieval> retrievals) {
    if (retrievals == null) {
      return null;
    }
    List<RetrievalDO> dos = Lists.newArrayList();
    for (Retrieval retrieval : retrievals) {
      dos.add(toDO(retrieval));
    }
    return dos;
  }

  public static List<Retrieval> toModelList(List<RetrievalDO> retrievalDOS) {
    if (retrievalDOS == null) {
      return null;
    }
    List<Retrieval> retrievals = Lists.newArrayList();
    for (RetrievalDO retrievalDO : retrievalDOS) {
      retrievals.add(toModel(retrievalDO));
    }
    return retrievals;
  }
}
