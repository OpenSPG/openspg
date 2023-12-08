package com.antgroup.openspg.builder.core.physical.process.pattern;

import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.core.semantic.PropertyMounterFactory;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.MappingPatternEnum;
import com.antgroup.openspg.builder.model.record.*;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.VertexRecordConvertor;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

@Getter
@AllArgsConstructor
public abstract class BaseMappingPattern {

  private final MappingPatternEnum type;

  public abstract void loadMappingConfig(MappingNodeConfig config);

  public abstract void loadAndCheckSchema(ProjectSchema schema);

  public abstract void loadPropertyMounter();

  public abstract List<BaseRecord> mapping(List<BaseRecord> inputs);

  protected boolean isFiltered(
      BuilderRecord record, List<MappingNodeConfig.MappingFilter> mappingFilters) {
    if (CollectionUtils.isEmpty(mappingFilters)) {
      return false;
    }
    for (MappingNodeConfig.MappingFilter mappingFilter : mappingFilters) {
      String columnName = mappingFilter.getColumnName();
      String columnValue = mappingFilter.getColumnValue();

      String propertyValue = record.getPropValue(columnName);
      if (columnValue.equals(propertyValue)) {
        return false;
      }
    }
    return true;
  }

  protected Map<String, List<PropertyMounter>> loadPropertyMounter(
      List<MappingNodeConfig.MappingSchema> mappingSchemas) {
    if (CollectionUtils.isEmpty(mappingSchemas)) {
      return new HashMap<>(0);
    }
    Map<String, List<PropertyMounter>> results = new HashMap<>(mappingSchemas.size());
    for (MappingNodeConfig.MappingSchema mappingSchema : mappingSchemas) {
      List<PropertyMounter> propertyMounters =
          mappingSchema.getPropertyMounterConfigs().stream()
              .map(PropertyMounterFactory::getPropertyMounter)
              .collect(Collectors.toList());
      results.put(mappingSchema.getPropertyName(), propertyMounters);
    }
    return results;
  }

  protected BuilderRecord doMapping(
      BuilderRecord record, List<MappingNodeConfig.MappingConfig> mappingConfigs) {
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      // todo 同名映射
      //      throw new PipelineConfigException();
    }
    Map<String, String> newProps = new HashMap<>(record.getProps().size());
    for (MappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      String source = mappingConfig.getSource();
      List<String> targets = mappingConfig.getTarget();

      String sourceValue = record.getPropValue(source);
      for (String target : targets) {
        newProps.put(target, sourceValue);
      }
    }
    return record.withNewProps(newProps);
  }

  protected BaseAdvancedRecord toSPGRecord(BuilderRecord record, BaseSPGType spgType) {
    String bizId = record.getPropValue("id");
    if (StringUtils.isBlank(bizId)) {
      // todo
      //      throw new BuilderRecordException();
    }
    return VertexRecordConvertor.toAdvancedRecord(spgType, bizId, record.getProps());
  }

  protected RelationRecord toSPGRecord(BuilderRecord record, Relation relation) {
    String srcId = record.getPropValue("srcId");
    String dstId = record.getPropValue("dstId");
    if (StringUtils.isBlank(srcId) || StringUtils.isBlank(dstId)) {
      //      throw new BuilderRecordException();
    }
    return EdgeRecordConvertor.toRelationRecord(relation, srcId, dstId, record.getProps());
  }

  protected void propertyMount(
      BaseAdvancedRecord advancedRecord, Map<String, List<PropertyMounter>> propertyMounters) {
    if (MapUtils.isEmpty(propertyMounters)) {
      return;
    }

    for (SPGPropertyRecord propertyRecord : advancedRecord.getSpgProperties()) {
      if (!propertyRecord.getProperty().getObjectTypeRef().isAdvancedType()) {
        continue;
      }
      List<PropertyMounter> mounters = propertyMounters.get(propertyRecord.getName());
      if (CollectionUtils.isEmpty(mounters)) {
        continue;
      }
      mounters.forEach(mounter -> mounter.propertyMount(propertyRecord));
    }
  }
}
