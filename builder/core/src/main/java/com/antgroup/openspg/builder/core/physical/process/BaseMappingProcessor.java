package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.VertexRecordConvertor;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.BaseOntology;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGIdentifierTypeEnum;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public abstract class BaseMappingProcessor<T extends BaseMappingNodeConfig>
    extends BaseProcessor<T> {

  public BaseMappingProcessor(String id, String name, T config) {
    super(id, name, config);
  }

  protected BaseOntology loadSchema(BaseSPGIdentifier identifier, BuilderCatalog catalog) {
    SPGIdentifierTypeEnum identifierType = identifier.getIdentifierType();
    switch (identifierType) {
      case SPG_TYPE:
        return catalog.getSPGType((SPGTypeIdentifier) identifier);
      case RELATION:
        return catalog.getRelation((RelationIdentifier) identifier);
      default:
        throw new IllegalArgumentException("illegal identifier type=" + identifierType);
    }
  }

  protected static boolean isFiltered(
      BuilderRecord record, List<BaseMappingNodeConfig.MappingFilter> mappingFilters) {
    if (CollectionUtils.isEmpty(mappingFilters)) {
      return false;
    }
    for (BaseMappingNodeConfig.MappingFilter mappingFilter : mappingFilters) {
      String columnName = mappingFilter.getColumnName();
      String columnValue = mappingFilter.getColumnValue();

      String propertyValue = record.getPropValue(columnName);
      if (columnValue.equals(propertyValue)) {
        return false;
      }
    }
    return true;
  }

  protected static <T extends BaseMappingNodeConfig.MappingConfig> BuilderRecord mapping(
      BuilderRecord record, List<T> mappingConfigs) {
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      // if empty, perform mapping with the same name
      return record;
    }
    Map<String, String> newProps = new HashMap<>(record.getProps().size());
    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      String source = mappingConfig.getSource();
      String target = mappingConfig.getTarget();

      String sourceValue = record.getPropValue(source);
      if (sourceValue != null) {
        newProps.put(target, sourceValue);
      }
    }
    return record.withNewProps(newProps);
  }

  public static BaseAdvancedRecord toSPGRecord(BuilderRecord record, BaseSPGType spgType) {
    String bizId = record.getPropValue("id");
    if (StringUtils.isBlank(bizId)) {
      throw new BuilderRecordException("");
    }
    return VertexRecordConvertor.toAdvancedRecord(spgType, bizId, record.getProps());
  }

  public static RelationRecord toSPGRecord(BuilderRecord record, Relation relation) {
    String srcId = record.getPropValue("srcId");
    String dstId = record.getPropValue("dstId");
    if (StringUtils.isBlank(srcId) || StringUtils.isBlank(dstId)) {
      throw new BuilderRecordException("");
    }
    return EdgeRecordConvertor.toRelationRecord(relation, srcId, dstId, record.getProps());
  }
}
