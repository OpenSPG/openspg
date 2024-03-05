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

package com.antgroup.openspg.reasoner.rdg.common.groupProcess;

import java.util.List;

public class ParsedAggEle {
  private final String sourceAlias;
  private final String sourcePropertyName;
  private final List<String> exprStrList;

  public ParsedAggEle(String sourceAlias, String sourcePropertyName, List<String> exprStrList) {
    this.sourceAlias = sourceAlias;
    this.sourcePropertyName = sourcePropertyName;
    this.exprStrList = exprStrList;
  }

  public String getSourceAlias() {
    return sourceAlias;
  }

  public String getSourcePropertyName() {
    return sourcePropertyName;
  }

  public List<String> getExprStrList() {
    return exprStrList;
  }
}
