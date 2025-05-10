package com.antgroup.openspgapp.core.reasoner.service.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspgapp.core.reasoner.model.Tutorial;
import com.antgroup.openspgapp.core.reasoner.service.TutorialService;
import com.antgroup.openspgapp.core.reasoner.service.utils.Utils;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOWithBLOBs;
import com.antgroup.openspgapp.infra.dao.mapper.ReasonTutorialDOMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TutorialServiceImpl.class */
public class TutorialServiceImpl implements TutorialService {

  @Autowired private ReasonTutorialDOMapper reasonTutorialDOMapper;

  @Override // com.antgroup.openspgapp.core.reasoner.service.TutorialService
  public List<Tutorial> queryTutorials(Long projectId, String keyword, Boolean enable) {
    ReasonTutorialDOExample example = new ReasonTutorialDOExample();
    if (StringUtils.isEmpty(keyword)) {
      queryCriteriaWithoutKeyword(example, projectId, enable);
    } else {
      ReasonTutorialDOExample.Criteria criteria1 =
          queryCriteriaWithoutKeyword(example, projectId, enable);
      criteria1.andDslLike(keyword);
      ReasonTutorialDOExample.Criteria criteria2 =
          queryCriteriaWithoutKeyword(example, projectId, enable);
      criteria2.andNlLike(keyword);
      ReasonTutorialDOExample.Criteria criteria3 =
          queryCriteriaWithoutKeyword(example, projectId, enable);
      criteria3.andNameLike("%" + keyword + "%");
    }
    List<ReasonTutorialDOWithBLOBs> tutorialList =
        this.reasonTutorialDOMapper.selectByExampleWithBLOBs(example);
    return (List) tutorialList.stream().map(this::convert).collect(Collectors.toList());
  }

  private ReasonTutorialDOExample.Criteria queryCriteriaWithoutKeyword(
      ReasonTutorialDOExample example, Long projectId, Boolean enable) {
    ReasonTutorialDOExample.Criteria criteria = example.or();
    criteria.andProjectIdEqualTo(projectId);
    if (null != enable) {
      criteria.andEnableEqualTo(enable);
    }
    return criteria;
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TutorialService
  public int create(Tutorial tutorial) {
    return this.reasonTutorialDOMapper.insertSelective(convert(tutorial));
  }

  @Override // com.antgroup.openspgapp.core.reasoner.service.TutorialService
  public int update(Tutorial tutorial) {
    return this.reasonTutorialDOMapper.updateByPrimaryKeySelective(convert(tutorial));
  }

  private ReasonTutorialDOWithBLOBs convert(Tutorial tutorial) {
    ReasonTutorialDOWithBLOBs reasonTutorialDOWithBLOBs = new ReasonTutorialDOWithBLOBs();
    reasonTutorialDOWithBLOBs.setId(tutorial.getId());
    reasonTutorialDOWithBLOBs.setProjectId(tutorial.getProjectId());
    reasonTutorialDOWithBLOBs.setEnable(tutorial.getEnable());
    reasonTutorialDOWithBLOBs.setName(tutorial.getName());
    reasonTutorialDOWithBLOBs.setDsl(tutorial.getDsl());
    reasonTutorialDOWithBLOBs.setNl(tutorial.getNl());
    if (MapUtils.isNotEmpty(tutorial.getParams())) {
      reasonTutorialDOWithBLOBs.setParams(JSON.toJSONString(tutorial.getParams()));
    }
    reasonTutorialDOWithBLOBs.setDescription(tutorial.getDescription());
    return reasonTutorialDOWithBLOBs;
  }

  private Tutorial convert(ReasonTutorialDOWithBLOBs reasonTutorialDOWithBLOBs) {
    Tutorial tutorial = new Tutorial();
    tutorial.setId(reasonTutorialDOWithBLOBs.getId());
    tutorial.setProjectId(reasonTutorialDOWithBLOBs.getProjectId());
    tutorial.setEnable(reasonTutorialDOWithBLOBs.getEnable());
    tutorial.setName(reasonTutorialDOWithBLOBs.getName());
    tutorial.setDsl(reasonTutorialDOWithBLOBs.getDsl());
    tutorial.setNl(reasonTutorialDOWithBLOBs.getNl());
    if (StringUtils.isNotEmpty(reasonTutorialDOWithBLOBs.getParams())) {
      tutorial.setParams(Utils.parseParams(reasonTutorialDOWithBLOBs.getParams()));
    }
    tutorial.setDescription(reasonTutorialDOWithBLOBs.getDescription());
    return tutorial;
  }
}
