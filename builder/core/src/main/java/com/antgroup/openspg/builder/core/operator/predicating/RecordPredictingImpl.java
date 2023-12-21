package com.antgroup.openspg.builder.core.operator.predicating;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PredictingException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public class RecordPredictingImpl implements RecordPredicting {

  private final List<BaseMappingNodeConfig.PredictingConfig> predicatingConfigs;
  private final Map<String, PropertyPredicting> semanticPropertyPredicating;

  public RecordPredictingImpl(List<BaseMappingNodeConfig.PredictingConfig> predicatingConfigs) {
    this.predicatingConfigs = predicatingConfigs;
    this.semanticPropertyPredicating = new HashMap<>();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    if (CollectionUtils.isEmpty(predicatingConfigs)) {
      return;
    }

    for (BaseMappingNodeConfig.PredictingConfig predicatingConfig : predicatingConfigs) {
      PropertyPredicting propertyPredicating =
          PropertyPredictingFactory.getPropertyPredicating(
              predicatingConfig.getPredictingConfig());
      propertyPredicating.init(context);
      semanticPropertyPredicating.put(predicatingConfig.getTarget(), propertyPredicating);
    }
  }

  @Override
  public void propertyPredicating(BaseSPGRecord spgRecord) throws PredictingException {
    // todo
  }
}
