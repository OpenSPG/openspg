package com.antgroup.openspg.server.biz.common;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.api.facade.dto.common.request.AppRequest;
import com.antgroup.openspg.server.common.model.app.App;
import java.util.List;
import java.util.Map;

public interface AppManager {
  Long create(AppRequest request);

  Integer update(AppRequest request);

  Integer deleteById(Long id);

  App queryById(Long id);

  List<App> queryPage(AppRequest request, Long page, Long size);

  Long selectCountByCondition(AppRequest request);

  Map<String, Object> getKbIdsAndLLMIdByConfig(JSONObject config);

  App queryByName(String name);

  List<App> queryByAlias(String alias);
}
