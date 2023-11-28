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

public class OperatorVersionDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  public OperatorVersionDOExample() {
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

    public Criteria andOverviewIdIsNull() {
      addCriterion("overview_id is null");
      return (Criteria) this;
    }

    public Criteria andOverviewIdIsNotNull() {
      addCriterion("overview_id is not null");
      return (Criteria) this;
    }

    public Criteria andOverviewIdEqualTo(Long value) {
      addCriterion("overview_id =", value, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdNotEqualTo(Long value) {
      addCriterion("overview_id <>", value, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdGreaterThan(Long value) {
      addCriterion("overview_id >", value, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdGreaterThanOrEqualTo(Long value) {
      addCriterion("overview_id >=", value, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdLessThan(Long value) {
      addCriterion("overview_id <", value, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdLessThanOrEqualTo(Long value) {
      addCriterion("overview_id <=", value, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdIn(List<Long> values) {
      addCriterion("overview_id in", values, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdNotIn(List<Long> values) {
      addCriterion("overview_id not in", values, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdBetween(Long value1, Long value2) {
      addCriterion("overview_id between", value1, value2, "overviewId");
      return (Criteria) this;
    }

    public Criteria andOverviewIdNotBetween(Long value1, Long value2) {
      addCriterion("overview_id not between", value1, value2, "overviewId");
      return (Criteria) this;
    }

    public Criteria andMainClassIsNull() {
      addCriterion("main_class is null");
      return (Criteria) this;
    }

    public Criteria andMainClassIsNotNull() {
      addCriterion("main_class is not null");
      return (Criteria) this;
    }

    public Criteria andMainClassEqualTo(String value) {
      addCriterion("main_class =", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassNotEqualTo(String value) {
      addCriterion("main_class <>", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassGreaterThan(String value) {
      addCriterion("main_class >", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassGreaterThanOrEqualTo(String value) {
      addCriterion("main_class >=", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassLessThan(String value) {
      addCriterion("main_class <", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassLessThanOrEqualTo(String value) {
      addCriterion("main_class <=", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassLike(String value) {
      addCriterion("main_class like", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassNotLike(String value) {
      addCriterion("main_class not like", value, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassIn(List<String> values) {
      addCriterion("main_class in", values, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassNotIn(List<String> values) {
      addCriterion("main_class not in", values, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassBetween(String value1, String value2) {
      addCriterion("main_class between", value1, value2, "mainClass");
      return (Criteria) this;
    }

    public Criteria andMainClassNotBetween(String value1, String value2) {
      addCriterion("main_class not between", value1, value2, "mainClass");
      return (Criteria) this;
    }

    public Criteria andJarAddressIsNull() {
      addCriterion("jar_address is null");
      return (Criteria) this;
    }

    public Criteria andJarAddressIsNotNull() {
      addCriterion("jar_address is not null");
      return (Criteria) this;
    }

    public Criteria andJarAddressEqualTo(String value) {
      addCriterion("jar_address =", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressNotEqualTo(String value) {
      addCriterion("jar_address <>", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressGreaterThan(String value) {
      addCriterion("jar_address >", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressGreaterThanOrEqualTo(String value) {
      addCriterion("jar_address >=", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressLessThan(String value) {
      addCriterion("jar_address <", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressLessThanOrEqualTo(String value) {
      addCriterion("jar_address <=", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressLike(String value) {
      addCriterion("jar_address like", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressNotLike(String value) {
      addCriterion("jar_address not like", value, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressIn(List<String> values) {
      addCriterion("jar_address in", values, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressNotIn(List<String> values) {
      addCriterion("jar_address not in", values, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressBetween(String value1, String value2) {
      addCriterion("jar_address between", value1, value2, "jarAddress");
      return (Criteria) this;
    }

    public Criteria andJarAddressNotBetween(String value1, String value2) {
      addCriterion("jar_address not between", value1, value2, "jarAddress");
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
