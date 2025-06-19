package com.antgroup.openspgapp.core.reasoner.service.repository.impl;

import com.antgroup.openspgapp.core.reasoner.model.Session;
import com.antgroup.openspgapp.core.reasoner.service.repository.ReasonSessionRepository;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonSessionDO;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonSessionDOExample;
import com.antgroup.openspgapp.infra.dao.mapper.ReasonSessionDOMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import scala.Tuple2;

@Repository
/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/repository/impl/ReasonSessionRepositoryImpl.class */
public class ReasonSessionRepositoryImpl implements ReasonSessionRepository {

  @Autowired private ReasonSessionDOMapper reasonSessionDOMapper;

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonSessionRepository
  public int create(Session session) {
    ReasonSessionDO reasonSessionDO = convert(session);
    int cnt = this.reasonSessionDOMapper.insertSelective(reasonSessionDO);
    session.setId(reasonSessionDO.getId());
    return cnt;
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonSessionRepository
  public Session query(Long id) {
    return convert(this.reasonSessionDOMapper.selectByPrimaryKey(id));
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonSessionRepository
  public int update(Session session) {
    return this.reasonSessionDOMapper.updateByPrimaryKeySelective(convert(session));
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonSessionRepository
  public int delete(Long id) {
    return this.reasonSessionDOMapper.deleteByPrimaryKey(id);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonSessionRepository
  public Tuple2<List<Session>, Long> querySessionList(Session session, int start, int limit) {
    PageHelper.offsetPage(start, limit);
    ReasonSessionDOExample example = new ReasonSessionDOExample();
    ReasonSessionDOExample.Criteria criteria = example.createCriteria();
    if (null != session.getId()) {
      criteria.andIdEqualTo(session.getId());
    }
    if (null != session.getProjectId()) {
      criteria.andProjectIdEqualTo(session.getProjectId());
    }
    if (null != session.getUserId()) {
      criteria.andUserIdEqualTo(session.getUserId());
    }
    example.setOrderByClause("id DESC");
    Page<ReasonSessionDO> listWithPage =
        this.reasonSessionDOMapper.selectByExampleWithBLOBs(example);
    List<Session> sessionList =
        (List) listWithPage.stream().map(this::convert).collect(Collectors.toList());
    return new Tuple2<>(sessionList, Long.valueOf(listWithPage.getTotal()));
  }

  private static ReasonSessionDO convert(Session session) {
    ReasonSessionDO reasonSessionDO = new ReasonSessionDO();
    reasonSessionDO.setId(session.getId());
    reasonSessionDO.setProjectId(session.getProjectId());
    reasonSessionDO.setUserId(session.getUserId());
    reasonSessionDO.setName(session.getName());
    reasonSessionDO.setDescription(session.getDescription());
    return reasonSessionDO;
  }

  private Session convert(ReasonSessionDO sessionDO) {
    return new Session(
        sessionDO.getId(),
        sessionDO.getProjectId(),
        sessionDO.getUserId(),
        sessionDO.getName(),
        sessionDO.getDescription());
  }
}
