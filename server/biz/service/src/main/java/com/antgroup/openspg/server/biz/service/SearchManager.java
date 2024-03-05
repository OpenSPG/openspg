package com.antgroup.openspg.server.biz.service;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSearchRequest;
import java.util.List;

public interface SearchManager {

  List<IdxRecord> spgTypeSearch(SPGTypeSearchRequest request);
}
