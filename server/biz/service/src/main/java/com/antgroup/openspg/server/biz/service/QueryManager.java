package com.antgroup.openspg.server.biz.service;

import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import java.util.List;

public interface QueryManager {

  List<SPGTypeInstance> query(SPGTypeQueryRequest query);
}
