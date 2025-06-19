package com.antgroup.openspgapp.biz.reasoner;

import com.antgroup.openspgapp.core.reasoner.model.Session;
import java.util.List;

/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/SessionManager.class */
public interface SessionManager {
  Session createSession(Session session);

  Integer updateSession(Session session);

  Integer deleteSession(Session session);

  List<Session> queryRecentSession(Long projectId, Long userId, Integer limit);
}
