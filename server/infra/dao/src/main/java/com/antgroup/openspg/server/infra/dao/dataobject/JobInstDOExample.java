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

public class JobInstDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  public JobInstDOExample() {
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

    public Criteria andJobIdIsNull() {
      addCriterion("job_id is null");
      return (Criteria) this;
    }

    public Criteria andJobIdIsNotNull() {
      addCriterion("job_id is not null");
      return (Criteria) this;
    }

    public Criteria andJobIdEqualTo(Long value) {
      addCriterion("job_id =", value, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdNotEqualTo(Long value) {
      addCriterion("job_id <>", value, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdGreaterThan(Long value) {
      addCriterion("job_id >", value, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdGreaterThanOrEqualTo(Long value) {
      addCriterion("job_id >=", value, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdLessThan(Long value) {
      addCriterion("job_id <", value, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdLessThanOrEqualTo(Long value) {
      addCriterion("job_id <=", value, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdIn(List<Long> values) {
      addCriterion("job_id in", values, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdNotIn(List<Long> values) {
      addCriterion("job_id not in", values, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdBetween(Long value1, Long value2) {
      addCriterion("job_id between", value1, value2, "jobId");
      return (Criteria) this;
    }

    public Criteria andJobIdNotBetween(Long value1, Long value2) {
      addCriterion("job_id not between", value1, value2, "jobId");
      return (Criteria) this;
    }

    public Criteria andTypeIsNull() {
      addCriterion("type is null");
      return (Criteria) this;
    }

    public Criteria andTypeIsNotNull() {
      addCriterion("type is not null");
      return (Criteria) this;
    }

    public Criteria andTypeEqualTo(String value) {
      addCriterion("type =", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotEqualTo(String value) {
      addCriterion("type <>", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThan(String value) {
      addCriterion("type >", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThanOrEqualTo(String value) {
      addCriterion("type >=", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeLessThan(String value) {
      addCriterion("type <", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeLessThanOrEqualTo(String value) {
      addCriterion("type <=", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeLike(String value) {
      addCriterion("type like", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotLike(String value) {
      addCriterion("type not like", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeIn(List<String> values) {
      addCriterion("type in", values, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotIn(List<String> values) {
      addCriterion("type not in", values, "type");
      return (Criteria) this;
    }

    public Criteria andTypeBetween(String value1, String value2) {
      addCriterion("type between", value1, value2, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotBetween(String value1, String value2) {
      addCriterion("type not between", value1, value2, "type");
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

    public Criteria andHostIsNull() {
      addCriterion("host is null");
      return (Criteria) this;
    }

    public Criteria andHostIsNotNull() {
      addCriterion("host is not null");
      return (Criteria) this;
    }

    public Criteria andHostEqualTo(String value) {
      addCriterion("host =", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostNotEqualTo(String value) {
      addCriterion("host <>", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostGreaterThan(String value) {
      addCriterion("host >", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostGreaterThanOrEqualTo(String value) {
      addCriterion("host >=", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostLessThan(String value) {
      addCriterion("host <", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostLessThanOrEqualTo(String value) {
      addCriterion("host <=", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostLike(String value) {
      addCriterion("host like", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostNotLike(String value) {
      addCriterion("host not like", value, "host");
      return (Criteria) this;
    }

    public Criteria andHostIn(List<String> values) {
      addCriterion("host in", values, "host");
      return (Criteria) this;
    }

    public Criteria andHostNotIn(List<String> values) {
      addCriterion("host not in", values, "host");
      return (Criteria) this;
    }

    public Criteria andHostBetween(String value1, String value2) {
      addCriterion("host between", value1, value2, "host");
      return (Criteria) this;
    }

    public Criteria andHostNotBetween(String value1, String value2) {
      addCriterion("host not between", value1, value2, "host");
      return (Criteria) this;
    }

    public Criteria andTraceIdIsNull() {
      addCriterion("trace_id is null");
      return (Criteria) this;
    }

    public Criteria andTraceIdIsNotNull() {
      addCriterion("trace_id is not null");
      return (Criteria) this;
    }

    public Criteria andTraceIdEqualTo(String value) {
      addCriterion("trace_id =", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdNotEqualTo(String value) {
      addCriterion("trace_id <>", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdGreaterThan(String value) {
      addCriterion("trace_id >", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdGreaterThanOrEqualTo(String value) {
      addCriterion("trace_id >=", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdLessThan(String value) {
      addCriterion("trace_id <", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdLessThanOrEqualTo(String value) {
      addCriterion("trace_id <=", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdLike(String value) {
      addCriterion("trace_id like", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdNotLike(String value) {
      addCriterion("trace_id not like", value, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdIn(List<String> values) {
      addCriterion("trace_id in", values, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdNotIn(List<String> values) {
      addCriterion("trace_id not in", values, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdBetween(String value1, String value2) {
      addCriterion("trace_id between", value1, value2, "traceId");
      return (Criteria) this;
    }

    public Criteria andTraceIdNotBetween(String value1, String value2) {
      addCriterion("trace_id not between", value1, value2, "traceId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdIsNull() {
      addCriterion("idempotent_id is null");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdIsNotNull() {
      addCriterion("idempotent_id is not null");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdEqualTo(String value) {
      addCriterion("idempotent_id =", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdNotEqualTo(String value) {
      addCriterion("idempotent_id <>", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdGreaterThan(String value) {
      addCriterion("idempotent_id >", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdGreaterThanOrEqualTo(String value) {
      addCriterion("idempotent_id >=", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdLessThan(String value) {
      addCriterion("idempotent_id <", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdLessThanOrEqualTo(String value) {
      addCriterion("idempotent_id <=", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdLike(String value) {
      addCriterion("idempotent_id like", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdNotLike(String value) {
      addCriterion("idempotent_id not like", value, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdIn(List<String> values) {
      addCriterion("idempotent_id in", values, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdNotIn(List<String> values) {
      addCriterion("idempotent_id not in", values, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdBetween(String value1, String value2) {
      addCriterion("idempotent_id between", value1, value2, "idempotentId");
      return (Criteria) this;
    }

    public Criteria andIdempotentIdNotBetween(String value1, String value2) {
      addCriterion("idempotent_id not between", value1, value2, "idempotentId");
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
