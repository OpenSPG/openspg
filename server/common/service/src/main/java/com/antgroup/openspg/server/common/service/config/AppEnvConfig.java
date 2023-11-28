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

package com.antgroup.openspg.server.common.service.config;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/** Application environment configuration */
public class AppEnvConfig implements Serializable {

  private String schemaUri;

  private Boolean enableSearchEngine;
  private String builderOperatorPythonExec;
  private String builderOperatorPythonPaths;

  public String getSchemaUri() {
    return schemaUri;
  }

  public void setSchemaUri(String schemaUri) {
    this.schemaUri = schemaUri;
  }

  public boolean getEnableSearchEngine() {
    return BooleanUtils.isTrue(enableSearchEngine);
  }

  public void setEnableSearchEngine(Boolean enableSearchEngine) {
    this.enableSearchEngine = enableSearchEngine;
  }

  public String getBuilderOperatorPythonExec() {
    return builderOperatorPythonExec;
  }

  public void setBuilderOperatorPythonExec(String builderOperatorPythonExec) {
    this.builderOperatorPythonExec = builderOperatorPythonExec;
  }

  public String[] getBuilderOperatorPythonPaths() {
    if (StringUtils.isBlank(builderOperatorPythonPaths)) {
      return null;
    }
    return Arrays.stream(builderOperatorPythonPaths.split(";"))
        .filter(StringUtils::isNotBlank)
        .toArray(String[]::new);
  }

  public void setBuilderOperatorPythonPaths(String builderOperatorPythonPaths) {
    this.builderOperatorPythonPaths = builderOperatorPythonPaths;
  }
}
