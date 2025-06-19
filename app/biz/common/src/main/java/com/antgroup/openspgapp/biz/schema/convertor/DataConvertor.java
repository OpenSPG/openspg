package com.antgroup.openspgapp.biz.schema.convertor;

import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import com.antgroup.openspg.server.common.model.data.EntitySampleData;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/convertor/DataConvertor.class */
public class DataConvertor {
  public static EntitySampleData toEntitySampleData(SPGTypeInstance spgTypeInstance) {
    if (null == spgTypeInstance) {
      return null;
    }
    EntitySampleData entitySampleData = new EntitySampleData();
    entitySampleData.setId(spgTypeInstance.getId());
    entitySampleData.setLabel(spgTypeInstance.getSpgType());
    entitySampleData.setProperties(spgTypeInstance.getProperties());
    return entitySampleData;
  }
}
