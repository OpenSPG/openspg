package com.antgroup.openspg.builder.core.semantic;

import com.antgroup.openspg.builder.core.semantic.impl.IdEqualsPropertyMounter;
import com.antgroup.openspg.builder.core.semantic.impl.OperatorPropertyMounter;
import com.antgroup.openspg.builder.core.semantic.impl.SearchEnginePropertyMounter;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorPropertyMounterConfig;
import com.antgroup.openspg.builder.model.pipeline.config.PropertyMounterConfig;
import com.antgroup.openspg.builder.model.pipeline.config.SearchEnginePropertyMounterConfig;

public class PropertyMounterFactory {

  public static PropertyMounter getPropertyMounter(PropertyMounterConfig config) {
    switch (config.getMounterType()) {
      case OPERATOR:
        return new OperatorPropertyMounter((OperatorPropertyMounterConfig) config);
      case SEARCH_ENGINE:
        return new SearchEnginePropertyMounter((SearchEnginePropertyMounterConfig) config);
      case ID_EQUALS:
        return IdEqualsPropertyMounter.INSTANCE;
      default:
        throw new IllegalArgumentException(
            "illegal property mounter type=" + config.getMounterType());
    }
  }
}
