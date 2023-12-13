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

package com.antgroup.openspg.reasoner.catalog.impl;

import com.antgroup.kg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class OpenKgCatalogTest {

  public static final KgSchemaConnectionInfo connInfo =
      new KgSchemaConnectionInfo("http://127.0.0.1:8887", "a8bB6398B6Da9170");

  @Test
  public void testGet() {
    long projectId = 1000025L;

    OpenKgCatalog catalog = new OpenKgCatalog(projectId, connInfo, null);
    catalog.init();

    Assert.assertNotNull(catalog.getKnowledgeGraph());
  }
}
