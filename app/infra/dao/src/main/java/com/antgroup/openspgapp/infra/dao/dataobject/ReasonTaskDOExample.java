package com.antgroup.openspgapp.infra.dao.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTaskDOExample.class */
public class ReasonTaskDOExample {
  protected String orderByClause;
  protected boolean distinct;
  protected List<Criteria> oredCriteria = new ArrayList();

  public void setOrderByClause(String orderByClause) {
    this.orderByClause = orderByClause;
  }

  public String getOrderByClause() {
    return this.orderByClause;
  }

  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }

  public boolean isDistinct() {
    return this.distinct;
  }

  public List<Criteria> getOredCriteria() {
    return this.oredCriteria;
  }

  public void or(Criteria criteria) {
    this.oredCriteria.add(criteria);
  }

  public Criteria or() {
    Criteria criteria = createCriteriaInternal();
    this.oredCriteria.add(criteria);
    return criteria;
  }

  public Criteria createCriteria() {
    Criteria criteria = createCriteriaInternal();
    if (this.oredCriteria.size() == 0) {
      this.oredCriteria.add(criteria);
    }
    return criteria;
  }

  protected Criteria createCriteriaInternal() {
    Criteria criteria = new Criteria();
    return criteria;
  }

  public void clear() {
    this.oredCriteria.clear();
    this.orderByClause = null;
    this.distinct = false;
  }

  /* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTaskDOExample$GeneratedCriteria.class */
  protected abstract static class GeneratedCriteria {
    protected List<Criterion> criteria = new ArrayList();

    protected GeneratedCriteria() {}

    public boolean isValid() {
      return this.criteria.size() > 0;
    }

    public List<Criterion> getAllCriteria() {
      return this.criteria;
    }

    public List<Criterion> getCriteria() {
      return this.criteria;
    }

    protected void addCriterion(String condition) {
      if (condition == null) {
        throw new RuntimeException("Value for condition cannot be null");
      }
      this.criteria.add(new Criterion(condition));
    }

    protected void addCriterion(String condition, Object value, String property) {
      if (value == null) {
        throw new RuntimeException("Value for " + property + " cannot be null");
      }
      this.criteria.add(new Criterion(condition, value));
    }

    protected void addCriterion(String condition, Object value1, Object value2, String property) {
      if (value1 == null || value2 == null) {
        throw new RuntimeException("Between values for " + property + " cannot be null");
      }
      this.criteria.add(new Criterion(condition, value1, value2));
    }

    public Criteria andIdIsNull() {
      addCriterion("id is null");
      return (Criteria) this;
    }

    public Criteria andIdIsNotNull() {
      addCriterion("id is not null");
      return (Criteria) this;
    }

    public Criteria andIdEqualTo(Long value) {
      addCriterion("id =", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdIn(List<Long> values) {
      addCriterion("id in", values, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotIn(List<Long> values) {
      addCriterion("id not in", values, "id");
      return (Criteria) this;
    }

    public Criteria andIdBetween(Long value1, Long value2) {
      addCriterion("id between", value1, value2, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotBetween(Long value1, Long value2) {
      addCriterion("id not between", value1, value2, "id");
      return (Criteria) this;
    }

    public Criteria andProjectIdIsNull() {
      addCriterion("project_id is null");
      return (Criteria) this;
    }

    public Criteria andProjectIdIsNotNull() {
      addCriterion("project_id is not null");
      return (Criteria) this;
    }

    public Criteria andProjectIdEqualTo(Long value) {
      addCriterion("project_id =", value, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdNotEqualTo(Long value) {
      addCriterion("project_id <>", value, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdGreaterThan(Long value) {
      addCriterion("project_id >", value, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdGreaterThanOrEqualTo(Long value) {
      addCriterion("project_id >=", value, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdLessThan(Long value) {
      addCriterion("project_id <", value, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdLessThanOrEqualTo(Long value) {
      addCriterion("project_id <=", value, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdIn(List<Long> values) {
      addCriterion("project_id in", values, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdNotIn(List<Long> values) {
      addCriterion("project_id not in", values, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdBetween(Long value1, Long value2) {
      addCriterion("project_id between", value1, value2, "projectId");
      return (Criteria) this;
    }

    public Criteria andProjectIdNotBetween(Long value1, Long value2) {
      addCriterion("project_id not between", value1, value2, "projectId");
      return (Criteria) this;
    }

    public Criteria andUserIdIsNull() {
      addCriterion("user_id is null");
      return (Criteria) this;
    }

    public Criteria andUserIdIsNotNull() {
      addCriterion("user_id is not null");
      return (Criteria) this;
    }

    public Criteria andUserIdEqualTo(Long value) {
      addCriterion("user_id =", value, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdNotEqualTo(Long value) {
      addCriterion("user_id <>", value, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdGreaterThan(Long value) {
      addCriterion("user_id >", value, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
      addCriterion("user_id >=", value, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdLessThan(Long value) {
      addCriterion("user_id <", value, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdLessThanOrEqualTo(Long value) {
      addCriterion("user_id <=", value, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdIn(List<Long> values) {
      addCriterion("user_id in", values, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdNotIn(List<Long> values) {
      addCriterion("user_id not in", values, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdBetween(Long value1, Long value2) {
      addCriterion("user_id between", value1, value2, "userId");
      return (Criteria) this;
    }

    public Criteria andUserIdNotBetween(Long value1, Long value2) {
      addCriterion("user_id not between", value1, value2, "userId");
      return (Criteria) this;
    }

    public Criteria andSessionIdIsNull() {
      addCriterion("session_id is null");
      return (Criteria) this;
    }

    public Criteria andSessionIdIsNotNull() {
      addCriterion("session_id is not null");
      return (Criteria) this;
    }

    public Criteria andSessionIdEqualTo(Long value) {
      addCriterion("session_id =", value, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdNotEqualTo(Long value) {
      addCriterion("session_id <>", value, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdGreaterThan(Long value) {
      addCriterion("session_id >", value, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdGreaterThanOrEqualTo(Long value) {
      addCriterion("session_id >=", value, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdLessThan(Long value) {
      addCriterion("session_id <", value, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdLessThanOrEqualTo(Long value) {
      addCriterion("session_id <=", value, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdIn(List<Long> values) {
      addCriterion("session_id in", values, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdNotIn(List<Long> values) {
      addCriterion("session_id not in", values, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdBetween(Long value1, Long value2) {
      addCriterion("session_id between", value1, value2, "sessionId");
      return (Criteria) this;
    }

    public Criteria andSessionIdNotBetween(Long value1, Long value2) {
      addCriterion("session_id not between", value1, value2, "sessionId");
      return (Criteria) this;
    }

    public Criteria andGmtCreateIsNull() {
      addCriterion("gmt_create is null");
      return (Criteria) this;
    }

    public Criteria andGmtCreateIsNotNull() {
      addCriterion("gmt_create is not null");
      return (Criteria) this;
    }

    public Criteria andGmtCreateEqualTo(Date value) {
      addCriterion("gmt_create =", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualTo(Date value) {
      addCriterion("gmt_create <>", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThan(Date value) {
      addCriterion("gmt_create >", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_create >=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThan(Date value) {
      addCriterion("gmt_create <", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      addCriterion("gmt_create <=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateIn(List<Date> values) {
      addCriterion("gmt_create in", values, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotIn(List<Date> values) {
      addCriterion("gmt_create not in", values, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateBetween(Date value1, Date value2) {
      addCriterion("gmt_create between", value1, value2, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotBetween(Date value1, Date value2) {
      addCriterion("gmt_create not between", value1, value2, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedIsNull() {
      addCriterion("gmt_modified is null");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedIsNotNull() {
      addCriterion("gmt_modified is not null");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedEqualTo(Date value) {
      addCriterion("gmt_modified =", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualTo(Date value) {
      addCriterion("gmt_modified <>", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThan(Date value) {
      addCriterion("gmt_modified >", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_modified >=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThan(Date value) {
      addCriterion("gmt_modified <", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      addCriterion("gmt_modified <=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedIn(List<Date> values) {
      addCriterion("gmt_modified in", values, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotIn(List<Date> values) {
      addCriterion("gmt_modified not in", values, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedBetween(Date value1, Date value2) {
      addCriterion("gmt_modified between", value1, value2, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotBetween(Date value1, Date value2) {
      addCriterion("gmt_modified not between", value1, value2, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andMarkIsNull() {
      addCriterion("mark is null");
      return (Criteria) this;
    }

    public Criteria andMarkIsNotNull() {
      addCriterion("mark is not null");
      return (Criteria) this;
    }

    public Criteria andMarkEqualTo(String value) {
      addCriterion("mark =", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkNotEqualTo(String value) {
      addCriterion("mark <>", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkGreaterThan(String value) {
      addCriterion("mark >", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkGreaterThanOrEqualTo(String value) {
      addCriterion("mark >=", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkLessThan(String value) {
      addCriterion("mark <", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkLessThanOrEqualTo(String value) {
      addCriterion("mark <=", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkLike(String value) {
      addCriterion("mark like", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkNotLike(String value) {
      addCriterion("mark not like", value, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkIn(List<String> values) {
      addCriterion("mark in", values, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkNotIn(List<String> values) {
      addCriterion("mark not in", values, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkBetween(String value1, String value2) {
      addCriterion("mark between", value1, value2, "mark");
      return (Criteria) this;
    }

    public Criteria andMarkNotBetween(String value1, String value2) {
      addCriterion("mark not between", value1, value2, "mark");
      return (Criteria) this;
    }

    public Criteria andStatusIsNull() {
      addCriterion("status is null");
      return (Criteria) this;
    }

    public Criteria andStatusIsNotNull() {
      addCriterion("status is not null");
      return (Criteria) this;
    }

    public Criteria andStatusEqualTo(String value) {
      addCriterion("status =", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotEqualTo(String value) {
      addCriterion("status <>", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThan(String value) {
      addCriterion("status >", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanOrEqualTo(String value) {
      addCriterion("status >=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThan(String value) {
      addCriterion("status <", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThanOrEqualTo(String value) {
      addCriterion("status <=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLike(String value) {
      addCriterion("status like", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotLike(String value) {
      addCriterion("status not like", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusIn(List<String> values) {
      addCriterion("status in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotIn(List<String> values) {
      addCriterion("status not in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusBetween(String value1, String value2) {
      addCriterion("status between", value1, value2, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotBetween(String value1, String value2) {
      addCriterion("status not between", value1, value2, "status");
      return (Criteria) this;
    }

    public Criteria andDslLike(String value) {
      addCriterion("dsl like", "%" + value + "%", "dsl");
      return (Criteria) this;
    }

    public Criteria andNlLike(String value) {
      addCriterion("nl like", "%" + value + "%", "nl");
      return (Criteria) this;
    }
  }

  /* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTaskDOExample$Criteria.class */
  public static class Criteria extends GeneratedCriteria {
    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNlLike(String value) {
      return super.andNlLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andDslLike(String value) {
      return super.andDslLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusNotBetween(String value1, String value2) {
      return super.andStatusNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusBetween(String value1, String value2) {
      return super.andStatusBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusNotIn(List values) {
      return super.andStatusNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusIn(List values) {
      return super.andStatusIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusNotLike(String value) {
      return super.andStatusNotLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusLike(String value) {
      return super.andStatusLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusLessThanOrEqualTo(String value) {
      return super.andStatusLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusLessThan(String value) {
      return super.andStatusLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusGreaterThanOrEqualTo(String value) {
      return super.andStatusGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusGreaterThan(String value) {
      return super.andStatusGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusNotEqualTo(String value) {
      return super.andStatusNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusEqualTo(String value) {
      return super.andStatusEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusIsNotNull() {
      return super.andStatusIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andStatusIsNull() {
      return super.andStatusIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkNotBetween(String value1, String value2) {
      return super.andMarkNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkBetween(String value1, String value2) {
      return super.andMarkBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkNotIn(List values) {
      return super.andMarkNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkIn(List values) {
      return super.andMarkIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkNotLike(String value) {
      return super.andMarkNotLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkLike(String value) {
      return super.andMarkLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkLessThanOrEqualTo(String value) {
      return super.andMarkLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkLessThan(String value) {
      return super.andMarkLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkGreaterThanOrEqualTo(String value) {
      return super.andMarkGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkGreaterThan(String value) {
      return super.andMarkGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkNotEqualTo(String value) {
      return super.andMarkNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkEqualTo(String value) {
      return super.andMarkEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkIsNotNull() {
      return super.andMarkIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andMarkIsNull() {
      return super.andMarkIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedNotBetween(
        Date value1, Date value2) {
      return super.andGmtModifiedNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedBetween(Date value1, Date value2) {
      return super.andGmtModifiedBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedNotIn(List values) {
      return super.andGmtModifiedNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedIn(List values) {
      return super.andGmtModifiedIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      return super.andGmtModifiedLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedLessThan(Date value) {
      return super.andGmtModifiedLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      return super.andGmtModifiedGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedGreaterThan(Date value) {
      return super.andGmtModifiedGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedNotEqualTo(Date value) {
      return super.andGmtModifiedNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedEqualTo(Date value) {
      return super.andGmtModifiedEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedIsNotNull() {
      return super.andGmtModifiedIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedIsNull() {
      return super.andGmtModifiedIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateNotBetween(Date value1, Date value2) {
      return super.andGmtCreateNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateBetween(Date value1, Date value2) {
      return super.andGmtCreateBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateNotIn(List values) {
      return super.andGmtCreateNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateIn(List values) {
      return super.andGmtCreateIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      return super.andGmtCreateLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateLessThan(Date value) {
      return super.andGmtCreateLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      return super.andGmtCreateGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateGreaterThan(Date value) {
      return super.andGmtCreateGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateNotEqualTo(Date value) {
      return super.andGmtCreateNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateEqualTo(Date value) {
      return super.andGmtCreateEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateIsNotNull() {
      return super.andGmtCreateIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateIsNull() {
      return super.andGmtCreateIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdNotBetween(Long value1, Long value2) {
      return super.andSessionIdNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdBetween(Long value1, Long value2) {
      return super.andSessionIdBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdNotIn(List values) {
      return super.andSessionIdNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdIn(List values) {
      return super.andSessionIdIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdLessThanOrEqualTo(Long value) {
      return super.andSessionIdLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdLessThan(Long value) {
      return super.andSessionIdLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdGreaterThanOrEqualTo(Long value) {
      return super.andSessionIdGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdGreaterThan(Long value) {
      return super.andSessionIdGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdNotEqualTo(Long value) {
      return super.andSessionIdNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdEqualTo(Long value) {
      return super.andSessionIdEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdIsNotNull() {
      return super.andSessionIdIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andSessionIdIsNull() {
      return super.andSessionIdIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdNotBetween(Long value1, Long value2) {
      return super.andUserIdNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdBetween(Long value1, Long value2) {
      return super.andUserIdBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdNotIn(List values) {
      return super.andUserIdNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdIn(List values) {
      return super.andUserIdIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdLessThanOrEqualTo(Long value) {
      return super.andUserIdLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdLessThan(Long value) {
      return super.andUserIdLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdGreaterThanOrEqualTo(Long value) {
      return super.andUserIdGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdGreaterThan(Long value) {
      return super.andUserIdGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdNotEqualTo(Long value) {
      return super.andUserIdNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdEqualTo(Long value) {
      return super.andUserIdEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdIsNotNull() {
      return super.andUserIdIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andUserIdIsNull() {
      return super.andUserIdIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdNotBetween(Long value1, Long value2) {
      return super.andProjectIdNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdBetween(Long value1, Long value2) {
      return super.andProjectIdBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdNotIn(List values) {
      return super.andProjectIdNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdIn(List values) {
      return super.andProjectIdIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdLessThanOrEqualTo(Long value) {
      return super.andProjectIdLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdLessThan(Long value) {
      return super.andProjectIdLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdGreaterThanOrEqualTo(Long value) {
      return super.andProjectIdGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdGreaterThan(Long value) {
      return super.andProjectIdGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdNotEqualTo(Long value) {
      return super.andProjectIdNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdEqualTo(Long value) {
      return super.andProjectIdEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdIsNotNull() {
      return super.andProjectIdIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdIsNull() {
      return super.andProjectIdIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdNotBetween(Long value1, Long value2) {
      return super.andIdNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdBetween(Long value1, Long value2) {
      return super.andIdBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdNotIn(List values) {
      return super.andIdNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdIn(List values) {
      return super.andIdIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdLessThanOrEqualTo(Long value) {
      return super.andIdLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdLessThan(Long value) {
      return super.andIdLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdGreaterThanOrEqualTo(Long value) {
      return super.andIdGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdGreaterThan(Long value) {
      return super.andIdGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdNotEqualTo(Long value) {
      return super.andIdNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdEqualTo(Long value) {
      return super.andIdEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdIsNotNull() {
      return super.andIdIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdIsNull() {
      return super.andIdIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ List getCriteria() {
      return super.getCriteria();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ List getAllCriteria() {
      return super.getAllCriteria();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ boolean isValid() {
      return super.isValid();
    }

    protected Criteria() {}
  }

  /* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTaskDOExample$Criterion.class */
  public static class Criterion {
    private String condition;
    private Object value;
    private Object secondValue;
    private boolean noValue;
    private boolean singleValue;
    private boolean betweenValue;
    private boolean listValue;
    private String typeHandler;

    public String getCondition() {
      return this.condition;
    }

    public Object getValue() {
      return this.value;
    }

    public Object getSecondValue() {
      return this.secondValue;
    }

    public boolean isNoValue() {
      return this.noValue;
    }

    public boolean isSingleValue() {
      return this.singleValue;
    }

    public boolean isBetweenValue() {
      return this.betweenValue;
    }

    public boolean isListValue() {
      return this.listValue;
    }

    public String getTypeHandler() {
      return this.typeHandler;
    }

    protected Criterion(String condition) {
      this.condition = condition;
      this.typeHandler = null;
      this.noValue = true;
    }

    protected Criterion(String condition, Object value, String typeHandler) {
      this.condition = condition;
      this.value = value;
      this.typeHandler = typeHandler;
      if (value instanceof List) {
        this.listValue = true;
      } else {
        this.singleValue = true;
      }
    }

    protected Criterion(String condition, Object value) {
      this(condition, value, (String) null);
    }

    protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
      this.condition = condition;
      this.value = value;
      this.secondValue = secondValue;
      this.typeHandler = typeHandler;
      this.betweenValue = true;
    }

    protected Criterion(String condition, Object value, Object secondValue) {
      this(condition, value, secondValue, null);
    }
  }
}
