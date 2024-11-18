/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SemanticDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  public SemanticDOExample() {
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

    public Criteria andResourceIdIsNull() {
      addCriterion("resource_id is null");
      return (Criteria) this;
    }

    public Criteria andResourceIdIsNotNull() {
      addCriterion("resource_id is not null");
      return (Criteria) this;
    }

    public Criteria andResourceIdEqualTo(String value) {
      addCriterion("resource_id =", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdNotEqualTo(String value) {
      addCriterion("resource_id <>", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdGreaterThan(String value) {
      addCriterion("resource_id >", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdGreaterThanOrEqualTo(String value) {
      addCriterion("resource_id >=", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdLessThan(String value) {
      addCriterion("resource_id <", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdLessThanOrEqualTo(String value) {
      addCriterion("resource_id <=", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdLike(String value) {
      addCriterion("resource_id like", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdNotLike(String value) {
      addCriterion("resource_id not like", value, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdIn(List<String> values) {
      addCriterion("resource_id in", values, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdNotIn(List<String> values) {
      addCriterion("resource_id not in", values, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdBetween(String value1, String value2) {
      addCriterion("resource_id between", value1, value2, "resourceId");
      return (Criteria) this;
    }

    public Criteria andResourceIdNotBetween(String value1, String value2) {
      addCriterion("resource_id not between", value1, value2, "resourceId");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeIsNull() {
      addCriterion("semantic_type is null");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeIsNotNull() {
      addCriterion("semantic_type is not null");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeEqualTo(String value) {
      addCriterion("semantic_type =", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeNotEqualTo(String value) {
      addCriterion("semantic_type <>", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeGreaterThan(String value) {
      addCriterion("semantic_type >", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeGreaterThanOrEqualTo(String value) {
      addCriterion("semantic_type >=", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeLessThan(String value) {
      addCriterion("semantic_type <", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeLessThanOrEqualTo(String value) {
      addCriterion("semantic_type <=", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeLike(String value) {
      addCriterion("semantic_type like", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeNotLike(String value) {
      addCriterion("semantic_type not like", value, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeIn(List<String> values) {
      addCriterion("semantic_type in", values, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeNotIn(List<String> values) {
      addCriterion("semantic_type not in", values, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeBetween(String value1, String value2) {
      addCriterion("semantic_type between", value1, value2, "semanticType");
      return (Criteria) this;
    }

    public Criteria andSemanticTypeNotBetween(String value1, String value2) {
      addCriterion("semantic_type not between", value1, value2, "semanticType");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdIsNull() {
      addCriterion("original_resource_id is null");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdIsNotNull() {
      addCriterion("original_resource_id is not null");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdEqualTo(String value) {
      addCriterion("original_resource_id =", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdNotEqualTo(String value) {
      addCriterion("original_resource_id <>", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdGreaterThan(String value) {
      addCriterion("original_resource_id >", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdGreaterThanOrEqualTo(String value) {
      addCriterion("original_resource_id >=", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdLessThan(String value) {
      addCriterion("original_resource_id <", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdLessThanOrEqualTo(String value) {
      addCriterion("original_resource_id <=", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdLike(String value) {
      addCriterion("original_resource_id like", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdNotLike(String value) {
      addCriterion("original_resource_id not like", value, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdIn(List<String> values) {
      addCriterion("original_resource_id in", values, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdNotIn(List<String> values) {
      addCriterion("original_resource_id not in", values, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdBetween(String value1, String value2) {
      addCriterion("original_resource_id between", value1, value2, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andOriginalResourceIdNotBetween(String value1, String value2) {
      addCriterion("original_resource_id not between", value1, value2, "originalResourceId");
      return (Criteria) this;
    }

    public Criteria andResourceTypeIsNull() {
      addCriterion("resource_type is null");
      return (Criteria) this;
    }

    public Criteria andResourceTypeIsNotNull() {
      addCriterion("resource_type is not null");
      return (Criteria) this;
    }

    public Criteria andResourceTypeEqualTo(String value) {
      addCriterion("resource_type =", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeNotEqualTo(String value) {
      addCriterion("resource_type <>", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeGreaterThan(String value) {
      addCriterion("resource_type >", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeGreaterThanOrEqualTo(String value) {
      addCriterion("resource_type >=", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeLessThan(String value) {
      addCriterion("resource_type <", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeLessThanOrEqualTo(String value) {
      addCriterion("resource_type <=", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeLike(String value) {
      addCriterion("resource_type like", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeNotLike(String value) {
      addCriterion("resource_type not like", value, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeIn(List<String> values) {
      addCriterion("resource_type in", values, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeNotIn(List<String> values) {
      addCriterion("resource_type not in", values, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeBetween(String value1, String value2) {
      addCriterion("resource_type between", value1, value2, "resourceType");
      return (Criteria) this;
    }

    public Criteria andResourceTypeNotBetween(String value1, String value2) {
      addCriterion("resource_type not between", value1, value2, "resourceType");
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

    public Criteria andStatusEqualTo(Integer value) {
      addCriterion("status =", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotEqualTo(Integer value) {
      addCriterion("status <>", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThan(Integer value) {
      addCriterion("status >", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
      addCriterion("status >=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThan(Integer value) {
      addCriterion("status <", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThanOrEqualTo(Integer value) {
      addCriterion("status <=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusIn(List<Integer> values) {
      addCriterion("status in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotIn(List<Integer> values) {
      addCriterion("status not in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusBetween(Integer value1, Integer value2) {
      addCriterion("status between", value1, value2, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotBetween(Integer value1, Integer value2) {
      addCriterion("status not between", value1, value2, "status");
      return (Criteria) this;
    }

    public Criteria andRuleIdIsNull() {
      addCriterion("rule_id is null");
      return (Criteria) this;
    }

    public Criteria andRuleIdIsNotNull() {
      addCriterion("rule_id is not null");
      return (Criteria) this;
    }

    public Criteria andRuleIdEqualTo(String value) {
      addCriterion("rule_id =", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdNotEqualTo(String value) {
      addCriterion("rule_id <>", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdGreaterThan(String value) {
      addCriterion("rule_id >", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdGreaterThanOrEqualTo(String value) {
      addCriterion("rule_id >=", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdLessThan(String value) {
      addCriterion("rule_id <", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdLessThanOrEqualTo(String value) {
      addCriterion("rule_id <=", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdLike(String value) {
      addCriterion("rule_id like", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdNotLike(String value) {
      addCriterion("rule_id not like", value, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdIn(List<String> values) {
      addCriterion("rule_id in", values, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdNotIn(List<String> values) {
      addCriterion("rule_id not in", values, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdBetween(String value1, String value2) {
      addCriterion("rule_id between", value1, value2, "ruleId");
      return (Criteria) this;
    }

    public Criteria andRuleIdNotBetween(String value1, String value2) {
      addCriterion("rule_id not between", value1, value2, "ruleId");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeIsNull() {
      addCriterion("subject_meta_type is null");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeIsNotNull() {
      addCriterion("subject_meta_type is not null");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeEqualTo(String value) {
      addCriterion("subject_meta_type =", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeNotEqualTo(String value) {
      addCriterion("subject_meta_type <>", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeGreaterThan(String value) {
      addCriterion("subject_meta_type >", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeGreaterThanOrEqualTo(String value) {
      addCriterion("subject_meta_type >=", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeLessThan(String value) {
      addCriterion("subject_meta_type <", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeLessThanOrEqualTo(String value) {
      addCriterion("subject_meta_type <=", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeLike(String value) {
      addCriterion("subject_meta_type like", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeNotLike(String value) {
      addCriterion("subject_meta_type not like", value, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeIn(List<String> values) {
      addCriterion("subject_meta_type in", values, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeNotIn(List<String> values) {
      addCriterion("subject_meta_type not in", values, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeBetween(String value1, String value2) {
      addCriterion("subject_meta_type between", value1, value2, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andSubjectMetaTypeNotBetween(String value1, String value2) {
      addCriterion("subject_meta_type not between", value1, value2, "subjectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeIsNull() {
      addCriterion("object_meta_type is null");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeIsNotNull() {
      addCriterion("object_meta_type is not null");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeEqualTo(String value) {
      addCriterion("object_meta_type =", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeNotEqualTo(String value) {
      addCriterion("object_meta_type <>", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeGreaterThan(String value) {
      addCriterion("object_meta_type >", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeGreaterThanOrEqualTo(String value) {
      addCriterion("object_meta_type >=", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeLessThan(String value) {
      addCriterion("object_meta_type <", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeLessThanOrEqualTo(String value) {
      addCriterion("object_meta_type <=", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeLike(String value) {
      addCriterion("object_meta_type like", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeNotLike(String value) {
      addCriterion("object_meta_type not like", value, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeIn(List<String> values) {
      addCriterion("object_meta_type in", values, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeNotIn(List<String> values) {
      addCriterion("object_meta_type not in", values, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeBetween(String value1, String value2) {
      addCriterion("object_meta_type between", value1, value2, "objectMetaType");
      return (Criteria) this;
    }

    public Criteria andObjectMetaTypeNotBetween(String value1, String value2) {
      addCriterion("object_meta_type not between", value1, value2, "objectMetaType");
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
