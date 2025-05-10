package com.antgroup.openspgapp.infra.dao.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTutorialDOExample.class */
public class ReasonTutorialDOExample {
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

  /* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTutorialDOExample$GeneratedCriteria.class */
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

    public Criteria andEnableIsNull() {
      addCriterion("enable is null");
      return (Criteria) this;
    }

    public Criteria andEnableIsNotNull() {
      addCriterion("enable is not null");
      return (Criteria) this;
    }

    public Criteria andEnableEqualTo(Boolean value) {
      addCriterion("enable =", value, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableNotEqualTo(Boolean value) {
      addCriterion("enable <>", value, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableGreaterThan(Boolean value) {
      addCriterion("enable >", value, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableGreaterThanOrEqualTo(Boolean value) {
      addCriterion("enable >=", value, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableLessThan(Boolean value) {
      addCriterion("enable <", value, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableLessThanOrEqualTo(Boolean value) {
      addCriterion("enable <=", value, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableIn(List<Boolean> values) {
      addCriterion("enable in", values, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableNotIn(List<Boolean> values) {
      addCriterion("enable not in", values, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableBetween(Boolean value1, Boolean value2) {
      addCriterion("enable between", value1, value2, "enable");
      return (Criteria) this;
    }

    public Criteria andEnableNotBetween(Boolean value1, Boolean value2) {
      addCriterion("enable not between", value1, value2, "enable");
      return (Criteria) this;
    }

    public Criteria andNameIsNull() {
      addCriterion("name is null");
      return (Criteria) this;
    }

    public Criteria andNameIsNotNull() {
      addCriterion("name is not null");
      return (Criteria) this;
    }

    public Criteria andNameEqualTo(String value) {
      addCriterion("name =", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotEqualTo(String value) {
      addCriterion("name <>", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThan(String value) {
      addCriterion("name >", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanOrEqualTo(String value) {
      addCriterion("name >=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThan(String value) {
      addCriterion("name <", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThanOrEqualTo(String value) {
      addCriterion("name <=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLike(String value) {
      addCriterion("name like", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotLike(String value) {
      addCriterion("name not like", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameIn(List<String> values) {
      addCriterion("name in", values, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotIn(List<String> values) {
      addCriterion("name not in", values, "name");
      return (Criteria) this;
    }

    public Criteria andNameBetween(String value1, String value2) {
      addCriterion("name between", value1, value2, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotBetween(String value1, String value2) {
      addCriterion("name not between", value1, value2, "name");
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

  /* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTutorialDOExample$Criteria.class */
  public static class Criteria extends GeneratedCriteria {
    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNlLike(String value) {
      return super.andNlLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andDslLike(String value) {
      return super.andDslLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameNotBetween(String value1, String value2) {
      return super.andNameNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameBetween(String value1, String value2) {
      return super.andNameBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameNotIn(List values) {
      return super.andNameNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameIn(List values) {
      return super.andNameIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameNotLike(String value) {
      return super.andNameNotLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameLike(String value) {
      return super.andNameLike(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameLessThanOrEqualTo(String value) {
      return super.andNameLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameLessThan(String value) {
      return super.andNameLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameGreaterThanOrEqualTo(String value) {
      return super.andNameGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameGreaterThan(String value) {
      return super.andNameGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameNotEqualTo(String value) {
      return super.andNameNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameEqualTo(String value) {
      return super.andNameEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameIsNotNull() {
      return super.andNameIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andNameIsNull() {
      return super.andNameIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableNotBetween(
        Boolean value1, Boolean value2) {
      return super.andEnableNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableBetween(Boolean value1, Boolean value2) {
      return super.andEnableBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableNotIn(List values) {
      return super.andEnableNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableIn(List values) {
      return super.andEnableIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableLessThanOrEqualTo(Boolean value) {
      return super.andEnableLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableLessThan(Boolean value) {
      return super.andEnableLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableGreaterThanOrEqualTo(Boolean value) {
      return super.andEnableGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableGreaterThan(Boolean value) {
      return super.andEnableGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableNotEqualTo(Boolean value) {
      return super.andEnableNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableEqualTo(Boolean value) {
      return super.andEnableEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableIsNotNull() {
      return super.andEnableIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andEnableIsNull() {
      return super.andEnableIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedNotBetween(
        Date value1, Date value2) {
      return super.andGmtModifiedNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedBetween(Date value1, Date value2) {
      return super.andGmtModifiedBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedNotIn(List values) {
      return super.andGmtModifiedNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedIn(List values) {
      return super.andGmtModifiedIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      return super.andGmtModifiedLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedLessThan(Date value) {
      return super.andGmtModifiedLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      return super.andGmtModifiedGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedGreaterThan(Date value) {
      return super.andGmtModifiedGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedNotEqualTo(Date value) {
      return super.andGmtModifiedNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedEqualTo(Date value) {
      return super.andGmtModifiedEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedIsNotNull() {
      return super.andGmtModifiedIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtModifiedIsNull() {
      return super.andGmtModifiedIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateNotBetween(Date value1, Date value2) {
      return super.andGmtCreateNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateBetween(Date value1, Date value2) {
      return super.andGmtCreateBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateNotIn(List values) {
      return super.andGmtCreateNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateIn(List values) {
      return super.andGmtCreateIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      return super.andGmtCreateLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateLessThan(Date value) {
      return super.andGmtCreateLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      return super.andGmtCreateGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateGreaterThan(Date value) {
      return super.andGmtCreateGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateNotEqualTo(Date value) {
      return super.andGmtCreateNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateEqualTo(Date value) {
      return super.andGmtCreateEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateIsNotNull() {
      return super.andGmtCreateIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andGmtCreateIsNull() {
      return super.andGmtCreateIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdNotBetween(Long value1, Long value2) {
      return super.andProjectIdNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdBetween(Long value1, Long value2) {
      return super.andProjectIdBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdNotIn(List values) {
      return super.andProjectIdNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdIn(List values) {
      return super.andProjectIdIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdLessThanOrEqualTo(Long value) {
      return super.andProjectIdLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdLessThan(Long value) {
      return super.andProjectIdLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdGreaterThanOrEqualTo(Long value) {
      return super.andProjectIdGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdGreaterThan(Long value) {
      return super.andProjectIdGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdNotEqualTo(Long value) {
      return super.andProjectIdNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdEqualTo(Long value) {
      return super.andProjectIdEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdIsNotNull() {
      return super.andProjectIdIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andProjectIdIsNull() {
      return super.andProjectIdIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdNotBetween(Long value1, Long value2) {
      return super.andIdNotBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdBetween(Long value1, Long value2) {
      return super.andIdBetween(value1, value2);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdNotIn(List values) {
      return super.andIdNotIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdIn(List values) {
      return super.andIdIn(values);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdLessThanOrEqualTo(Long value) {
      return super.andIdLessThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdLessThan(Long value) {
      return super.andIdLessThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdGreaterThanOrEqualTo(Long value) {
      return super.andIdGreaterThanOrEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdGreaterThan(Long value) {
      return super.andIdGreaterThan(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdNotEqualTo(Long value) {
      return super.andIdNotEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdEqualTo(Long value) {
      return super.andIdEqualTo(value);
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdIsNotNull() {
      return super.andIdIsNotNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ Criteria andIdIsNull() {
      return super.andIdIsNull();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ List getCriteria() {
      return super.getCriteria();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ List getAllCriteria() {
      return super.getAllCriteria();
    }

    @Override // com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample.GeneratedCriteria
    public /* bridge */ /* synthetic */ boolean isValid() {
      return super.isValid();
    }

    protected Criteria() {}
  }

  /* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTutorialDOExample$Criterion.class */
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
