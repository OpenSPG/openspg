package com.antgroup.openspgapp.biz.reasoner.impl;

import com.antgroup.openspgapp.biz.reasoner.TutorialManager;
import com.antgroup.openspgapp.core.reasoner.model.Tutorial;
import com.antgroup.openspgapp.core.reasoner.service.TutorialService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/impl/TutorialManagerImpl.class */
public class TutorialManagerImpl implements TutorialManager {

  @Autowired private TutorialService tutorialService;

  @Override // com.antgroup.openspgapp.biz.reasoner.TutorialManager
  public List<Tutorial> queryTutorials(Long projectId, String keyword) {
    return this.tutorialService.queryTutorials(projectId, keyword, true);
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TutorialManager
  public void create(Tutorial tutorial) {
    int cnt = this.tutorialService.create(tutorial);
    if (cnt < 0) {
      throw new RuntimeException("create tutorial error");
    }
  }

  @Override // com.antgroup.openspgapp.biz.reasoner.TutorialManager
  public void update(Tutorial tutorial) {
    int cnt = this.tutorialService.update(tutorial);
    if (cnt < 0) {
      throw new RuntimeException("create tutorial error, id=" + tutorial.getId());
    }
  }
}
