package com.antgroup.openspg.common.util.pemja.model;

import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class PemjaConfig {

  private String pythonExec;

  private String pythonPaths;

  private String modulePath;

  private String className;

  private String method;

  private Long projectId;

  private String hostAddr;

  private Map<String, String> params;

  private String paramsPrefix;

  public PemjaConfig(
      String pythonExec,
      String pythonPaths,
      String hostAddr,
      Long projectId,
      String modulePath,
      String className,
      String method,
      Map<String, String> params,
      String paramsPrefix) {
    this.pythonExec = pythonExec;
    this.pythonPaths = pythonPaths;
    this.modulePath = modulePath;
    this.className = className;
    this.method = method;
    this.params = params;
    this.paramsPrefix = paramsPrefix;
    this.projectId = projectId;
    this.hostAddr = hostAddr;
  }

  public PemjaConfig(
      String pythonExec,
      String pythonPaths,
      String hostAddr,
      Long projectId,
      PythonInvokeMethod pythonInvoke,
      Map<String, String> params) {
    this(
        pythonExec,
        pythonPaths,
        hostAddr,
        projectId,
        pythonInvoke.getModulePath(),
        pythonInvoke.getClassName(),
        pythonInvoke.getMethod(),
        params,
        pythonInvoke.getParamsPrefix());
  }
}
