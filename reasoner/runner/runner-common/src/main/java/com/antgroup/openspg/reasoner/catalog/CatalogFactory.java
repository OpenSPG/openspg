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

package com.antgroup.openspg.reasoner.catalog;

import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.catalog.impl.OpenSPGCatalog;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface;
import com.antgroup.openspg.reasoner.parser.KgDslParser;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.utils.SimpleObjSerde;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j(topic = "userlogger")
public class CatalogFactory {

  public static Catalog createCatalog(Map<String, Object> params, KgSchemaConnectionInfo connInfo) {
    return createCatalog(new KgDslParser(), params, connInfo);
  }

  public static Catalog createCatalog(
      ParserInterface parser, Map<String, Object> params, KgSchemaConnectionInfo connInfo) {
    String catalogStr = (String) params.get(ConfigKey.KG_REASONER_CATALOG);
    if (StringUtils.isNotEmpty(catalogStr)) {
      return (Catalog) SimpleObjSerde.de(catalogStr);
    }

    if (connInfo == null) {
      throw new RuntimeException("SchemaConnectionInfo is null");
    }
    Catalog catalog =
        new OpenSPGCatalog(
            Long.parseLong(String.valueOf(params.getOrDefault("projId", 0L))), connInfo, null);
    catalog.init();
    return catalog;
  }
}
