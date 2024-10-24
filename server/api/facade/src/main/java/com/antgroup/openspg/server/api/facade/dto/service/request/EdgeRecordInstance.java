/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.request;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Data;

@Data
public class EdgeRecordInstance {

  private String srcType;
  private String srcId;
  private String dstType;
  private String dstId;
  private String label;
  private Map<String, Object> properties = Maps.newHashMap();;
}
