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
package com.antgroup.openspg.common.util;

import java.io.File;
import org.springframework.util.Assert;

public class CommonUtils {

  /** The maximum number of non-paging entries for a DB query */
  public static final int INNER_QUERY_MAX_COUNT = 10000;

  public static void checkQueryPage(int count, Integer pageNo, Integer pageSize) {
    // pageNo is empty to indicate no paging
    if (pageNo == null) {
      // If the query is all, it must be less than the maximum limit to prevent OOM
      Assert.isTrue(
          count <= INNER_QUERY_MAX_COUNT,
          String.format(
              "The current query data volume %s exceeds the maximum limit %s, please use pagination query",
              count, INNER_QUERY_MAX_COUNT));
      return;
    }
    // When pageNo is not empty, pageSize cannot be empty either
    Assert.notNull(pageSize, "pageSize cannot be null");
    // pageSize cannot be larger than the maximum value
    Assert.isTrue(
        pageSize <= INNER_QUERY_MAX_COUNT,
        String.format(
            "The current query data volume %s exceeds the maximum limit %s, please use pagination query",
            pageSize, INNER_QUERY_MAX_COUNT));
  }

  public static String getInstanceStorageFileKey(Long projectId, Long instanceId) {
    return "builder"
        + File.separator
        + "project_"
        + projectId
        + File.separator
        + "instance_"
        + instanceId
        + File.separator;
  }

  public static String getTaskStorageFileKey(
      Long projectId, Long instanceId, Long taskId, String type) {
    return getInstanceStorageFileKey(projectId, instanceId) + taskId + "_" + type + ".kag";
  }
}
