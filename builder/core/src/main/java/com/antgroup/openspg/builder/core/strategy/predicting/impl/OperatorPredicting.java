package com.antgroup.openspg.builder.core.strategy.predicting.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.core.strategy.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.strategy.operator.PythonOperatorFactory;
import com.antgroup.openspg.builder.core.strategy.operator.PythonRecordConvertor;
import com.antgroup.openspg.builder.core.strategy.operator.protocol.InvokeResultWrapper;
import com.antgroup.openspg.builder.core.strategy.operator.protocol.PythonRecord;
import com.antgroup.openspg.builder.core.strategy.predicting.PropertyPredicting;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.exception.PredictingException;
import com.antgroup.openspg.builder.model.pipeline.config.predicting.OperatorPredictingConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
@SuppressWarnings("unchecked")
public class OperatorPredicting implements PropertyPredicting {

  private BuilderContext context;
  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorPredictingConfig predicatingConfig;
  private final OperatorFactory operatorFactory;
  private final RecordLinking recordLinking;

  public OperatorPredicting(OperatorPredictingConfig predicatingConfig) {
    this.predicatingConfig = predicatingConfig;
    this.operatorFactory = PythonOperatorFactory.getInstance();
    this.recordLinking = new RecordLinkingImpl();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    this.context = context;

    recordLinking.init(context);

    operatorFactory.init(context);
    operatorFactory.loadOperator(predicatingConfig.getOperatorConfig());
  }

  @Override
  public List<BaseAdvancedRecord> propertyPredicting(BaseAdvancedRecord record)
      throws PredictingException {
    PythonRecord pythonRecord = PythonRecordConvertor.toPythonRecord(record);
    InvokeResultWrapper<List<PythonRecord>> invokeResultWrapper = null;
    try {
      Map<String, Object> result =
          (Map<String, Object>)
              operatorFactory.invoke(predicatingConfig.getOperatorConfig(), pythonRecord);

      invokeResultWrapper =
          mapper.convertValue(
              result, new TypeReference<InvokeResultWrapper<List<PythonRecord>>>() {});
    } catch (Exception e) {
      throw new FusingException(e, "predicating error");
    }

    if (invokeResultWrapper == null || CollectionUtils.isEmpty(invokeResultWrapper.getData())) {
      return Collections.emptyList();
    }

    return CollectionsUtils.listMap(
        invokeResultWrapper.getData(),
        r -> PythonRecordConvertor.toAdvancedRecord(r, recordLinking, context.getCatalog()));
  }
}
