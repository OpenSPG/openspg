/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.response;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.BaseLPGRecord;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathMatchResponse {

  private List<Path> paths;

  public static class Path {

    /**
     * A list that describes the vertex records and edge records along a path.
     *
     * <p>The first element in the list represents the starting vertex of the path. Following this,
     * there are N pairs of edge record and vertex record, where each pair consists of an edge and
     * its adjacent vertex.
     *
     * <p>For a path with N hops (or steps), the list will contain 2N + 1 elements: - 1 element for
     * the starting point. - N pairs of edge and vertex, representing the intermediate edges and
     * their adjacent vertex.
     *
     * <p>Example for a 2-hop path: - Element 0: Starting point - Element 1: Edge between starting
     * vertex and the next vertex - Element 2: Next vertex - Element 3: Edge between the next vertex
     * and the final vertex - Element 4: Final vertex
     */
    private List<BaseLPGRecord> records;
  }
}
