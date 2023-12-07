package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.builder.model.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;

/**
 * 知识构建runner接口，具体实现有flink runner，local runner或者spark runner等
 * 他们负责接收构建的pipeline以及运行时上下文，然后启动任务进行知识构建流程
 */
public interface BuilderRunner {

  /** runner的初始化，当初始化异常时则直接抛出异常不再进行构建流程 */
  void init(Pipeline pipeline, RuntimeContext context) throws BuilderException;

  /** 开始执行runner，即开始知识构建流程，会按照pipeline的定义在具体执行引擎上执行知识构建 */
  void execute();

  /** 关闭runner，执行一些资源的清理工作 */
  void close() throws Exception;
}
