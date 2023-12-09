package com.antgroup.openspg.builder.core.normalize;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class RecordNormalizerImpl implements RecordNormalizer {

  private final List<BaseMappingNodeConfig.MappingConfig> mappingConfigs;
  private final Map<String, List<AdvancedPropertyNormalizer>> propertyNormalizers;

  public RecordNormalizerImpl(List<BaseMappingNodeConfig.MappingConfig> mappingConfigs) {
    this.mappingConfigs = mappingConfigs;
    this.propertyNormalizers = new HashMap<>(mappingConfigs.size());
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      return;
    }
    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      propertyNormalizers.put(
          mappingConfig.getTarget(),
          mappingConfig.getMounterConfigs().stream()
              .map(AdvancedPropertyNormalizer::getPropertyNormalizer)
              .collect(Collectors.toList()));
    }
  }

  @Override
  public void propertyNormalize(BaseSPGRecord spgRecord) throws PropertyNormalizeException {}
}
