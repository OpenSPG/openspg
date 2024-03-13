/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io.model;

import lombok.Data;

/**
 * @author kejian
 * @version SLSTableInfo.java, v 0.1 2024年03月05日 11:49 AM kejian
 */
@Data
public class SLSTableInfo extends AbstractTableInfo {
  /** sls project */
  private String project;

  /** sls endpoint */
  private String endpoint;

  /** sls logStore */
  private String logStore;

  /** sls accessId */
  private String accessId;

  /** sls accessKey */
  private String accessKey;

  /** akg Task dev id */
  private String taskId;
}
