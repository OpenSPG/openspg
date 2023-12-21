package com.antgroup.openspg.builder.core.operator.python;

import com.antgroup.openspg.builder.core.operator.linking.RecordLinking;
import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.VertexRecordConvertor;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.Map;

public class PythonRecordConvertor {

  public static BaseAdvancedRecord toAdvancedRecord(
      PythonRecord pythonRecord, RecordLinking recordLinking, BuilderCatalog catalog) {
    String recordId = pythonRecord.getId();
    if (StringUtils.isBlank(recordId)) {
      return null;
    }

    BaseSPGType spgType =
        catalog.getSPGType(SPGTypeIdentifier.parse(pythonRecord.getSpgTypeName()));
    if (spgType == null) {
      return null;
    }
    BaseAdvancedRecord advancedRecord =
        VertexRecordConvertor.toAdvancedRecord(spgType, recordId, pythonRecord.getProperties());
    recordLinking.propertyLinking(advancedRecord);
    return advancedRecord;
  }

  public static PythonRecord toPythonRecord(BaseAdvancedRecord advancedRecord) {
    Map<String, String> stdStrPropertyValueMap = advancedRecord.getStdStrPropertyValueMap();
    stdStrPropertyValueMap.put("id", advancedRecord.getId());
    return new PythonRecord()
        .setSpgTypeName(advancedRecord.getName())
        .setProperties(stdStrPropertyValueMap);
  }
}
