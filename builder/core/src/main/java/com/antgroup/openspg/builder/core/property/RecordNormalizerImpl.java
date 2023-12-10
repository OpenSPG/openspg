package com.antgroup.openspg.builder.core.property;

import com.antgroup.openspg.builder.core.property.impl.BasicPropertyNormalizer;
import com.antgroup.openspg.builder.core.property.impl.PropertySearchNormalizer;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public class RecordNormalizerImpl implements RecordNormalizer {

  private final List<BaseMappingNodeConfig.MappingConfig> mappingConfigs;
  private final BasicPropertyNormalizer basicPropertyNormalizer;
  private final Map<String, PropertyNormalizer> semanticPropertyNormalizers;
  private final PropertyNormalizer defaultPropertyNormalizer;

  public RecordNormalizerImpl(List<BaseMappingNodeConfig.MappingConfig> mappingConfigs) {
    this.mappingConfigs = mappingConfigs;
    this.basicPropertyNormalizer = new BasicPropertyNormalizer();
    this.semanticPropertyNormalizers = new HashMap<>(mappingConfigs.size());
    this.defaultPropertyNormalizer = new PropertySearchNormalizer();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    basicPropertyNormalizer.init(context);
    defaultPropertyNormalizer.init(context);
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      return;
    }
    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      PropertyNormalizer normalizer =
          PropertyNormalizerFactory.getPropertyNormalizer(mappingConfig.getNormalizerConfig());
      normalizer.init(context);
      semanticPropertyNormalizers.put(mappingConfig.getTarget(), normalizer);
    }
  }

  @Override
  public void propertyNormalize(BaseSPGRecord spgRecord) throws PropertyNormalizeException {
    for (BasePropertyRecord propertyRecord : spgRecord.getProperties()) {
      if (propertyRecord.isSemanticProperty()) {
        PropertyNormalizer normalizer = semanticPropertyNormalizers.get(propertyRecord.getName());
        if (normalizer != null) {
          normalizer.propertyNormalize(propertyRecord);
        } else {
          // we use default normalizer to normalize property value
          defaultPropertyNormalizer.propertyNormalize(propertyRecord);
        }
      } else {
        basicPropertyNormalizer.propertyNormalize(propertyRecord);
      }
    }
  }
}
