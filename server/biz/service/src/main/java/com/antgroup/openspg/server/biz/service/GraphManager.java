/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.biz.service;

import com.antgroup.openspg.server.api.facade.dto.service.request.*;
import com.antgroup.openspg.server.api.facade.dto.service.response.ManipulateDataResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.PageRankScoreInstance;
import java.util.List;

public interface GraphManager {

  List<String> getAllLabels(GetAllLabelsRequest request);

  ManipulateDataResponse upsertVertex(UpsertVertexRequest request);

  ManipulateDataResponse deleteVertex(DeleteVertexRequest request);

  ManipulateDataResponse upsertEdge(UpsertEdgeRequest request);

  ManipulateDataResponse deleteEdgeRequest(DeleteEdgeRequest request);

  List<PageRankScoreInstance> getPageRankScores(GetPageRankScoresRequest request);
}
