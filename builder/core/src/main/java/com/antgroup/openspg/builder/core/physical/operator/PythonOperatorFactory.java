package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import pemja.core.PythonInterpreter;
import pemja.core.PythonInterpreterConfig;

@Slf4j
public class PythonOperatorFactory implements OperatorFactory {

  private static volatile PythonInterpreter pythonInterpreter;
  private final Set<String> operatorObjects = Collections.newSetFromMap(new ConcurrentHashMap<>());

  private PythonOperatorFactory() {}

  private static final PythonOperatorFactory INSTANCE = new PythonOperatorFactory();

  public static OperatorFactory getInstance() {
    return INSTANCE;
  }

  private static PythonInterpreter newPythonInterpreter(BuilderContext context) {
    String pythonExec = context.getPythonExec();
    String[] pythonPaths =
        (context.getPythonPaths() != null ? context.getPythonPaths().split(",") : null);

    PythonInterpreterConfig.PythonInterpreterConfigBuilder builder =
        PythonInterpreterConfig.newBuilder();
    log.info("pythonExec={}, pythonPaths={}", pythonExec, Arrays.toString(pythonPaths));
    if (pythonExec != null) {
      builder.setPythonExec(pythonExec);
    }
    if (pythonPaths != null) {
      builder.addPythonPaths(pythonPaths);
    }
    return new PythonInterpreter(builder.build());
  }

  @Override
  public void init(BuilderContext context) {
    if (pythonInterpreter == null) {
      synchronized (PythonOperatorFactory.class) {
        if (pythonInterpreter == null) {
          pythonInterpreter = newPythonInterpreter(context);
        }
      }
    }
  }

  @Override
  public void loadOperator(OperatorConfig config) {
    loadOperatorObject(config);
  }

  @Override
  public Object invoke(OperatorConfig config, Object... input) {
    return pythonInterpreter.invokeMethod(config.getUniqueKey(), config.getMethod(), input);
  }

  private void loadOperatorObject(OperatorConfig config) {
    String pythonOperatorObject = config.getUniqueKey();
    if (operatorObjects.contains(pythonOperatorObject)) {
      return;
    }

    pythonInterpreter.exec(
        String.format("from %s import %s", config.getModulePath(), config.getClassName()));
    pythonInterpreter.exec(
        String.format(
            "%s=%s(%s)",
            pythonOperatorObject, config.getClassName(), paramToPythonString(config.getParams())));
    operatorObjects.add(pythonOperatorObject);
  }

  private String paramToPythonString(Map<String, String> params) {
    if (MapUtils.isEmpty(params)) {
      return "";
    }
    String keyValue =
        params.entrySet().stream()
            .map(entry -> String.format("'%s': '%s'", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(","));
    return String.format("{%s}", keyValue);
  }
}
