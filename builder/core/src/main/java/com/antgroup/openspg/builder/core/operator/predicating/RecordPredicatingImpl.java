package com.antgroup.openspg.builder.core.operator.predicating;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PredicatingException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public class RecordPredicatingImpl implements RecordPredicating {

  private final List<BaseMappingNodeConfig.PredicatingConfig> predicatingConfigs;
  private final Map<String, PropertyPredicating> semanticPropertyPredicating;

  public RecordPredicatingImpl(List<BaseMappingNodeConfig.PredicatingConfig> predicatingConfigs) {
    this.predicatingConfigs = predicatingConfigs;
    this.semanticPropertyPredicating = new HashMap<>();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    if (CollectionUtils.isEmpty(predicatingConfigs)) {
      return;
    }

    for (BaseMappingNodeConfig.PredicatingConfig predicatingConfig : predicatingConfigs) {
      PropertyPredicating propertyPredicating =
          PropertyPredicatingFactory.getPropertyPredicating(
              predicatingConfig.getPredicatingConfig());
      propertyPredicating.init(context);
      semanticPropertyPredicating.put(predicatingConfig.getTarget(), propertyPredicating);
    }
  }

  @Override
  public void propertyPredicating(BaseSPGRecord spgRecord) throws PredicatingException {
    // todo
  }
}
