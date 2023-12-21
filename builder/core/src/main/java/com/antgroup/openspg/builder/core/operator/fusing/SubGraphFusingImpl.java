package com.antgroup.openspg.builder.core.operator.fusing;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.BaseFusingConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;

import java.util.*;

import com.antgroup.openspg.core.schema.model.constraint.Constraint;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import org.apache.commons.collections4.CollectionUtils;

public class SubGraphFusingImpl implements SubGraphFusing {

  private final List<BaseMappingNodeConfig.MappingConfig> mappingConfigs;
  private final Map<String, EntityFusing> semanticEntityFusing;

  public SubGraphFusingImpl(List<BaseMappingNodeConfig.MappingConfig> mappingConfigs) {
    this.mappingConfigs = mappingConfigs;
    this.semanticEntityFusing = new HashMap<>(mappingConfigs.size());
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      return;
    }

    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      if (mappingConfig.getOperatorConfig() != null) {
        EntityFusing entityFusing =
            EntityFusingFactory.getEntityFusing(
                (BaseFusingConfig) mappingConfig.getOperatorConfig());
        entityFusing.init(context);
        semanticEntityFusing.put(mappingConfig.getTarget(), entityFusing);
      }
    }
  }

  @Override
  public List<BaseSPGRecord> subGraphFusing(BaseAdvancedRecord advancedRecord)
      throws FusingException {
    List<BaseSPGRecord> results = new ArrayList<>();
    for (BasePropertyRecord propertyRecord : advancedRecord.getProperties()) {
      if (propertyRecord.isSemanticProperty()) {
        EntityFusing entityFusing = semanticEntityFusing.get(propertyRecord.getName());
        if (entityFusing == null) {
          continue;
        }
        List<BaseSPGRecord> propertySPGRecords = toSPGRecords(propertyRecord);
        List<BaseSPGRecord> fusedRecords = entityFusing.entityFusing(propertySPGRecords);
        modifyPropertyRecord(propertyRecord, fusedRecords);
        results.addAll(fusedRecords);
      }
    }
    results.add(advancedRecord);
    return results;
  }

  private List<BaseSPGRecord> toSPGRecords(BasePropertyRecord propertyRecord) {
    List<String> rawValues = propertyRecord.getRawValues();
  }

  private void modifyPropertyRecord(
      BasePropertyRecord propertyRecord, List<BaseSPGRecord> fusedRecord) {}
}
