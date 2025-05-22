package com.antgroup.openspgapp.biz.reasoner;

import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import java.util.List;
import java.util.Map;

/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/TaskManager.class */
public interface TaskManager {
  Task query(Long id);

  int update(Task task);

  Task submit(Long sessionId, Long userId, String dsl, String nl, Map<String, String> params);

  Task dialog(
      Long sessionId,
      Long projectId,
      Long userId,
      String dsl,
      String nl,
      Map<String, String> params);

  void stop(Long id);

  void mark(Long id);

  void unMark(Long id);

  List<Task> queryTasks(
      Long projectId,
      Long userId,
      Long sessionId,
      String mark,
      String keyword,
      Long startTaskId,
      Integer limit);
}
