package com.antgroup.openspgapp.core.reasoner.service.impl;

import com.antgroup.openspgapp.core.reasoner.model.Session;
import com.antgroup.openspgapp.core.reasoner.service.SessionService;
import com.antgroup.openspgapp.core.reasoner.service.repository.ReasonSessionRepository;
import com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;

@Service
/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/SessionServiceImpl.class */
public class SessionServiceImpl implements SessionService {

  @Autowired private ReasonSessionRepository reasonSessionRepository;

  @Autowired private ReasonTaskRepository reasonTaskRepository;

  @Override // com.antgroup.openspgapp.core.reasoner.service.SessionService
  public Session create(Session session) {
    int cnt = this.reasonSessionRepository.create(session);
    if (cnt <= 0) {
      throw new RuntimeException("create session error");
    }
    return session;
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.SessionService
  public Session query(Long id) {
    return this.reasonSessionRepository.query(id);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.SessionService
  public Integer update(Session session) {
    return Integer.valueOf(this.reasonSessionRepository.update(session));
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.SessionService
  public Integer delete(Session session) {
    this.reasonTaskRepository.deleteTaskInSession(session.getId());
    return Integer.valueOf(this.reasonSessionRepository.delete(session.getId()));
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.SessionService
  public Tuple2<List<Session>, Long> getSessionList(
      Long projectId, Long userId, Integer start, Integer limit) {
    Session session = new Session((Long) null, projectId, userId, (String) null, (String) null);
    return this.reasonSessionRepository.querySessionList(
        session, start.intValue(), limit.intValue());
  }
}
