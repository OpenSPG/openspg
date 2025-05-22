package com.antgroup.openspgapp.biz.builder.impl;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.bulider.BuilderJobQuery;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspgapp.biz.builder.BuilderJobManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: biz-builder-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/builder/impl/BuilderJobManagerImpl.class */
public class BuilderJobManagerImpl implements BuilderJobManager {

  @Autowired private BuilderJobService builderJobService;

  @Override // com.antgroup.openspgapp.biz.builder.BuilderJobManager
  public BuilderJob queryById(Long id) {
    return this.builderJobService.getById(id);
  }

  @Override // com.antgroup.openspgapp.biz.builder.BuilderJobManager
  public Paged<BuilderJob> query(
      Long projectId, String createUser, String keyword, Integer start, Integer limit) {
    BuilderJobQuery record = new BuilderJobQuery();
    record.setProjectId(projectId);
    record.setCreateUser(createUser);
    record.setKeyword(keyword);
    record.setPageNo(start);
    record.setPageSize(limit);
    record.setSort("gmtModified");
    record.setOrder("desc");
    return this.builderJobService.query(record);
  }

  @Override // com.antgroup.openspgapp.biz.builder.BuilderJobManager
  public BuilderJob submit(BuilderJob job) {
    Long id = this.builderJobService.insert(job);
    job.setId(id);
    return job;
  }

  @Override // com.antgroup.openspgapp.biz.builder.BuilderJobManager
  public Long update(BuilderJob job) {
    return this.builderJobService.update(job);
  }

  @Override // com.antgroup.openspgapp.biz.builder.BuilderJobManager
  public int delete(Long id) {
    return this.builderJobService.deleteById(id);
  }
}
