package com.antgroup.openspg.builder.core.property.impl;

import com.antgroup.openspg.builder.core.physical.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonOperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.protocol.InvokeResult;
import com.antgroup.openspg.builder.core.physical.operator.protocol.InvokeResultWrapper;
import com.antgroup.openspg.builder.core.property.PropertyNormalizer;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorPropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.common.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;

@SuppressWarnings("unchecked")
public class PropertyOperatorNormalizer implements PropertyNormalizer {

  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorPropertyNormalizerConfig normalizerConfig;
  private final OperatorFactory operatorFactory;

  public PropertyOperatorNormalizer(OperatorPropertyNormalizerConfig config) {
    this.normalizerConfig = config;
    this.operatorFactory = PythonOperatorFactory.getInstance();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    operatorFactory.init(context);
    operatorFactory.loadOperator(normalizerConfig.getOperatorConfig());
  }

  @Override
  public void propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException {
    List<String> rawValues = record.getRawValues();

    List<String> ids = new ArrayList<>(rawValues.size());
    for (String rawValue : rawValues) {
      Map<String, Object> result =
          (Map<String, Object>)
              operatorFactory.invoke(
                  normalizerConfig.getOperatorConfig(), rawValue, new HashMap<>(0));
      InvokeResultWrapper<List<InvokeResult>> invokeResultWrapper =
          mapper.convertValue(
              result, new TypeReference<InvokeResultWrapper<List<InvokeResult>>>() {});

      if (invokeResultWrapper == null || CollectionUtils.isEmpty(invokeResultWrapper.getData())) {
        continue;
      }

      for (InvokeResult data : invokeResultWrapper.getData()) {
        String id = data.getId();
        if (StringUtils.isBlank(id)) {
          continue;
        }
        ids.add(id);
      }
    }
    record.getValue().setStds(Collections.singletonList(ids));
    record.getValue().setIds(ids);
  }
}
