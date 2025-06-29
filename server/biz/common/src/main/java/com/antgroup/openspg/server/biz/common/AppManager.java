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
