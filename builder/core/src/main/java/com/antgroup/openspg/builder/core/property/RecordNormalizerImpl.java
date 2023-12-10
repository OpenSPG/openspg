package com.antgroup.openspg.builder.core.property;

import com.antgroup.openspg.builder.core.property.impl.BasicPropertyNormalizer;
import com.antgroup.openspg.builder.core.property.impl.PropertySearchNormalizer;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.PropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public class RecordNormalizerImpl implements RecordNormalizer {

  private final List<BaseMappingNodeConfig.MappingConfig> mappingConfigs;
  private final BasicPropertyNormalizer basicPropertyNormalizer;
  private final Map<String, List<PropertyNormalizer>> semanticPropertyNormalizers;
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
      List<PropertyNormalizer> normalizers =
          new ArrayList<>(mappingConfig.getNormalizerConfigs().size());
      for (PropertyNormalizerConfig normalizerConfig : mappingConfig.getNormalizerConfigs()) {
        PropertyNormalizer normalizer =
            PropertyNormalizerFactory.getPropertyNormalizer(normalizerConfig);
        normalizer.init(context);
        normalizers.add(normalizer);
      }
      semanticPropertyNormalizers.put(mappingConfig.getTarget(), normalizers);
    }
  }

  @Override
  public void propertyNormalize(BaseSPGRecord spgRecord) throws PropertyNormalizeException {
    for (BasePropertyRecord propertyRecord : spgRecord.getProperties()) {
      boolean propertyNormalized = false;
      try {
        if (propertyRecord.isSemanticProperty()) {
          List<PropertyNormalizer> normalizers =
              semanticPropertyNormalizers.get(propertyRecord.getName());
          if (normalizers != null) {
            for (PropertyNormalizer normalizer : normalizers) {
              propertyNormalized = normalizer.propertyNormalize(propertyRecord);
              if (propertyNormalized) {
                break;
              }
            }
          } else {
            // we use default normalizer to normalize property value
            propertyNormalized = defaultPropertyNormalizer.propertyNormalize(propertyRecord);
          }
        } else {
          propertyNormalized = basicPropertyNormalizer.propertyNormalize(propertyRecord);
        }
      } catch (Exception e) {
        throw new PropertyNormalizeException(e, "property normalizer error");
      }

      if (!propertyNormalized) {
        throw new PropertyNormalizeException("");
      }
    }
  }
}
