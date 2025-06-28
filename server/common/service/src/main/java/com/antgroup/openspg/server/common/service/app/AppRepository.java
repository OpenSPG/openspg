package com.antgroup.openspg.server.common.service.app;

import com.antgroup.openspg.server.api.facade.dto.common.request.AppRequest;
import com.antgroup.openspg.server.common.model.app.App;
import java.util.List;

public interface AppRepository {
  Integer save(App app);

  Integer update(App app);

  App queryById(Long id);

  List<App> queryPage(AppRequest request, int start, int size);

  Long selectCountByCondition(AppRequest request);

  Integer deleteById(Long id);

  App queryByName(String name);

  List<App> queryByCondition(AppRequest request);
}
