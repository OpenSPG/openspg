package com.antgroup.openspg.builder.core.operator.linking;

import com.antgroup.openspg.builder.core.operator.linking.impl.BasicPropertyLinking;
import com.antgroup.openspg.builder.core.operator.linking.impl.IdEqualsLinking;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.pipeline.config.BaseMappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.linking.BaseLinkingConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

public class RecordLinkingImpl implements RecordLinking {

  private final List<BaseMappingNodeConfig.MappingConfig> mappingConfigs;
  private final BasicPropertyLinking basicPropertyLinking;
  private final Map<String, PropertyLinking> semanticPropertyLinking;

  @Setter private PropertyLinking defaultPropertyLinking = IdEqualsLinking.INSTANCE;

  public RecordLinkingImpl(List<BaseMappingNodeConfig.MappingConfig> mappingConfigs) {
    this.mappingConfigs = mappingConfigs;
    this.basicPropertyLinking = new BasicPropertyLinking();
    this.semanticPropertyLinking = new HashMap<>(mappingConfigs.size());
  }

  public RecordLinkingImpl() {
    this(Collections.emptyList());
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    basicPropertyLinking.init(context);
    defaultPropertyLinking.init(context);
    if (CollectionUtils.isEmpty(mappingConfigs)) {
      return;
    }
    for (BaseMappingNodeConfig.MappingConfig mappingConfig : mappingConfigs) {
      if (mappingConfig.getOperatorConfig() != null) {
        PropertyLinking propertyLinking =
            PropertyLinkingFactory.getPropertyLinking(
                (BaseLinkingConfig) mappingConfig.getOperatorConfig());
        propertyLinking.init(context);
        semanticPropertyLinking.put(mappingConfig.getTarget(), propertyLinking);
      }
    }
  }

  @Override
  public void propertyLinking(BaseSPGRecord spgRecord) throws LinkingException {
    for (BasePropertyRecord propertyRecord : spgRecord.getProperties()) {
      if (propertyRecord.isSemanticProperty()) {
        PropertyLinking propertyLinking = semanticPropertyLinking.get(propertyRecord.getName());
        if (propertyLinking != null) {
          // we use user-defined normalizer to normalize property value
          propertyLinking.propertyLinking(propertyRecord);
        } else {
          // we use default normalizer to normalize property value
          defaultPropertyLinking.propertyLinking(propertyRecord);
        }
      } else {
        // we use basic normalizer to normalize property value
        basicPropertyLinking.propertyLinking(propertyRecord);
      }
    }
  }
}
