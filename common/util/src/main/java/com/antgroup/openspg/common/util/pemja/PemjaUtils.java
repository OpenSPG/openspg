package com.antgroup.openspg.common.util.pemja;

import com.antgroup.openspg.common.util.Md5Utils;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pemja.core.PythonInterpreter;
import pemja.core.PythonInterpreterConfig;

public class PemjaUtils {

  public static Object invoke(PemjaConfig config, Object... input) {
    String uniqueKey = config.getClassName() + "_" + Md5Utils.md5Of(UUID.randomUUID().toString());
    PythonInterpreter interpreter = null;
    try {
      interpreter = getPythonInterpreter(config, uniqueKey);
      return interpreter.invokeMethod(uniqueKey, config.getMethod(), input);
    } finally {
      if (interpreter != null) {
        interpreter.close();
      }
    }
  }

  private static PythonInterpreter getPythonInterpreter(PemjaConfig config, String uniqueKey) {
    PythonInterpreter interpreter =
        newPythonInterpreter(config.getPythonExec(), config.getPythonPaths());
    if (config.getProjectId() != null) {
      interpreter.exec("from kag.bridge.spg_server_bridge import init_kag_config");
      interpreter.exec(
          String.format(
              "init_kag_config(\"%s\",\"%s\")", config.getProjectId(), config.getHostAddr()));
    }
    interpreter.exec(
        String.format("from %s import %s", config.getModulePath(), config.getClassName()));
    interpreter.exec(
        String.format(
            "%s=%s(%s)",
            uniqueKey,
            config.getClassName(),
            paramToPythonString(config.getParams(), config.getParamsPrefix())));
    return interpreter;
  }

  public static PythonInterpreter newPythonInterpreter(String pythonExec, String pythonPaths) {
    PythonInterpreterConfig.PythonInterpreterConfigBuilder builder =
        PythonInterpreterConfig.newBuilder();
    if (StringUtils.isNotBlank(pythonExec)) {
      builder.setPythonExec(pythonExec);
    }
    if (StringUtils.isNotBlank(pythonPaths)) {
      String[] pythonPathList = pythonPaths.split(";");
      builder.addPythonPaths(pythonPathList);
    }
    return new PythonInterpreter(builder.build());
  }

  private static String paramToPythonString(Map<String, String> params, String paramsPrefix) {
    if (MapUtils.isEmpty(params)) {
      return "";
    }
    if (StringUtils.isBlank(paramsPrefix)) {
      paramsPrefix = "**";
    }
    String keyValue =
        params.entrySet().stream()
            .map(entry -> String.format("'%s': '%s'", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(","));
    return String.format("%s{%s}", paramsPrefix, keyValue);
  }
}
