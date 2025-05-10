package com.antgroup.openspgapp.core.reasoner.service;

import com.antgroup.openspgapp.core.reasoner.model.Tutorial;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/TutorialService.class */
public interface TutorialService {
  List<Tutorial> queryTutorials(Long projectId, String keyword, Boolean enable);

  int create(Tutorial tutorial);

  int update(Tutorial tutorial);
}
