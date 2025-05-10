package com.antgroup.openspgapp.core.reasoner.service.impl;

import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskRuntime.class */
public class TaskRuntime {
  private Long id;
  private Future<Task> future;
  private Function<Task, Integer> callback;
  private Map<String, ExecuteNode> nodes;

  public TaskRuntime(
      Long id,
      Map<String, ExecuteNode> nodes,
      Future<Task> future,
      Function<Task, Integer> callback) {
    this.id = id;
    this.future = future;
    this.callback = callback;
    this.nodes = nodes;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Future<Task> getFuture() {
    return this.future;
  }

  public void setFuture(Future<Task> future) {
    this.future = future;
  }

  public Function<Task, Integer> getCallback() {
    return this.callback;
  }

  public void setCallback(Function<Task, Integer> callback) {
    this.callback = callback;
  }

  public Map<String, ExecuteNode> getNodes() {
    return this.nodes;
  }

  public void setNodes(Map<String, ExecuteNode> nodes) {
    this.nodes = nodes;
  }
}
