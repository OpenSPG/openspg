package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.common.util.Md5Utils;
import com.antgroup.openspg.core.schema.model.type.OperatorKey;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import pemja.core.PythonInterpreter;
import pemja.core.PythonInterpreterConfig;

@Slf4j
public class PythonOperatorFactory implements OperatorFactory {

  public static final String PYTHON_EXEC = "pythonExec";
  public static final String PYTHON_PATHS = "pythonPaths";
  private static volatile PythonInterpreter pythonInterpreter;
  private final Map<OperatorKey, OperatorConfig> operators = new ConcurrentHashMap<>();
  private final Map<OperatorKey, String> operatorObjects = new ConcurrentHashMap<>();

  private PythonOperatorFactory() {}

  private static final PythonOperatorFactory INSTANCE = new PythonOperatorFactory();

  public static OperatorFactory getInstance() {
    return INSTANCE;
  }

  private static PythonInterpreter newPythonInterpreter(Map<String, Object> params) {
    String[] pythonPaths = (String[]) params.get(PYTHON_PATHS);
    String pythonExec = (String) params.get(PYTHON_EXEC);

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
          pythonInterpreter = newPythonInterpreter(context.getParams());
        }
      }
    }
  }

  @Override
  public boolean register(OperatorConfig config) {
    OperatorKey operatorKey = config.toKey();
    operators.put(operatorKey, config);
    loadOperatorObject(operatorKey, config);
    return true;
  }

  @Override
  public Object invoke(OperatorKey key, Object... input) {
    OperatorConfig config = operators.get(key);
    String operatorObject = operatorObjects.get(key);
    return pythonInterpreter.invokeMethod(operatorObject, config.getMainClass(), input);
  }

  private void loadOperatorObject(OperatorKey key, OperatorConfig config) {
    if (operatorObjects.containsKey(key)) {
      return;
    }

    String address = config.getAddress();
    String pythonFileName =
        address.substring(address.lastIndexOf("/") + 1, address.lastIndexOf(".py"));
    pythonInterpreter.exec("from objectStore.operator import " + pythonFileName);
    String paramMd5 =
        Md5Utils.md5Of(
            String.valueOf(Thread.currentThread().hashCode()),
            config.getParams() == null ? "" : config.getParams().toString());
    String operatorObjectName = String.join("_", config.toKey().toString(), paramMd5);
    String classNamePath =
        config
            .getName()
            .concat("_")
            .concat("v")
            .concat(String.valueOf(config.getVersion()))
            .concat(".")
            .concat(config.getName());
    pythonInterpreter.exec(
        String.format(
            "%s=%s(%s)",
            operatorObjectName, classNamePath, paramToPythonString(config.getParams())));
    operatorObjects.put(key, operatorObjectName);
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
