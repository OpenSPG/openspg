package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.fusing.SubjectFusing;
import com.antgroup.openspg.builder.core.strategy.fusing.SubjectFusingImpl;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.core.strategy.predicting.RecordPredicting;
import com.antgroup.openspg.builder.core.strategy.predicting.RecordPredictingImpl;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.pipeline.config.SPGTypeMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.VertexRecordConvertor;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.BaseOntology;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGIdentifierTypeEnum;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.google.common.collect.Lists;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@AllArgsConstructor
public class SPGTypeMappingHelper {

  private final SPGTypeIdentifier identifier;
  private BaseSPGType spgType;

  private RecordLinking recordLinking;
  private RecordPredicting recordPredicting;

  private SubjectFusing subjectFusing;

  private final SPGTypeMappingNodeConfig config;

  public SPGTypeMappingHelper(SPGTypeMappingNodeConfig config) {
    this.config = config;
    this.identifier = SPGTypeIdentifier.parse(config.getSpgType());
  }

  public void init(BuilderContext context) {
    this.spgType = (BaseSPGType) loadSchema(identifier, context.getCatalog());

    this.recordLinking =
        new RecordLinkingImpl(
            Stream.concat(
                    config.getPropertyLinkingConfigs().stream(),
                    config.getRelationLinkingConfigs().stream())
                .collect(Collectors.toList()));
    this.recordLinking.init(context);

    this.recordPredicting =
        new RecordPredictingImpl(
            Stream.concat(
                    config.getPropertyPredictingConfigs().stream(),
                    config.getRelationPredictingConfigs().stream())
                .collect(Collectors.toList()));
    this.recordPredicting.init(context);

    this.subjectFusing = new SubjectFusingImpl(config.getSubjectFusingConfig());
    this.subjectFusing.init(context);
  }

  private BaseOntology loadSchema(BaseSPGIdentifier identifier, BuilderCatalog catalog) {
    SPGIdentifierTypeEnum identifierType = identifier.getIdentifierType();
    if (identifierType == SPGIdentifierTypeEnum.SPG_TYPE) {
      return catalog.getSPGType((SPGTypeIdentifier) identifier);
    }
    throw new IllegalArgumentException("illegal identifier type=" + identifierType);
  }

  public boolean isFiltered(BuilderRecord record) {
    List<SPGTypeMappingNodeConfig.MappingFilter> mappingFilters = config.getMappingFilters();
    if (record.getIdentifier() != null && !record.getIdentifier().equals(identifier)) {
      return true;
    }
    if (CollectionUtils.isEmpty(mappingFilters)) {
      return false;
    }
    for (SPGTypeMappingNodeConfig.MappingFilter mappingFilter : mappingFilters) {
      String columnName = mappingFilter.getColumnName();
      String columnValue = mappingFilter.getColumnValue();

      String propertyValue = record.getPropValue(columnName);
      if (columnValue.equals(propertyValue)) {
        return false;
      }
    }
    return true;
  }

  public List<BaseSPGRecord> toSPGRecords(BuilderRecord record) {
    Map<String, String> propertyValues = propertyMapping(record);
    Map<String, String> relationValues = relationMapping(record);

    List<BaseSPGRecord> results = new ArrayList<>();
    List<BaseAdvancedRecord> advancedRecords = toAdvancedRecord(propertyValues, relationValues);
    for (BaseAdvancedRecord advancedRecord : advancedRecords) {
      if (CollectionUtils.isNotEmpty(advancedRecord.getRelationRecords())) {
        for (RelationRecord relationRecord : advancedRecord.getRelationRecords()) {
          relationRecord.setSrcId(advancedRecord.getId());
          results.add(relationRecord);
        }
      }
      results.add(advancedRecord);
    }
    return results;
  }

  public Map<String, String> propertyMapping(BuilderRecord record) {
    List<SPGTypeMappingNodeConfig.MappingConfig> propertyLinkingConfigs =
        config.getPropertyLinkingConfigs();

    if (CollectionUtils.isEmpty(propertyLinkingConfigs)) {
      throw new BuilderRecordException("subjectMapping cannot be empty");
    }

    Map<String, String> propertyValues = new HashMap<>(record.getProps().size());
    for (SPGTypeMappingNodeConfig.MappingConfig mappingConfig : propertyLinkingConfigs) {
      String source = mappingConfig.getSource();
      String target = mappingConfig.getTarget();
      if (source == null) {
        continue;
      }

      String sourceValue = record.getPropValue(source);
      if (sourceValue != null) {
        propertyValues.put(target, sourceValue);
      }
    }
    return propertyValues;
  }

  public Map<String, String> relationMapping(BuilderRecord record) {
    List<SPGTypeMappingNodeConfig.MappingConfig> relationMappingConfigs =
        config.getRelationLinkingConfigs();
    if (CollectionUtils.isEmpty(relationMappingConfigs)) {
      return Collections.emptyMap();
    }

    Map<String, String> relationValues = new HashMap<>(record.getProps().size());
    for (SPGTypeMappingNodeConfig.MappingConfig mappingConfig : relationMappingConfigs) {
      String source = mappingConfig.getSource();
      String target = mappingConfig.getTarget();
      if (source == null) {
        continue;
      }

      String sourceValue = record.getPropValue(source);
      if (sourceValue != null) {
        relationValues.put(target, sourceValue);
      }
    }

    List<SPGTypeMappingNodeConfig.MappingConfig> subRelationLinkingConfigs =
        config.getSubRelationLinkingConfigs();
    for (SPGTypeMappingNodeConfig.MappingConfig mappingConfig : subRelationLinkingConfigs) {
      String source = mappingConfig.getSource();
      String target = mappingConfig.getTarget();

      String sourceValue = record.getPropValue(source);
      if (sourceValue != null) {
        relationValues.put(target, sourceValue);
      }
    }
    return relationValues;
  }

  private List<BaseAdvancedRecord> toAdvancedRecord(
      Map<String, String> propertyValues, Map<String, String> relationValues) {
    String bizId = propertyValues.get("id");
    if (StringUtils.isBlank(bizId)) {
      throw new BuilderRecordException("");
    }

    BaseAdvancedRecord advancedRecord =
        VertexRecordConvertor.toAdvancedRecord(spgType, bizId, propertyValues);

    advancedRecord.setRelationRecords(
        EdgeRecordConvertor.toRelationRecords(spgType, relationValues));
    recordLinking.linking(advancedRecord);
    recordPredicting.predicting(advancedRecord);
    return subjectFusing.fusing(Lists.newArrayList(advancedRecord));
  }
}
