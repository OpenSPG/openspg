/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.request;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Data;

@Data
public class VertexRecordInstance {

  private String type;
  private String id;
  private Map<String, Object> properties = Maps.newHashMap();
  private Map<String, float[]> vectors = Maps.newHashMap();
}
