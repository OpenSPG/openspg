package com.antgroup.openspg.builder.core.property.impl;

import com.antgroup.openspg.builder.core.physical.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonOperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.protocol.EvalResult;
import com.antgroup.openspg.builder.core.physical.operator.protocol.Vertex;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorPropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.core.schema.model.type.OperatorKey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;

@SuppressWarnings("unchecked")
public class OperatorPropertyNormalizer extends AdvancedPropertyNormalizer {

  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorKey operatorKey;
  private final OperatorPropertyNormalizerConfig mounterConfig;
  private OperatorFactory operatorFactory;

  public OperatorPropertyNormalizer(OperatorPropertyNormalizerConfig config) {
    this.mounterConfig = config;
    this.operatorKey = config.getConfig().toKey();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    operatorFactory = PythonOperatorFactory.getInstance();
    operatorFactory.init(context);
    operatorFactory.loadOperator(mounterConfig.getConfig());
  }

  @Override
  public void propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException {
    List<String> rawValues = record.getRawValues();

    // todo
    List<String> stdValues = new ArrayList<>(rawValues.size());
    List<String> ids = new ArrayList<>(rawValues.size());
    for (String rawValue : rawValues) {
      Map<String, Object> result =
          (Map<String, Object>) operatorFactory.invoke(operatorKey, rawValue, new HashMap<>(0));
      EvalResult<List<Vertex>> evalResult =
          mapper.convertValue(result, new TypeReference<EvalResult<List<Vertex>>>() {});

      if (evalResult == null || CollectionUtils.isEmpty(evalResult.getData())) {
        continue;
      }

      evalResult.getData().stream()
          .map(Vertex::getBizId)
          .filter(Objects::nonNull)
          .forEach(ids::add);
    }
    record.getValue().setIds(ids);
  }
}
