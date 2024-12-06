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

package com.antgroup.openspg.server.biz.service;

import com.antgroup.openspg.server.api.facade.dto.service.request.*;
import com.antgroup.openspg.server.api.facade.dto.service.response.ExpendOneHopResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.ManipulateDataResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.PageRankScoreInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.QueryVertexResponse;
import java.util.List;

public interface GraphManager {

  List<String> getAllLabels(GetAllLabelsRequest request);

  ManipulateDataResponse upsertVertex(UpsertVertexRequest request);

  ManipulateDataResponse deleteVertex(DeleteVertexRequest request);

  ManipulateDataResponse upsertEdge(UpsertEdgeRequest request);

  ManipulateDataResponse deleteEdgeRequest(DeleteEdgeRequest request);

  List<PageRankScoreInstance> getPageRankScores(GetPageRankScoresRequest request);

  QueryVertexResponse queryVertex(QueryVertexRequest request);

  ExpendOneHopResponse expendOneHop(ExpendOneHopRequest request);
}
