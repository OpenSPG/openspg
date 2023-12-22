package com.antgroup.openspg.builder.core.strategy.linking.impl;

import com.antgroup.openspg.builder.core.physical.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonOperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.protocol.InvokeResultWrapper;
import com.antgroup.openspg.builder.core.physical.operator.protocol.PythonRecord;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.PropertyLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.pipeline.config.linking.OperatorLinkingConfig;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.common.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
@SuppressWarnings("unchecked")
public class OperatorLinking implements PropertyLinking {

  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorLinkingConfig linkingConfig;
  private final OperatorFactory operatorFactory;

  public OperatorLinking(OperatorLinkingConfig linkingConfig) {
    this.linkingConfig = linkingConfig;
    this.operatorFactory = PythonOperatorFactory.getInstance();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    operatorFactory.init(context);
    operatorFactory.loadOperator(linkingConfig.getOperatorConfig());
  }

  @Override
  public void propertyLinking(BasePropertyRecord record) throws LinkingException {
    List<String> rawValues = record.getRawValues();

    List<String> ids = new ArrayList<>(rawValues.size());
    for (String rawValue : rawValues) {
      InvokeResultWrapper<List<PythonRecord>> invokeResultWrapper = null;
      try {
        Map<String, Object> result =
            (Map<String, Object>)
                operatorFactory.invoke(
                    linkingConfig.getOperatorConfig(), rawValue, new HashMap<>(0));
        invokeResultWrapper =
            mapper.convertValue(
                result, new TypeReference<InvokeResultWrapper<List<PythonRecord>>>() {});
      } catch (Exception e) {
        throw new LinkingException(e, "{} normalize error", rawValue);
      }

      if (invokeResultWrapper == null || CollectionUtils.isEmpty(invokeResultWrapper.getData())) {
        continue;
      }

      for (PythonRecord data : invokeResultWrapper.getData()) {
        String id = data.getId();
        if (StringUtils.isBlank(id)) {
          continue;
        }
        ids.add(id);
      }
    }
    record.getValue().setStrStds(ids);
    record.getValue().setIds(ids);
  }
}
