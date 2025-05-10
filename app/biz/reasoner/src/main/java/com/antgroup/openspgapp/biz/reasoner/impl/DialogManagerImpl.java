package com.antgroup.openspgapp.biz.reasoner.impl;

import com.antgroup.openspgapp.biz.reasoner.DialogManager;
import com.antgroup.openspgapp.core.reasoner.model.Session;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.service.SessionService;
import com.antgroup.openspgapp.core.reasoner.service.TaskService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/impl/DialogManagerImpl.class */
public class DialogManagerImpl implements DialogManager {

  @Autowired private TaskService taskService;

  @Autowired private SessionService sessionService;

  @Override // com.antgroup.openspgapp.biz.reasoner.DialogManager
  public Task submit(
      Long sessionId, String dsl, String nl, String resultMessage, Map<String, String> params) {
    Session session = this.sessionService.query(sessionId);
    Task task = new Task();
    task.setProjectId(session.getProjectId());
    task.setUserId(session.getUserId());
    task.setSessionId(session.getId());
    task.setDsl(dsl);
    task.setNl(nl);
    task.setParams(params);
    task.setStatus(StatusEnum.INIT);
    task.setResultMessage(resultMessage);
    return this.taskService.nlQuery(task);
  }
}
