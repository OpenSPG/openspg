package com.antgroup.openspg.builder.core.property;

import com.antgroup.openspg.builder.core.property.impl.AdvancedPropertyNormalizer;
import com.antgroup.openspg.builder.core.property.impl.BasicPropertyNormalizer;
import com.antgroup.openspg.builder.core.property.impl.SearchPropertyNormalizer;
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
  private final Map<String, List<AdvancedPropertyNormalizer>> advancedPropertyNormalizers;
  private final AdvancedPropertyNormalizer backupPropertyNormalizer;

  public RecordNormalizerImpl(List<BaseMappingNodeConfig.MappingConfig> mappingConfigs) {
    this.mappingConfigs = mappingConfigs;
    this.basicPropertyNormalizer = new BasicPropertyNormalizer();
    this.advancedPropertyNormalizers = new HashMap<>(mappingConfigs.size());
    this.backupPropertyNormalizer = new SearchPropertyNormalizer();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    basicPropertyNormalizer.init(context);
    backupPropertyNormalizer.init(context);
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      return;
    }
    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      List<AdvancedPropertyNormalizer> normalizers =
          new ArrayList<>(mappingConfig.getNormalizerConfigs().size());
      for (PropertyNormalizerConfig normalizerConfig : mappingConfig.getNormalizerConfigs()) {
        AdvancedPropertyNormalizer normalizer =
            AdvancedPropertyNormalizer.getPropertyNormalizer(normalizerConfig);
        normalizer.init(context);
        normalizers.add(normalizer);
      }
      advancedPropertyNormalizers.put(mappingConfig.getTarget(), normalizers);
    }
  }

  @Override
  public void propertyNormalize(BaseSPGRecord spgRecord) throws PropertyNormalizeException {
    try {
      for (BasePropertyRecord propertyRecord : spgRecord.getProperties()) {
        if (propertyRecord.isSemanticProperty()) {
          List<AdvancedPropertyNormalizer> normalizers =
              advancedPropertyNormalizers.get(propertyRecord.getName());
          if (normalizers != null) {
            for (AdvancedPropertyNormalizer normalizer : normalizers) {
              normalizer.propertyNormalize(propertyRecord);
            }
          }
          if (backupPropertyNormalizer != null) {
            backupPropertyNormalizer.propertyNormalize(propertyRecord);
          }
        } else {
          basicPropertyNormalizer.propertyNormalize(propertyRecord);
        }
      }
    } catch (Exception e) {
      throw new PropertyNormalizeException(e, "property normalizer error");
    }
  }
}
