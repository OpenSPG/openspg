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

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.server.api.facade.dto.service.request.CustomSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.TextSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.VectorSearchRequest;
import java.util.List;

public interface SearchManager {

  List<IdxRecord> spgTypeSearch(SPGTypeSearchRequest request);

  List<IdxRecord> textSearch(TextSearchRequest request);

  List<IdxRecord> vectorSearch(VectorSearchRequest request);

  List<IdxRecord> customSearch(CustomSearchRequest request);
}
