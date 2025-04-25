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

package com.antgroup.openspg.server.common.service.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@ToString
@Getter
@Component
public class DefaultValue {

  @Value("${cloudext.graphstore.url:}")
  private String graphStoreUrl;

  @Value("${cloudext.searchengine.url:}")
  private String searchEngineUrl;

  @Value("${cloudext.objectstorage.url:}")
  private String objectStorageUrl;

  @Value("${cloudext.computingengine.url:}")
  private String computingEngineUrl;

  @Value("${schema.uri:}")
  private String schemaUrlHost;

  @Value("${builder.model.execute.num:20}")
  private Integer modelExecuteNum;

  @Value("${python.exec:}")
  private String pythonExec;

  @Value("${python.paths:}")
  private String pythonPaths;

  @Value("${python.env:}")
  private String pythonEnv;

  @Value("${objectStorage.builder.bucketName:}")
  private String builderBucketName;

  @Value("${objectStorage.upload.bucketName:}")
  private String uploadBucketName;

  @Value("${yuque.api.url:}")
  private String yuQueApiUrl;
}
