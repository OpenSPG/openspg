package com.antgroup.openspgapp.core.reasoner.service;

import com.antgroup.openspgapp.core.reasoner.model.Session;
import java.util.List;
import scala.Tuple2;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/SessionService.class */
public interface SessionService {
  Session create(Session session);

  Session query(Long id);

  Integer update(Session session);

  Integer delete(Session session);

  Tuple2<List<Session>, Long> getSessionList(
      Long projectId, Long userId, Integer start, Integer limit);
}
