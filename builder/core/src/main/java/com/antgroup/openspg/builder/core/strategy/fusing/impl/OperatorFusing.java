package com.antgroup.openspg.builder.core.strategy.fusing.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.fusing.EntityFusing;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinkingImpl;
import com.antgroup.openspg.builder.core.physical.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonOperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonRecordConvertor;
import com.antgroup.openspg.builder.core.physical.operator.protocol.InvokeResultWrapper;
import com.antgroup.openspg.builder.core.physical.operator.protocol.PythonRecord;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.OperatorFusingConfig;
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
public class OperatorFusing implements EntityFusing {

  private BuilderContext context;
  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorFusingConfig fusingConfig;
  private final OperatorFactory operatorFactory;
  private final RecordLinking recordLinking;

  public OperatorFusing(OperatorFusingConfig fusingConfig) {
    this.fusingConfig = fusingConfig;
    this.operatorFactory = PythonOperatorFactory.getInstance();
    this.recordLinking = new RecordLinkingImpl();
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    this.context = context;

    recordLinking.init(context);

    operatorFactory.init(context);
    operatorFactory.loadOperator(fusingConfig.getOperatorConfig());
  }

  @Override
  public List<BaseAdvancedRecord> entityFusing(List<BaseAdvancedRecord> records)
      throws FusingException {
    List<PythonRecord> pythonRecords =
        CollectionsUtils.listMap(records, PythonRecordConvertor::toPythonRecord);
    InvokeResultWrapper<List<PythonRecord>> invokeResultWrapper = null;
    try {
      Map<String, Object> result =
          (Map<String, Object>)
              operatorFactory.invoke(fusingConfig.getOperatorConfig(), pythonRecords);

      invokeResultWrapper =
          mapper.convertValue(
              result, new TypeReference<InvokeResultWrapper<List<PythonRecord>>>() {});
    } catch (Exception e) {
      throw new FusingException(e, "fusing error");
    }

    if (invokeResultWrapper == null || CollectionUtils.isEmpty(invokeResultWrapper.getData())) {
      return Collections.emptyList();
    }
    return CollectionsUtils.listMap(
        invokeResultWrapper.getData(),
        r -> PythonRecordConvertor.toAdvancedRecord(r, recordLinking, context.getCatalog()));
  }
}
