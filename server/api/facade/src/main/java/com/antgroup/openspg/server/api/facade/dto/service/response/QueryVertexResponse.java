/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.response;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryVertexResponse {

  private VertexRecord vertex;
}
