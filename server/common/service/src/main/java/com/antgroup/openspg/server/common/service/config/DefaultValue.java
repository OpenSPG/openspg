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

  @Value("${builder.model.execute.num:5}")
  private Integer modelExecuteNum;

  @Value("${python.exec:}")
  private String pythonExec;

  @Value("${python.paths:}")
  private String pythonPaths;

  @Value("${objectStorage.builder.bucketName:}")
  private String builderBucketName;
}
