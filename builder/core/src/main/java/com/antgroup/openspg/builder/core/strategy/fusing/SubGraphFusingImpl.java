package com.antgroup.openspg.builder.core.strategy.fusing;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.BaseFusingConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.VertexRecordConvertor;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class SubGraphFusingImpl implements SubGraphFusing {

  private BuilderContext context;
  private final List<BaseMappingNodeConfig.MappingConfig> mappingConfigs;
  private final Map<String, EntityFusing> semanticEntityFusing;
  private final RecordLinking recordLinking;

  public SubGraphFusingImpl(
      List<BaseMappingNodeConfig.MappingConfig> mappingConfigs, RecordLinking recordLinking) {
    this.mappingConfigs = mappingConfigs;
    this.semanticEntityFusing = new HashMap<>(mappingConfigs.size());
    this.recordLinking = recordLinking;
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    this.context = context;
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      return;
    }

    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      if (mappingConfig.getStrategyConfig() != null) {
        EntityFusing entityFusing =
            EntityFusingFactory.getEntityFusing(
                (BaseFusingConfig) mappingConfig.getStrategyConfig());
        entityFusing.init(context);
        semanticEntityFusing.put(mappingConfig.getTarget(), entityFusing);
      }
    }
  }

  @Override
  public List<BaseAdvancedRecord> subGraphFusing(BaseAdvancedRecord advancedRecord)
      throws FusingException {
    List<BaseAdvancedRecord> results = new ArrayList<>();
    for (BasePropertyRecord propertyRecord : advancedRecord.getProperties()) {
      if (propertyRecord.isSemanticProperty()) {
        EntityFusing entityFusing = semanticEntityFusing.get(propertyRecord.getName());
        if (entityFusing == null) {
          continue;
        }
        List<BaseAdvancedRecord> advancedRecords = toAdvancedRecords(propertyRecord);
        advancedRecords.forEach(recordLinking::propertyLinking);
        List<BaseAdvancedRecord> fusedRecords = entityFusing.entityFusing(advancedRecords);
        modifyPropertyRecord(propertyRecord, fusedRecords);
        results.addAll(fusedRecords);
      }
    }
    results.add(advancedRecord);
    return results;
  }

  private List<BaseAdvancedRecord> toAdvancedRecords(BasePropertyRecord propertyRecord) {
    List<String> rawValues = propertyRecord.getRawValues();
    BaseSPGType spgType =
        context.getCatalog().getSPGType(propertyRecord.getObjectTypeRef().getBaseSpgIdentifier());
    return CollectionsUtils.listMap(
        rawValues,
        rawValue -> {
          Map<String, String> properties = new HashMap<>(2);
          properties.put("id", rawValue);
          properties.put("name", rawValue);
          return VertexRecordConvertor.toAdvancedRecord(spgType, rawValue, properties);
        });
  }

  private void modifyPropertyRecord(
      BasePropertyRecord propertyRecord, List<BaseAdvancedRecord> fusedRecord) {
    if (!propertyRecord.isMultiValue()) {
      BaseAdvancedRecord advancedRecord = fusedRecord.get(0);
      String bizId = advancedRecord.getId();
      propertyRecord.getValue().setSingleStd(bizId);
      propertyRecord.getValue().setSingleId(bizId);
    } else {
      List<String> bizIds =
          fusedRecord.stream()
              .map(BaseAdvancedRecord::getId)
              .distinct()
              .collect(Collectors.toList());
      propertyRecord.getValue().setStrStds(bizIds);
      propertyRecord.getValue().setIds(bizIds);
    }
  }
}
