/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OntologyParentRelDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  public OntologyParentRelDOExample() {
    oredCriteria = new ArrayList<Criteria>();
  }

  public void setOrderByClause(String orderByClause) {
    this.orderByClause = orderByClause;
  }

  public String getOrderByClause() {
    return orderByClause;
  }

  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }

  public boolean isDistinct() {
    return distinct;
  }

  public List<Criteria> getOredCriteria() {
    return oredCriteria;
  }

  public void or(Criteria criteria) {
    oredCriteria.add(criteria);
  }

  public Criteria or() {
    Criteria criteria = createCriteriaInternal();
    oredCriteria.add(criteria);
    return criteria;
  }

  public Criteria createCriteria() {
    Criteria criteria = createCriteriaInternal();
    if (oredCriteria.size() == 0) {
      oredCriteria.add(criteria);
    }
    return criteria;
  }

  protected Criteria createCriteriaInternal() {
    Criteria criteria = new Criteria();
    return criteria;
  }

  public void clear() {
    oredCriteria.clear();
    orderByClause = null;
    distinct = false;
  }

  protected abstract static class GeneratedCriteria {
    protected List<Criterion> criteria;

    protected GeneratedCriteria() {
      super();
      criteria = new ArrayList<Criterion>();
    }

    public boolean isValid() {
      return criteria.size() > 0;
    }

    public List<Criterion> getAllCriteria() {
      return criteria;
    }

    public List<Criterion> getCriteria() {
      return criteria;
    }

    protected void addCriterion(String condition) {
      if (condition == null) {
        throw new RuntimeException("Value for condition cannot be null");
      }
      criteria.add(new Criterion(condition));
    }

    protected void addCriterion(String condition, Object value, String property) {
      if (value == null) {
        throw new RuntimeException("Value for " + property + " cannot be null");
      }
      criteria.add(new Criterion(condition, value));
    }

    protected void addCriterion(String condition, Object value1, Object value2, String property) {
      if (value1 == null || value2 == null) {
        throw new RuntimeException("Between values for " + property + " cannot be null");
      }
      criteria.add(new Criterion(condition, value1, value2));
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

    public Criteria andEntityIdIsNull() {
      addCriterion("entity_id is null");
      return (Criteria) this;
    }

    public Criteria andEntityIdIsNotNull() {
      addCriterion("entity_id is not null");
      return (Criteria) this;
    }

    public Criteria andEntityIdEqualTo(Long value) {
      addCriterion("entity_id =", value, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdNotEqualTo(Long value) {
      addCriterion("entity_id <>", value, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdGreaterThan(Long value) {
      addCriterion("entity_id >", value, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdGreaterThanOrEqualTo(Long value) {
      addCriterion("entity_id >=", value, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdLessThan(Long value) {
      addCriterion("entity_id <", value, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdLessThanOrEqualTo(Long value) {
      addCriterion("entity_id <=", value, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdIn(List<Long> values) {
      addCriterion("entity_id in", values, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdNotIn(List<Long> values) {
      addCriterion("entity_id not in", values, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdBetween(Long value1, Long value2) {
      addCriterion("entity_id between", value1, value2, "entityId");
      return (Criteria) this;
    }

    public Criteria andEntityIdNotBetween(Long value1, Long value2) {
      addCriterion("entity_id not between", value1, value2, "entityId");
      return (Criteria) this;
    }

    public Criteria andParentIdIsNull() {
      addCriterion("parent_id is null");
      return (Criteria) this;
    }

    public Criteria andParentIdIsNotNull() {
      addCriterion("parent_id is not null");
      return (Criteria) this;
    }

    public Criteria andParentIdEqualTo(Long value) {
      addCriterion("parent_id =", value, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdNotEqualTo(Long value) {
      addCriterion("parent_id <>", value, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdGreaterThan(Long value) {
      addCriterion("parent_id >", value, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdGreaterThanOrEqualTo(Long value) {
      addCriterion("parent_id >=", value, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdLessThan(Long value) {
      addCriterion("parent_id <", value, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdLessThanOrEqualTo(Long value) {
      addCriterion("parent_id <=", value, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdIn(List<Long> values) {
      addCriterion("parent_id in", values, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdNotIn(List<Long> values) {
      addCriterion("parent_id not in", values, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdBetween(Long value1, Long value2) {
      addCriterion("parent_id between", value1, value2, "parentId");
      return (Criteria) this;
    }

    public Criteria andParentIdNotBetween(Long value1, Long value2) {
      addCriterion("parent_id not between", value1, value2, "parentId");
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

    public Criteria andPathIsNull() {
      addCriterion("path is null");
      return (Criteria) this;
    }

    public Criteria andPathIsNotNull() {
      addCriterion("path is not null");
      return (Criteria) this;
    }

    public Criteria andPathEqualTo(String value) {
      addCriterion("path =", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathNotEqualTo(String value) {
      addCriterion("path <>", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathGreaterThan(String value) {
      addCriterion("path >", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathGreaterThanOrEqualTo(String value) {
      addCriterion("path >=", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathLessThan(String value) {
      addCriterion("path <", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathLessThanOrEqualTo(String value) {
      addCriterion("path <=", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathLike(String value) {
      addCriterion("path like", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathNotLike(String value) {
      addCriterion("path not like", value, "path");
      return (Criteria) this;
    }

    public Criteria andPathIn(List<String> values) {
      addCriterion("path in", values, "path");
      return (Criteria) this;
    }

    public Criteria andPathNotIn(List<String> values) {
      addCriterion("path not in", values, "path");
      return (Criteria) this;
    }

    public Criteria andPathBetween(String value1, String value2) {
      addCriterion("path between", value1, value2, "path");
      return (Criteria) this;
    }

    public Criteria andPathNotBetween(String value1, String value2) {
      addCriterion("path not between", value1, value2, "path");
      return (Criteria) this;
    }

    public Criteria andDeepInheritIsNull() {
      addCriterion("deep_inherit is null");
      return (Criteria) this;
    }

    public Criteria andDeepInheritIsNotNull() {
      addCriterion("deep_inherit is not null");
      return (Criteria) this;
    }

    public Criteria andDeepInheritEqualTo(String value) {
      addCriterion("deep_inherit =", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritNotEqualTo(String value) {
      addCriterion("deep_inherit <>", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritGreaterThan(String value) {
      addCriterion("deep_inherit >", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritGreaterThanOrEqualTo(String value) {
      addCriterion("deep_inherit >=", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritLessThan(String value) {
      addCriterion("deep_inherit <", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritLessThanOrEqualTo(String value) {
      addCriterion("deep_inherit <=", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritLike(String value) {
      addCriterion("deep_inherit like", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritNotLike(String value) {
      addCriterion("deep_inherit not like", value, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritIn(List<String> values) {
      addCriterion("deep_inherit in", values, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritNotIn(List<String> values) {
      addCriterion("deep_inherit not in", values, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritBetween(String value1, String value2) {
      addCriterion("deep_inherit between", value1, value2, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andDeepInheritNotBetween(String value1, String value2) {
      addCriterion("deep_inherit not between", value1, value2, "deepInherit");
      return (Criteria) this;
    }

    public Criteria andHistoryPathIsNull() {
      addCriterion("history_path is null");
      return (Criteria) this;
    }

    public Criteria andHistoryPathIsNotNull() {
      addCriterion("history_path is not null");
      return (Criteria) this;
    }

    public Criteria andHistoryPathEqualTo(String value) {
      addCriterion("history_path =", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathNotEqualTo(String value) {
      addCriterion("history_path <>", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathGreaterThan(String value) {
      addCriterion("history_path >", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathGreaterThanOrEqualTo(String value) {
      addCriterion("history_path >=", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathLessThan(String value) {
      addCriterion("history_path <", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathLessThanOrEqualTo(String value) {
      addCriterion("history_path <=", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathLike(String value) {
      addCriterion("history_path like", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathNotLike(String value) {
      addCriterion("history_path not like", value, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathIn(List<String> values) {
      addCriterion("history_path in", values, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathNotIn(List<String> values) {
      addCriterion("history_path not in", values, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathBetween(String value1, String value2) {
      addCriterion("history_path between", value1, value2, "historyPath");
      return (Criteria) this;
    }

    public Criteria andHistoryPathNotBetween(String value1, String value2) {
      addCriterion("history_path not between", value1, value2, "historyPath");
      return (Criteria) this;
    }
  }

  public static class Criteria extends GeneratedCriteria {

    protected Criteria() {
      super();
    }
  }

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
      return condition;
    }

    public Object getValue() {
      return value;
    }

    public Object getSecondValue() {
      return secondValue;
    }

    public boolean isNoValue() {
      return noValue;
    }

    public boolean isSingleValue() {
      return singleValue;
    }

    public boolean isBetweenValue() {
      return betweenValue;
    }

    public boolean isListValue() {
      return listValue;
    }

    public String getTypeHandler() {
      return typeHandler;
    }

    protected Criterion(String condition) {
      super();
      this.condition = condition;
      this.typeHandler = null;
      this.noValue = true;
    }

    protected Criterion(String condition, Object value, String typeHandler) {
      super();
      this.condition = condition;
      this.value = value;
      this.typeHandler = typeHandler;
      if (value instanceof List<?>) {
        this.listValue = true;
      } else {
        this.singleValue = true;
      }
    }

    protected Criterion(String condition, Object value) {
      this(condition, value, null);
    }

    protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
      super();
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
