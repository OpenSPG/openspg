package com.antgroup.openspgapp.biz.schema;

import com.antgroup.openspg.server.common.model.data.EntitySampleData;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/DataManager.class */
public interface DataManager {
  List<EntitySampleData> getTypeSampleData(Long projectId, String name, Integer limit);
}
