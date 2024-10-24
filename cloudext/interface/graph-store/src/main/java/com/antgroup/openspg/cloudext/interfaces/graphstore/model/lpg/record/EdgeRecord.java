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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link EdgeRecord EdgeRecord} represents the persistent record of a {@link EdgeType EdgeType},
 * identified by start vertex's ID <tt>(srcId)</tt>, end vertex's ID <tt>(dstId)</tt>, and version
 * number <tt>(version)</tt>. <strong>NOTE:</strong> If <tt>version</tt> is not specified, the
 * default value is <strong>ZERO</strong>.
 */
@Getter
public class EdgeRecord extends BaseLPGRecord {

  private static final Long DEFAULT_VERSION = 0L;

  @Setter private EdgeTypeName edgeType;

  /** Source vertex's ID */
  private final String srcId;

  /** Destination vertex's ID */
  private final String dstId;

  /** Version number */
  @Setter private Long version = DEFAULT_VERSION;

  public EdgeRecord(
      String srcId, String dstId, EdgeTypeName edgeType, List<LPGPropertyRecord> properties) {
    super(LPGRecordTypeEnum.EDGE, properties);
    this.srcId = srcId;
    this.dstId = dstId;
    this.edgeType = edgeType;
  }

  public EdgeRecord(
      String srcId,
      String dstId,
      EdgeTypeName edgeType,
      List<LPGPropertyRecord> properties,
      Long version) {
    super(LPGRecordTypeEnum.EDGE, properties);
    this.srcId = srcId;
    this.dstId = dstId;
    this.edgeType = edgeType;
    this.version = version == null ? DEFAULT_VERSION : version;
  }

  @Override
  public Map<String, Object> toPropertyMapWithId() {
    Map<String, Object> otherProperties = toPropertyMap();
    otherProperties.put(EdgeType.SRC_ID, srcId);
    otherProperties.put(EdgeType.DST_ID, dstId);
    otherProperties.put(EdgeType.VERSION, version);
    return otherProperties;
  }

  @Override
  public String generateUniqueString() {
    return srcId
        + UNIQUE_STRING_SEPARATOR
        + edgeType.toString()
        + UNIQUE_STRING_SEPARATOR
        + dstId
        + UNIQUE_STRING_SEPARATOR
        + version;
  }
}
