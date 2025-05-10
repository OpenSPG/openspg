package com.antgroup.openspgapp.biz.builder;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;

/* loaded from: biz-builder-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/builder/BuilderJobManager.class */
public interface BuilderJobManager {
  BuilderJob queryById(Long id);

  Paged<BuilderJob> query(
      Long projectId, String createUser, String keyword, Integer start, Integer limit);

  BuilderJob submit(BuilderJob job);

  Long update(BuilderJob task);

  int delete(Long id);
}
