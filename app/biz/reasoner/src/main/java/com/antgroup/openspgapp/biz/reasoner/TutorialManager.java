package com.antgroup.openspgapp.biz.reasoner;

import com.antgroup.openspgapp.core.reasoner.model.Tutorial;
import java.util.List;

/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/TutorialManager.class */
public interface TutorialManager {
  List<Tutorial> queryTutorials(Long projectId, String keyword);

  void create(Tutorial tutorial);

  void update(Tutorial tutorial);
}
