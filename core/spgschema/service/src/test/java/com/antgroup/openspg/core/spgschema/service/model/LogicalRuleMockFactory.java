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

package com.antgroup.openspg.core.spgschema.service.model;

import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule;

public class LogicalRuleMockFactory {

  public static String mockPropertyLogicRule() {
    return "Define (s:OpenSource.Person)-[p:installGamblingAppNum]->(o:Int) {\n"
        + "  GraphStructure {\n"
        + "  \t# path里是否支持概念，概念如何执行？（展开，替换）\n"
        + "  \t(s)-[p1:hasDevice]->(o1:OpenSource.Device)-[p2:installApp]->(o2:`OpenSource.TaxonomyOfApp`/`涉赌`)\n"
        + "  }\n"
        + "  Rule {\n"
        + "  \to = group(s).count(o2.id)\n"
        + "  }\n"
        + "}";
  }

  public static LogicalRule mockRelationLogicRule() {
    String dsl =
        "Define (s:OpenSource.Person)-[p:installGamblingAppNum]->(o:Int) {\n"
            + "  GraphStructure {\n"
            + "  \t# path里是否支持概念，概念如何执行？（展开，替换）\n"
            + "  \t(s)-[p1:hasDevice]->(o1:OpenSource.Device)-[p2:installApp]->(o2:`OpenSource.TaxonomyOfApp`/`涉赌`)\n"
            + "  }\n"
            + "  Rule {\n"
            + "  \to = group(s).count(o2.id)\n"
            + "  }\n"
            + "}";

    return new LogicalRule(null, null, dsl);
  }
}
