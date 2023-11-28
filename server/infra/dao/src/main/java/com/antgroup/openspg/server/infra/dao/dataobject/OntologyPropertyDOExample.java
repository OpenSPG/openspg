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

package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OntologyPropertyDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  public OntologyPropertyDOExample() {
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

    public Criteria andDomainIdIsNull() {
      addCriterion("domain_id is null");
      return (Criteria) this;
    }

    public Criteria andDomainIdIsNotNull() {
      addCriterion("domain_id is not null");
      return (Criteria) this;
    }

    public Criteria andDomainIdEqualTo(Long value) {
      addCriterion("domain_id =", value, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdNotEqualTo(Long value) {
      addCriterion("domain_id <>", value, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdGreaterThan(Long value) {
      addCriterion("domain_id >", value, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdGreaterThanOrEqualTo(Long value) {
      addCriterion("domain_id >=", value, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdLessThan(Long value) {
      addCriterion("domain_id <", value, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdLessThanOrEqualTo(Long value) {
      addCriterion("domain_id <=", value, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdIn(List<Long> values) {
      addCriterion("domain_id in", values, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdNotIn(List<Long> values) {
      addCriterion("domain_id not in", values, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdBetween(Long value1, Long value2) {
      addCriterion("domain_id between", value1, value2, "domainId");
      return (Criteria) this;
    }

    public Criteria andDomainIdNotBetween(Long value1, Long value2) {
      addCriterion("domain_id not between", value1, value2, "domainId");
      return (Criteria) this;
    }

    public Criteria andPropertyNameIsNull() {
      addCriterion("property_name is null");
      return (Criteria) this;
    }

    public Criteria andPropertyNameIsNotNull() {
      addCriterion("property_name is not null");
      return (Criteria) this;
    }

    public Criteria andPropertyNameEqualTo(String value) {
      addCriterion("property_name =", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameNotEqualTo(String value) {
      addCriterion("property_name <>", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameGreaterThan(String value) {
      addCriterion("property_name >", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameGreaterThanOrEqualTo(String value) {
      addCriterion("property_name >=", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameLessThan(String value) {
      addCriterion("property_name <", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameLessThanOrEqualTo(String value) {
      addCriterion("property_name <=", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameLike(String value) {
      addCriterion("property_name like", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameNotLike(String value) {
      addCriterion("property_name not like", value, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameIn(List<String> values) {
      addCriterion("property_name in", values, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameNotIn(List<String> values) {
      addCriterion("property_name not in", values, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameBetween(String value1, String value2) {
      addCriterion("property_name between", value1, value2, "propertyName");
      return (Criteria) this;
    }

    public Criteria andPropertyNameNotBetween(String value1, String value2) {
      addCriterion("property_name not between", value1, value2, "propertyName");
      return (Criteria) this;
    }

    public Criteria andRangeIdIsNull() {
      addCriterion("range_id is null");
      return (Criteria) this;
    }

    public Criteria andRangeIdIsNotNull() {
      addCriterion("range_id is not null");
      return (Criteria) this;
    }

    public Criteria andRangeIdEqualTo(Long value) {
      addCriterion("range_id =", value, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdNotEqualTo(Long value) {
      addCriterion("range_id <>", value, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdGreaterThan(Long value) {
      addCriterion("range_id >", value, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdGreaterThanOrEqualTo(Long value) {
      addCriterion("range_id >=", value, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdLessThan(Long value) {
      addCriterion("range_id <", value, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdLessThanOrEqualTo(Long value) {
      addCriterion("range_id <=", value, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdIn(List<Long> values) {
      addCriterion("range_id in", values, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdNotIn(List<Long> values) {
      addCriterion("range_id not in", values, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdBetween(Long value1, Long value2) {
      addCriterion("range_id between", value1, value2, "rangeId");
      return (Criteria) this;
    }

    public Criteria andRangeIdNotBetween(Long value1, Long value2) {
      addCriterion("range_id not between", value1, value2, "rangeId");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhIsNull() {
      addCriterion("property_name_zh is null");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhIsNotNull() {
      addCriterion("property_name_zh is not null");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhEqualTo(String value) {
      addCriterion("property_name_zh =", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhNotEqualTo(String value) {
      addCriterion("property_name_zh <>", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhGreaterThan(String value) {
      addCriterion("property_name_zh >", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhGreaterThanOrEqualTo(String value) {
      addCriterion("property_name_zh >=", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhLessThan(String value) {
      addCriterion("property_name_zh <", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhLessThanOrEqualTo(String value) {
      addCriterion("property_name_zh <=", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhLike(String value) {
      addCriterion("property_name_zh like", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhNotLike(String value) {
      addCriterion("property_name_zh not like", value, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhIn(List<String> values) {
      addCriterion("property_name_zh in", values, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhNotIn(List<String> values) {
      addCriterion("property_name_zh not in", values, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhBetween(String value1, String value2) {
      addCriterion("property_name_zh between", value1, value2, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andPropertyNameZhNotBetween(String value1, String value2) {
      addCriterion("property_name_zh not between", value1, value2, "propertyNameZh");
      return (Criteria) this;
    }

    public Criteria andConstraintIdIsNull() {
      addCriterion("constraint_id is null");
      return (Criteria) this;
    }

    public Criteria andConstraintIdIsNotNull() {
      addCriterion("constraint_id is not null");
      return (Criteria) this;
    }

    public Criteria andConstraintIdEqualTo(Long value) {
      addCriterion("constraint_id =", value, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdNotEqualTo(Long value) {
      addCriterion("constraint_id <>", value, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdGreaterThan(Long value) {
      addCriterion("constraint_id >", value, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdGreaterThanOrEqualTo(Long value) {
      addCriterion("constraint_id >=", value, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdLessThan(Long value) {
      addCriterion("constraint_id <", value, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdLessThanOrEqualTo(Long value) {
      addCriterion("constraint_id <=", value, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdIn(List<Long> values) {
      addCriterion("constraint_id in", values, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdNotIn(List<Long> values) {
      addCriterion("constraint_id not in", values, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdBetween(Long value1, Long value2) {
      addCriterion("constraint_id between", value1, value2, "constraintId");
      return (Criteria) this;
    }

    public Criteria andConstraintIdNotBetween(Long value1, Long value2) {
      addCriterion("constraint_id not between", value1, value2, "constraintId");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryIsNull() {
      addCriterion("property_category is null");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryIsNotNull() {
      addCriterion("property_category is not null");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryEqualTo(String value) {
      addCriterion("property_category =", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryNotEqualTo(String value) {
      addCriterion("property_category <>", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryGreaterThan(String value) {
      addCriterion("property_category >", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryGreaterThanOrEqualTo(String value) {
      addCriterion("property_category >=", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryLessThan(String value) {
      addCriterion("property_category <", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryLessThanOrEqualTo(String value) {
      addCriterion("property_category <=", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryLike(String value) {
      addCriterion("property_category like", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryNotLike(String value) {
      addCriterion("property_category not like", value, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryIn(List<String> values) {
      addCriterion("property_category in", values, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryNotIn(List<String> values) {
      addCriterion("property_category not in", values, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryBetween(String value1, String value2) {
      addCriterion("property_category between", value1, value2, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andPropertyCategoryNotBetween(String value1, String value2) {
      addCriterion("property_category not between", value1, value2, "propertyCategory");
      return (Criteria) this;
    }

    public Criteria andMapTypeIsNull() {
      addCriterion("map_type is null");
      return (Criteria) this;
    }

    public Criteria andMapTypeIsNotNull() {
      addCriterion("map_type is not null");
      return (Criteria) this;
    }

    public Criteria andMapTypeEqualTo(String value) {
      addCriterion("map_type =", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeNotEqualTo(String value) {
      addCriterion("map_type <>", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeGreaterThan(String value) {
      addCriterion("map_type >", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeGreaterThanOrEqualTo(String value) {
      addCriterion("map_type >=", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeLessThan(String value) {
      addCriterion("map_type <", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeLessThanOrEqualTo(String value) {
      addCriterion("map_type <=", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeLike(String value) {
      addCriterion("map_type like", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeNotLike(String value) {
      addCriterion("map_type not like", value, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeIn(List<String> values) {
      addCriterion("map_type in", values, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeNotIn(List<String> values) {
      addCriterion("map_type not in", values, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeBetween(String value1, String value2) {
      addCriterion("map_type between", value1, value2, "mapType");
      return (Criteria) this;
    }

    public Criteria andMapTypeNotBetween(String value1, String value2) {
      addCriterion("map_type not between", value1, value2, "mapType");
      return (Criteria) this;
    }

    public Criteria andVersionIsNull() {
      addCriterion("version is null");
      return (Criteria) this;
    }

    public Criteria andVersionIsNotNull() {
      addCriterion("version is not null");
      return (Criteria) this;
    }

    public Criteria andVersionEqualTo(Integer value) {
      addCriterion("version =", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotEqualTo(Integer value) {
      addCriterion("version <>", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThan(Integer value) {
      addCriterion("version >", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThanOrEqualTo(Integer value) {
      addCriterion("version >=", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionLessThan(Integer value) {
      addCriterion("version <", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionLessThanOrEqualTo(Integer value) {
      addCriterion("version <=", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionIn(List<Integer> values) {
      addCriterion("version in", values, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotIn(List<Integer> values) {
      addCriterion("version not in", values, "version");
      return (Criteria) this;
    }

    public Criteria andVersionBetween(Integer value1, Integer value2) {
      addCriterion("version between", value1, value2, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotBetween(Integer value1, Integer value2) {
      addCriterion("version not between", value1, value2, "version");
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

    public Criteria andOriginalIdIsNull() {
      addCriterion("original_id is null");
      return (Criteria) this;
    }

    public Criteria andOriginalIdIsNotNull() {
      addCriterion("original_id is not null");
      return (Criteria) this;
    }

    public Criteria andOriginalIdEqualTo(Long value) {
      addCriterion("original_id =", value, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdNotEqualTo(Long value) {
      addCriterion("original_id <>", value, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdGreaterThan(Long value) {
      addCriterion("original_id >", value, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdGreaterThanOrEqualTo(Long value) {
      addCriterion("original_id >=", value, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdLessThan(Long value) {
      addCriterion("original_id <", value, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdLessThanOrEqualTo(Long value) {
      addCriterion("original_id <=", value, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdIn(List<Long> values) {
      addCriterion("original_id in", values, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdNotIn(List<Long> values) {
      addCriterion("original_id not in", values, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdBetween(Long value1, Long value2) {
      addCriterion("original_id between", value1, value2, "originalId");
      return (Criteria) this;
    }

    public Criteria andOriginalIdNotBetween(Long value1, Long value2) {
      addCriterion("original_id not between", value1, value2, "originalId");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameIsNull() {
      addCriterion("store_property_name is null");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameIsNotNull() {
      addCriterion("store_property_name is not null");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameEqualTo(String value) {
      addCriterion("store_property_name =", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameNotEqualTo(String value) {
      addCriterion("store_property_name <>", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameGreaterThan(String value) {
      addCriterion("store_property_name >", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameGreaterThanOrEqualTo(String value) {
      addCriterion("store_property_name >=", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameLessThan(String value) {
      addCriterion("store_property_name <", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameLessThanOrEqualTo(String value) {
      addCriterion("store_property_name <=", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameLike(String value) {
      addCriterion("store_property_name like", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameNotLike(String value) {
      addCriterion("store_property_name not like", value, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameIn(List<String> values) {
      addCriterion("store_property_name in", values, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameNotIn(List<String> values) {
      addCriterion("store_property_name not in", values, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameBetween(String value1, String value2) {
      addCriterion("store_property_name between", value1, value2, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andStorePropertyNameNotBetween(String value1, String value2) {
      addCriterion("store_property_name not between", value1, value2, "storePropertyName");
      return (Criteria) this;
    }

    public Criteria andTransformerIdIsNull() {
      addCriterion("transformer_id is null");
      return (Criteria) this;
    }

    public Criteria andTransformerIdIsNotNull() {
      addCriterion("transformer_id is not null");
      return (Criteria) this;
    }

    public Criteria andTransformerIdEqualTo(Long value) {
      addCriterion("transformer_id =", value, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdNotEqualTo(Long value) {
      addCriterion("transformer_id <>", value, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdGreaterThan(Long value) {
      addCriterion("transformer_id >", value, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdGreaterThanOrEqualTo(Long value) {
      addCriterion("transformer_id >=", value, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdLessThan(Long value) {
      addCriterion("transformer_id <", value, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdLessThanOrEqualTo(Long value) {
      addCriterion("transformer_id <=", value, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdIn(List<Long> values) {
      addCriterion("transformer_id in", values, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdNotIn(List<Long> values) {
      addCriterion("transformer_id not in", values, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdBetween(Long value1, Long value2) {
      addCriterion("transformer_id between", value1, value2, "transformerId");
      return (Criteria) this;
    }

    public Criteria andTransformerIdNotBetween(Long value1, Long value2) {
      addCriterion("transformer_id not between", value1, value2, "transformerId");
      return (Criteria) this;
    }

    public Criteria andPropertyDescIsNull() {
      addCriterion("property_desc is null");
      return (Criteria) this;
    }

    public Criteria andPropertyDescIsNotNull() {
      addCriterion("property_desc is not null");
      return (Criteria) this;
    }

    public Criteria andPropertyDescEqualTo(String value) {
      addCriterion("property_desc =", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescNotEqualTo(String value) {
      addCriterion("property_desc <>", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescGreaterThan(String value) {
      addCriterion("property_desc >", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescGreaterThanOrEqualTo(String value) {
      addCriterion("property_desc >=", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescLessThan(String value) {
      addCriterion("property_desc <", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescLessThanOrEqualTo(String value) {
      addCriterion("property_desc <=", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescLike(String value) {
      addCriterion("property_desc like", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescNotLike(String value) {
      addCriterion("property_desc not like", value, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescIn(List<String> values) {
      addCriterion("property_desc in", values, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescNotIn(List<String> values) {
      addCriterion("property_desc not in", values, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescBetween(String value1, String value2) {
      addCriterion("property_desc between", value1, value2, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescNotBetween(String value1, String value2) {
      addCriterion("property_desc not between", value1, value2, "propertyDesc");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhIsNull() {
      addCriterion("property_desc_zh is null");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhIsNotNull() {
      addCriterion("property_desc_zh is not null");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhEqualTo(String value) {
      addCriterion("property_desc_zh =", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhNotEqualTo(String value) {
      addCriterion("property_desc_zh <>", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhGreaterThan(String value) {
      addCriterion("property_desc_zh >", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhGreaterThanOrEqualTo(String value) {
      addCriterion("property_desc_zh >=", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhLessThan(String value) {
      addCriterion("property_desc_zh <", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhLessThanOrEqualTo(String value) {
      addCriterion("property_desc_zh <=", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhLike(String value) {
      addCriterion("property_desc_zh like", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhNotLike(String value) {
      addCriterion("property_desc_zh not like", value, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhIn(List<String> values) {
      addCriterion("property_desc_zh in", values, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhNotIn(List<String> values) {
      addCriterion("property_desc_zh not in", values, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhBetween(String value1, String value2) {
      addCriterion("property_desc_zh between", value1, value2, "propertyDescZh");
      return (Criteria) this;
    }

    public Criteria andPropertyDescZhNotBetween(String value1, String value2) {
      addCriterion("property_desc_zh not between", value1, value2, "propertyDescZh");
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

    public Criteria andOriginalDomainIdIsNull() {
      addCriterion("original_domain_id is null");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdIsNotNull() {
      addCriterion("original_domain_id is not null");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdEqualTo(Long value) {
      addCriterion("original_domain_id =", value, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdNotEqualTo(Long value) {
      addCriterion("original_domain_id <>", value, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdGreaterThan(Long value) {
      addCriterion("original_domain_id >", value, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdGreaterThanOrEqualTo(Long value) {
      addCriterion("original_domain_id >=", value, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdLessThan(Long value) {
      addCriterion("original_domain_id <", value, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdLessThanOrEqualTo(Long value) {
      addCriterion("original_domain_id <=", value, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdIn(List<Long> values) {
      addCriterion("original_domain_id in", values, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdNotIn(List<Long> values) {
      addCriterion("original_domain_id not in", values, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdBetween(Long value1, Long value2) {
      addCriterion("original_domain_id between", value1, value2, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalDomainIdNotBetween(Long value1, Long value2) {
      addCriterion("original_domain_id not between", value1, value2, "originalDomainId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdIsNull() {
      addCriterion("original_range_id is null");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdIsNotNull() {
      addCriterion("original_range_id is not null");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdEqualTo(Long value) {
      addCriterion("original_range_id =", value, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdNotEqualTo(Long value) {
      addCriterion("original_range_id <>", value, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdGreaterThan(Long value) {
      addCriterion("original_range_id >", value, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdGreaterThanOrEqualTo(Long value) {
      addCriterion("original_range_id >=", value, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdLessThan(Long value) {
      addCriterion("original_range_id <", value, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdLessThanOrEqualTo(Long value) {
      addCriterion("original_range_id <=", value, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdIn(List<Long> values) {
      addCriterion("original_range_id in", values, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdNotIn(List<Long> values) {
      addCriterion("original_range_id not in", values, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdBetween(Long value1, Long value2) {
      addCriterion("original_range_id between", value1, value2, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andOriginalRangeIdNotBetween(Long value1, Long value2) {
      addCriterion("original_range_id not between", value1, value2, "originalRangeId");
      return (Criteria) this;
    }

    public Criteria andVersionStatusIsNull() {
      addCriterion("version_status is null");
      return (Criteria) this;
    }

    public Criteria andVersionStatusIsNotNull() {
      addCriterion("version_status is not null");
      return (Criteria) this;
    }

    public Criteria andVersionStatusEqualTo(String value) {
      addCriterion("version_status =", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusNotEqualTo(String value) {
      addCriterion("version_status <>", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusGreaterThan(String value) {
      addCriterion("version_status >", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusGreaterThanOrEqualTo(String value) {
      addCriterion("version_status >=", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusLessThan(String value) {
      addCriterion("version_status <", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusLessThanOrEqualTo(String value) {
      addCriterion("version_status <=", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusLike(String value) {
      addCriterion("version_status like", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusNotLike(String value) {
      addCriterion("version_status not like", value, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusIn(List<String> values) {
      addCriterion("version_status in", values, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusNotIn(List<String> values) {
      addCriterion("version_status not in", values, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusBetween(String value1, String value2) {
      addCriterion("version_status between", value1, value2, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andVersionStatusNotBetween(String value1, String value2) {
      addCriterion("version_status not between", value1, value2, "versionStatus");
      return (Criteria) this;
    }

    public Criteria andRelationSourceIsNull() {
      addCriterion("relation_source is null");
      return (Criteria) this;
    }

    public Criteria andRelationSourceIsNotNull() {
      addCriterion("relation_source is not null");
      return (Criteria) this;
    }

    public Criteria andRelationSourceEqualTo(String value) {
      addCriterion("relation_source =", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceNotEqualTo(String value) {
      addCriterion("relation_source <>", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceGreaterThan(String value) {
      addCriterion("relation_source >", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceGreaterThanOrEqualTo(String value) {
      addCriterion("relation_source >=", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceLessThan(String value) {
      addCriterion("relation_source <", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceLessThanOrEqualTo(String value) {
      addCriterion("relation_source <=", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceLike(String value) {
      addCriterion("relation_source like", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceNotLike(String value) {
      addCriterion("relation_source not like", value, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceIn(List<String> values) {
      addCriterion("relation_source in", values, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceNotIn(List<String> values) {
      addCriterion("relation_source not in", values, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceBetween(String value1, String value2) {
      addCriterion("relation_source between", value1, value2, "relationSource");
      return (Criteria) this;
    }

    public Criteria andRelationSourceNotBetween(String value1, String value2) {
      addCriterion("relation_source not between", value1, value2, "relationSource");
      return (Criteria) this;
    }

    public Criteria andDirectionIsNull() {
      addCriterion("direction is null");
      return (Criteria) this;
    }

    public Criteria andDirectionIsNotNull() {
      addCriterion("direction is not null");
      return (Criteria) this;
    }

    public Criteria andDirectionEqualTo(String value) {
      addCriterion("direction =", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionNotEqualTo(String value) {
      addCriterion("direction <>", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionGreaterThan(String value) {
      addCriterion("direction >", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionGreaterThanOrEqualTo(String value) {
      addCriterion("direction >=", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionLessThan(String value) {
      addCriterion("direction <", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionLessThanOrEqualTo(String value) {
      addCriterion("direction <=", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionLike(String value) {
      addCriterion("direction like", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionNotLike(String value) {
      addCriterion("direction not like", value, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionIn(List<String> values) {
      addCriterion("direction in", values, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionNotIn(List<String> values) {
      addCriterion("direction not in", values, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionBetween(String value1, String value2) {
      addCriterion("direction between", value1, value2, "direction");
      return (Criteria) this;
    }

    public Criteria andDirectionNotBetween(String value1, String value2) {
      addCriterion("direction not between", value1, value2, "direction");
      return (Criteria) this;
    }

    public Criteria andMaskTypeIsNull() {
      addCriterion("mask_type is null");
      return (Criteria) this;
    }

    public Criteria andMaskTypeIsNotNull() {
      addCriterion("mask_type is not null");
      return (Criteria) this;
    }

    public Criteria andMaskTypeEqualTo(String value) {
      addCriterion("mask_type =", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeNotEqualTo(String value) {
      addCriterion("mask_type <>", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeGreaterThan(String value) {
      addCriterion("mask_type >", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeGreaterThanOrEqualTo(String value) {
      addCriterion("mask_type >=", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeLessThan(String value) {
      addCriterion("mask_type <", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeLessThanOrEqualTo(String value) {
      addCriterion("mask_type <=", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeLike(String value) {
      addCriterion("mask_type like", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeNotLike(String value) {
      addCriterion("mask_type not like", value, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeIn(List<String> values) {
      addCriterion("mask_type in", values, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeNotIn(List<String> values) {
      addCriterion("mask_type not in", values, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeBetween(String value1, String value2) {
      addCriterion("mask_type between", value1, value2, "maskType");
      return (Criteria) this;
    }

    public Criteria andMaskTypeNotBetween(String value1, String value2) {
      addCriterion("mask_type not between", value1, value2, "maskType");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigIsNull() {
      addCriterion("multiver_config is null");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigIsNotNull() {
      addCriterion("multiver_config is not null");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigEqualTo(String value) {
      addCriterion("multiver_config =", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigNotEqualTo(String value) {
      addCriterion("multiver_config <>", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigGreaterThan(String value) {
      addCriterion("multiver_config >", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigGreaterThanOrEqualTo(String value) {
      addCriterion("multiver_config >=", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigLessThan(String value) {
      addCriterion("multiver_config <", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigLessThanOrEqualTo(String value) {
      addCriterion("multiver_config <=", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigLike(String value) {
      addCriterion("multiver_config like", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigNotLike(String value) {
      addCriterion("multiver_config not like", value, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigIn(List<String> values) {
      addCriterion("multiver_config in", values, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigNotIn(List<String> values) {
      addCriterion("multiver_config not in", values, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigBetween(String value1, String value2) {
      addCriterion("multiver_config between", value1, value2, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andMultiverConfigNotBetween(String value1, String value2) {
      addCriterion("multiver_config not between", value1, value2, "multiverConfig");
      return (Criteria) this;
    }

    public Criteria andPropertySourceIsNull() {
      addCriterion("property_source is null");
      return (Criteria) this;
    }

    public Criteria andPropertySourceIsNotNull() {
      addCriterion("property_source is not null");
      return (Criteria) this;
    }

    public Criteria andPropertySourceEqualTo(Long value) {
      addCriterion("property_source =", value, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceNotEqualTo(Long value) {
      addCriterion("property_source <>", value, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceGreaterThan(Long value) {
      addCriterion("property_source >", value, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceGreaterThanOrEqualTo(Long value) {
      addCriterion("property_source >=", value, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceLessThan(Long value) {
      addCriterion("property_source <", value, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceLessThanOrEqualTo(Long value) {
      addCriterion("property_source <=", value, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceIn(List<Long> values) {
      addCriterion("property_source in", values, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceNotIn(List<Long> values) {
      addCriterion("property_source not in", values, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceBetween(Long value1, Long value2) {
      addCriterion("property_source between", value1, value2, "propertySource");
      return (Criteria) this;
    }

    public Criteria andPropertySourceNotBetween(Long value1, Long value2) {
      addCriterion("property_source not between", value1, value2, "propertySource");
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
