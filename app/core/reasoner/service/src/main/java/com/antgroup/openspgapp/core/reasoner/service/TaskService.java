package com.antgroup.openspgapp.core.reasoner.service;

import com.antgroup.openspgapp.core.reasoner.model.task.MarkEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/TaskService.class */
public interface TaskService {
  Task query(Long id);

  int update(Task task);

  Task create(Task task);

  Task dialog(Task task);

  Task nlQuery(Task task);

  void stop(Long id);

  void mark(Long id, MarkEnum markEnum);

  List<Task> queryTasks(
      Long projectId,
      Long userId,
      Long sessionId,
      String mark,
      String keyword,
      Long startTaskId,
      Integer limit);
}
