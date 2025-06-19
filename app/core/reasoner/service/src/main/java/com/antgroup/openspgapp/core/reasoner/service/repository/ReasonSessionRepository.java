package com.antgroup.openspgapp.core.reasoner.service.repository;

import com.antgroup.openspgapp.core.reasoner.model.Session;
import java.util.List;
import scala.Tuple2;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/repository/ReasonSessionRepository.class */
public interface ReasonSessionRepository {
  int create(Session session);

  Session query(Long id);

  int update(Session session);

  int delete(Long id);

  Tuple2<List<Session>, Long> querySessionList(Session session, int start, int limit);
}
