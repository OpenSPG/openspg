package com.antgroup.openspgapp.core.reasoner.service.repository.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspgapp.core.reasoner.model.task.MarkEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Path;
import com.antgroup.openspgapp.core.reasoner.model.task.result.TableResult;
import com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository;
import com.antgroup.openspgapp.core.reasoner.service.utils.Utils;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOWithBLOBs;
import com.antgroup.openspgapp.infra.dao.mapper.ReasonTaskDOMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/repository/impl/ReasonTaskRepositoryImpl.class */
public class ReasonTaskRepositoryImpl implements ReasonTaskRepository {

  @Autowired private ReasonTaskDOMapper reasonTaskDOMapper;

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public long count(Long sessionId, Long userId, String mark) {
    ReasonTaskDOExample example = new ReasonTaskDOExample();
    ReasonTaskDOExample.Criteria criteria = example.createCriteria();
    if (null != sessionId) {
      criteria.andSessionIdEqualTo(sessionId);
    }
    if (null != userId) {
      criteria.andUserIdEqualTo(userId);
    }
    if (null != mark) {
      criteria.andMarkEqualTo(mark);
    }
    return this.reasonTaskDOMapper.countByExample(example);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public Task query(Long id) {
    return convert(this.reasonTaskDOMapper.selectByPrimaryKey(id));
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public List<Task> queryTasks(
      Long projectId,
      Long userId,
      Long sessionId,
      String mark,
      String keyword,
      Long startTaskId,
      Integer limit) {
    ReasonTaskDOExample example = new ReasonTaskDOExample();
    if (StringUtils.isEmpty(keyword)) {
      addTaskQuery(example, projectId, userId, sessionId, mark, startTaskId);
    } else {
      ReasonTaskDOExample.Criteria criteria1 =
          addTaskQuery(example, projectId, userId, sessionId, mark, startTaskId);
      criteria1.andDslLike(keyword);
      ReasonTaskDOExample.Criteria criteria2 =
          addTaskQuery(example, projectId, userId, sessionId, mark, startTaskId);
      criteria2.andNlLike(keyword);
    }
    example.setOrderByClause("id DESC limit " + limit);
    List<ReasonTaskDOWithBLOBs> reasonTaskDOWithBLOBsList =
        this.reasonTaskDOMapper.selectByExampleWithBLOBs(example);
    return (List)
        reasonTaskDOWithBLOBsList.stream()
            .map(ReasonTaskRepositoryImpl::convert)
            .collect(Collectors.toList());
  }

  private ReasonTaskDOExample.Criteria addTaskQuery(
      ReasonTaskDOExample example,
      Long projectId,
      Long userId,
      Long sessionId,
      String mark,
      Long startTaskId) {
    ReasonTaskDOExample.Criteria criteria = example.or();
    if (null != projectId) {
      criteria.andProjectIdEqualTo(projectId);
    }
    if (null != sessionId) {
      criteria.andSessionIdEqualTo(sessionId);
    }
    if (null != userId) {
      criteria.andUserIdEqualTo(userId);
    }
    if (null != mark) {
      criteria.andMarkEqualTo(mark);
    }
    if (null != startTaskId && startTaskId.longValue() > 0) {
      criteria.andIdLessThan(startTaskId);
    }
    return criteria;
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public Task create(Task task) {
    ReasonTaskDOWithBLOBs reasonTaskDOWithBLOBs = convert(task);
    int cnt = this.reasonTaskDOMapper.insertSelective(reasonTaskDOWithBLOBs);
    if (cnt < 0) {
      throw new RuntimeException("create task error");
    }
    return convert(reasonTaskDOWithBLOBs);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public int update(Task task) {
    ReasonTaskDOWithBLOBs convert = convert(task);
    convert.setGmtModified(new Date());
    return this.reasonTaskDOMapper.updateByPrimaryKeySelective(convert(task));
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public int deleteTaskInSession(Long sessionId) {
    ReasonTaskDOExample example = new ReasonTaskDOExample();
    ReasonTaskDOExample.Criteria criteria = example.createCriteria();
    criteria.andSessionIdEqualTo(sessionId);
    return this.reasonTaskDOMapper.deleteByExample(example);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public int updateStatusToRunning(Long id) {
    ReasonTaskDOExample example = new ReasonTaskDOExample();
    ReasonTaskDOExample.Criteria criteria = example.createCriteria();
    criteria.andIdEqualTo(id);
    criteria.andStatusEqualTo(StatusEnum.INIT.name());
    ReasonTaskDOWithBLOBs record = new ReasonTaskDOWithBLOBs();
    record.setStatus(StatusEnum.RUNNING.name());
    return this.reasonTaskDOMapper.updateByExampleSelective(record, example);
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository
  public int updateStatusToExtracting(Long id) {
    ReasonTaskDOExample example = new ReasonTaskDOExample();
    ReasonTaskDOExample.Criteria criteria = example.createCriteria();
    criteria.andIdEqualTo(id);
    criteria.andStatusEqualTo(StatusEnum.RUNNING.name());
    ReasonTaskDOWithBLOBs record = new ReasonTaskDOWithBLOBs();
    record.setStatus(StatusEnum.EXTRACTING.name());
    return this.reasonTaskDOMapper.updateByExampleSelective(record, example);
  }

  private static Task convert(ReasonTaskDOWithBLOBs reasonTaskDOWithBLOBs) {
    if (reasonTaskDOWithBLOBs == null) {
      return null;
    }
    Task task = new Task();
    task.setId(reasonTaskDOWithBLOBs.getId());
    task.setProjectId(reasonTaskDOWithBLOBs.getProjectId());
    task.setUserId(reasonTaskDOWithBLOBs.getUserId());
    task.setSessionId(reasonTaskDOWithBLOBs.getSessionId());
    task.setDsl(reasonTaskDOWithBLOBs.getDsl());
    task.setNl(reasonTaskDOWithBLOBs.getNl());
    task.setGmtModified(reasonTaskDOWithBLOBs.getGmtModified());
    if (StringUtils.isNotEmpty(reasonTaskDOWithBLOBs.getParams())) {
      task.setParams(Utils.parseParams(reasonTaskDOWithBLOBs.getParams()));
    }
    if (StringUtils.isNotEmpty(reasonTaskDOWithBLOBs.getMark())) {
      task.setMark(MarkEnum.valueOf(reasonTaskDOWithBLOBs.getMark()));
    }
    if (StringUtils.isNotEmpty(reasonTaskDOWithBLOBs.getStatus())) {
      if (StatusEnum.RUNNING.name().equals(reasonTaskDOWithBLOBs.getStatus())
          && Math.abs(DateDiff(reasonTaskDOWithBLOBs.getGmtModified(), new Date()).toDays()) > 1) {
        task.setStatus(StatusEnum.TIMEOUT);
      } else {
        task.setStatus(StatusEnum.valueOf(reasonTaskDOWithBLOBs.getStatus()));
      }
    }
    task.setResultMessage(reasonTaskDOWithBLOBs.getResultMessage());
    if (StringUtils.isNotEmpty(reasonTaskDOWithBLOBs.getResultTable())) {
      task.setResultTable(
          (TableResult)
              JSON.parseObject(reasonTaskDOWithBLOBs.getResultTable(), TableResult.class));
    }
    if (StringUtils.isNotEmpty(reasonTaskDOWithBLOBs.getResultNodes())) {
      task.setResultNodes(JSON.parseArray(reasonTaskDOWithBLOBs.getResultNodes(), Node.class));
    }
    if (StringUtils.isNotEmpty(reasonTaskDOWithBLOBs.getResultEdges())) {
      task.setResultEdges(JSON.parseArray(reasonTaskDOWithBLOBs.getResultEdges(), Edge.class));
    }
    if (StringUtils.isNotEmpty(reasonTaskDOWithBLOBs.getResultPaths())) {
      task.setResultPaths(JSON.parseArray(reasonTaskDOWithBLOBs.getResultPaths(), Path.class));
    }
    return task;
  }

  private static Duration DateDiff(Date date1, Date date2) {
    Instant instant1 = date1.toInstant();
    Instant instant2 = date2.toInstant();
    return Duration.between(instant1, instant2);
  }

  private static ReasonTaskDOWithBLOBs convert(Task task) {
    ReasonTaskDOWithBLOBs reasonTaskDOWithBLOBs = new ReasonTaskDOWithBLOBs();
    reasonTaskDOWithBLOBs.setId(task.getId());
    reasonTaskDOWithBLOBs.setProjectId(task.getProjectId());
    reasonTaskDOWithBLOBs.setUserId(task.getUserId());
    reasonTaskDOWithBLOBs.setSessionId(task.getSessionId());
    reasonTaskDOWithBLOBs.setDsl(task.getDsl());
    reasonTaskDOWithBLOBs.setNl(task.getNl());
    if (null != task.getParams()) {
      reasonTaskDOWithBLOBs.setParams(JSON.toJSONString(task.getParams()));
    }
    if (null != task.getMark()) {
      reasonTaskDOWithBLOBs.setMark(task.getMark().name());
    }
    if (null != task.getStatus()) {
      reasonTaskDOWithBLOBs.setStatus(task.getStatus().name());
    }
    reasonTaskDOWithBLOBs.setResultMessage(task.getResultMessage());
    if (null != task.getResultTable()) {
      reasonTaskDOWithBLOBs.setResultTable(JSON.toJSONString(task.getResultTable()));
    }
    if (null != task.getResultNodes()) {
      reasonTaskDOWithBLOBs.setResultNodes(JSON.toJSONString(task.getResultNodes()));
    }
    if (null != task.getResultEdges()) {
      reasonTaskDOWithBLOBs.setResultEdges(JSON.toJSONString(task.getResultEdges()));
    }
    if (null != task.getResultPaths()) {
      reasonTaskDOWithBLOBs.setResultPaths(JSON.toJSONString(task.getResultPaths()));
    }
    return reasonTaskDOWithBLOBs;
  }
}
