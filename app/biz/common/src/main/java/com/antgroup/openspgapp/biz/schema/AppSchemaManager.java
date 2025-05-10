package com.antgroup.openspgapp.biz.schema;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspgapp.biz.schema.dto.EntityTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.ProjectSchemaDTO;
import com.antgroup.openspgapp.biz.schema.dto.RelationTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.SchemaTreeDTO;
import java.util.List;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/AppSchemaManager.class */
public interface AppSchemaManager {
  SchemaTreeDTO getProjectEntityType(Long projectId);

  EntityTypeDTO getEntityTypeByName(String entityTypeName);

  RelationTypeDTO getRelationTypeBySpo(String sName, String pName, String oName);

  ProjectSchemaDTO getProjectSchemaDetail(Long projectId);

  ProjectSchemaDTO getSimpleProjectSchema(Long projectId);

  Map<String, JSONObject> getDynamicConfig(String type, List<Long> ids);

  Map<String, String> getSchemaPropertyNameMap(Long projectId);

  String getSchemaScript(Long projectId);

  Map<String, Object> saveSchema(String schema);

  EntityTypeDTO getEntityTypeById(Long id);

  RelationTypeDTO getRelationTypeById(Long id);
}
