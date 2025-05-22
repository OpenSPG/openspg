package com.antgroup.openspgapp.biz.reasoner.impl;

import com.antgroup.openspgapp.biz.reasoner.SessionManager;
import com.antgroup.openspgapp.core.reasoner.model.Session;
import com.antgroup.openspgapp.core.reasoner.service.SessionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/impl/SessionManagerImpl.class */
public class SessionManagerImpl implements SessionManager {

  @Autowired private SessionService sessionService;

  @Override // com.antgroup.openspgapp.biz.reasoner.SessionManager
  public Session createSession(Session session) {
    return this.sessionService.create(session);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.SessionManager
  public Integer updateSession(Session session) {
    return this.sessionService.update(session);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.SessionManager
  public Integer deleteSession(Session session) {
    return this.sessionService.delete(session);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.SessionManager
  public List<Session> queryRecentSession(Long projectId, Long userId, Integer limit) {
    return (List) this.sessionService.getSessionList(projectId, userId, 0, limit)._1();
  }
}
