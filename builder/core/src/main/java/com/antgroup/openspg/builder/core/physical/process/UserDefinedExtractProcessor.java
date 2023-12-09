package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.physical.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.PythonOperatorFactory;
import com.antgroup.openspg.builder.core.physical.operator.protocol.EvalResult;
import com.antgroup.openspg.builder.core.physical.operator.protocol.Vertex;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.UserDefinedExtractNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.core.schema.model.type.OperatorKey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

@SuppressWarnings("unchecked")
public class UserDefinedExtractProcessor
    extends BaseExtractProcessor<UserDefinedExtractNodeConfig> {

  private static final ObjectMapper mapper = new ObjectMapper();
  private final OperatorFactory operatorFactory;
  private final OperatorKey operatorKey;

  public UserDefinedExtractProcessor(String id, String name, UserDefinedExtractNodeConfig config) {
    super(id, name, config);
    this.operatorFactory = PythonOperatorFactory.getInstance();
    this.operatorKey = config.getOperatorConfig().toKey();
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    this.operatorFactory.init(context);
    this.operatorFactory.loadOperator(config.getOperatorConfig());
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    List<BaseRecord> results = new ArrayList<>();
    for (BaseRecord record : inputs) {
      BuilderRecord builderRecord = (BuilderRecord) record;
      Vertex inputVertex = new Vertex().setProps(builderRecord.getProps());
      Map<String, Object> result =
          (Map<String, Object>) operatorFactory.invoke(operatorKey, inputVertex);

      EvalResult<List<Vertex>> evalResult =
          mapper.convertValue(result, new TypeReference<EvalResult<List<Vertex>>>() {});
      if (evalResult == null || CollectionUtils.isEmpty(evalResult.getData())) {
        continue;
      }

      for (Vertex vertex : evalResult.getData()) {
        results.add(new BuilderRecord(builderRecord.getRecordId(), null, vertex.getProps()));
      }
    }
    return results;
  }

  @Override
  public void close() throws Exception {}
}
