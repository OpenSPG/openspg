package com.antgroup.openspgapp.core.reasoner.service.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.common.service.builder.BuilderJobRepository;
import com.antgroup.openspg.server.core.reasoner.service.CatalogService;
import com.antgroup.openspgapp.core.reasoner.model.task.MarkEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.service.TaskService;
import com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository;
import com.antgroup.openspgapp.core.reasoner.service.utils.ReasonerValue;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskServiceImpl.class */
public class TaskServiceImpl implements TaskService {
  private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

  @Autowired private ReasonTaskRepository reasonTaskRepository;

  @Autowired private BuilderJobRepository builderJobRepository;

  @Autowired private ReasonerValue reasonerValue;

  @Autowired private ProjectManager projectManager;

  @Autowired private CatalogService catalogService;
  private TaskRunner taskRunner;

  @PostConstruct
  public void init() {
    this.taskRunner = new TaskRunner(this.reasonerValue, this.projectManager, this.catalogService);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public Task query(Long id) {
    return this.reasonTaskRepository.query(id);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public int update(Task task) {
    return this.reasonTaskRepository.update(task);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public Task create(Task task) {
    Task newTask = this.reasonTaskRepository.create(task);
    return runTask(newTask.getId());
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public Task dialog(Task task) {
    Task newTask = this.reasonTaskRepository.create(task);
    newTask.setExtend(task.getExtend());
    return runDialog(newTask);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public Task nlQuery(Task task) {
    Task newTask = this.reasonTaskRepository.create(task);
    return runNlQuery(newTask);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public void stop(Long id) {
    Task updateTask = new Task();
    updateTask.setId(id);
    updateTask.setStatus(StatusEnum.CANCELED);
    this.reasonTaskRepository.update(updateTask);
    this.taskRunner.cancel(id);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public void mark(Long id, MarkEnum markEnum) {
    Task task = new Task();
    task.setId(id);
    task.setMark(markEnum);
    int cnt = this.reasonTaskRepository.update(task);
    if (cnt < 0) {
      throw new RuntimeException("can not found task by id " + id);
    }
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TaskService
  public List<Task> queryTasks(
      Long projectId,
      Long userId,
      Long sessionId,
      String mark,
      String keyword,
      Long startTaskId,
      Integer limit) {
    return this.reasonTaskRepository.queryTasks(
        projectId, userId, sessionId, mark, keyword, startTaskId, limit);
  }

  private Task runTask(Long id) {
    int cnt = this.reasonTaskRepository.updateStatusToRunning(id);
    if (cnt > 0) {
      Task task = this.reasonTaskRepository.query(id);
      log.info("start_run_reasoner_task,task=" + JSON.toJSONString(task));
      this.taskRunner.run(
          task, new TaskCallBack(this.reasonTaskRepository, this.builderJobRepository));
      return task;
    }
    return null;
  }

  private Task runDialog(Task task) {
    int cnt = this.reasonTaskRepository.updateStatusToRunning(task.getId());
    if (cnt > 0) {
      log.info("start_run_dialog_task,task=" + JSON.toJSONString(task));
      this.taskRunner.dialog(
          task, new TaskCallBack(this.reasonTaskRepository, this.builderJobRepository));
      return task;
    }
    return null;
  }

  private Task runNlQuery(Task task) {
    int cnt = this.reasonTaskRepository.updateStatusToRunning(task.getId());
    if (cnt > 0) {
      log.info("start_run_nlQuery_task,task=" + JSON.toJSONString(task));
      this.taskRunner.nlQuery(
          task, new TaskCallBack(this.reasonTaskRepository, this.builderJobRepository));
      return task;
    }
    return null;
  }
}
