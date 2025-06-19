package com.antgroup.openspgapp.core.reasoner.service.repository;

import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/repository/ReasonTaskRepository.class */
public interface ReasonTaskRepository {
  long count(Long sessionId, Long userId, String mark);

  Task query(Long id);

  List<Task> queryTasks(
      Long projectId,
      Long userId,
      Long sessionId,
      String mark,
      String keyword,
      Long startTaskId,
      Integer limit);

  Task create(Task task);

  int update(Task task);

  int deleteTaskInSession(Long sessionId);

  int updateStatusToRunning(Long id);

  int updateStatusToExtracting(Long id);
}
