package com.antgroup.openspgapp.biz.reasoner.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspgapp.biz.reasoner.TaskManager;
import com.antgroup.openspgapp.core.reasoner.model.Session;
import com.antgroup.openspgapp.core.reasoner.model.task.MarkEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.service.SessionService;
import com.antgroup.openspgapp.core.reasoner.service.TaskService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/impl/TaskManagerImpl.class */
public class TaskManagerImpl implements TaskManager {

  @Autowired private TaskService taskService;

  @Autowired private SessionService sessionService;

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public Task query(Long id) {
    return this.taskService.query(id);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public int update(Task task) {
    return this.taskService.update(task);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public Task submit(
      Long sessionId, Long userId, String dsl, String nl, Map<String, String> params) {
    Session session = this.sessionService.query(sessionId);
    Task task = new Task();
    task.setProjectId(session.getProjectId());
    task.setUserId(session.getUserId());
    task.setSessionId(session.getId());
    task.setDsl(dsl);
    task.setNl(nl);
    task.setParams(params);
    task.setStatus(StatusEnum.INIT);
    return this.taskService.create(task);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public Task dialog(
      Long sessionId,
      Long projectId,
      Long userId,
      String dsl,
      String nl,
      Map<String, String> params) {
    Task task = new Task();
    task.setProjectId(projectId);
    task.setUserId(userId);
    task.setSessionId(sessionId);
    task.setDsl(dsl);
    task.setNl(nl);
    task.setParams(params);
    String pipelineStr = (String) task.getParams().get("pipeline");
    task.setExtend(JSON.parseObject(pipelineStr, Pipeline.class));
    task.setStatus(StatusEnum.INIT);
    return this.taskService.dialog(task);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public void stop(Long id) {
    this.taskService.stop(id);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public void mark(Long id) {
    this.taskService.mark(id, MarkEnum.MARKED);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public void unMark(Long id) {
    this.taskService.mark(id, MarkEnum.NULL);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TaskManager
  public List<Task> queryTasks(
      Long projectId,
      Long userId,
      Long sessionId,
      String mark,
      String keyword,
      Long startTaskId,
      Integer limit) {
    return this.taskService.queryTasks(
        projectId, userId, sessionId, mark, keyword, startTaskId, limit);
  }
}
