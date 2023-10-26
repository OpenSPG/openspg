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

public class ConstraintDOExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConstraintDOExample() {
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

        public Criteria andNameZhIsNull() {
            addCriterion("name_zh is null");
            return (Criteria) this;
        }

        public Criteria andNameZhIsNotNull() {
            addCriterion("name_zh is not null");
            return (Criteria) this;
        }

        public Criteria andNameZhEqualTo(String value) {
            addCriterion("name_zh =", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotEqualTo(String value) {
            addCriterion("name_zh <>", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhGreaterThan(String value) {
            addCriterion("name_zh >", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhGreaterThanOrEqualTo(String value) {
            addCriterion("name_zh >=", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhLessThan(String value) {
            addCriterion("name_zh <", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhLessThanOrEqualTo(String value) {
            addCriterion("name_zh <=", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhLike(String value) {
            addCriterion("name_zh like", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotLike(String value) {
            addCriterion("name_zh not like", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhIn(List<String> values) {
            addCriterion("name_zh in", values, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotIn(List<String> values) {
            addCriterion("name_zh not in", values, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhBetween(String value1, String value2) {
            addCriterion("name_zh between", value1, value2, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotBetween(String value1, String value2) {
            addCriterion("name_zh not between", value1, value2, "nameZh");
            return (Criteria) this;
        }

        public Criteria andIsRequireIsNull() {
            addCriterion("is_require is null");
            return (Criteria) this;
        }

        public Criteria andIsRequireIsNotNull() {
            addCriterion("is_require is not null");
            return (Criteria) this;
        }

        public Criteria andIsRequireEqualTo(String value) {
            addCriterion("is_require =", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireNotEqualTo(String value) {
            addCriterion("is_require <>", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireGreaterThan(String value) {
            addCriterion("is_require >", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireGreaterThanOrEqualTo(String value) {
            addCriterion("is_require >=", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireLessThan(String value) {
            addCriterion("is_require <", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireLessThanOrEqualTo(String value) {
            addCriterion("is_require <=", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireLike(String value) {
            addCriterion("is_require like", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireNotLike(String value) {
            addCriterion("is_require not like", value, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireIn(List<String> values) {
            addCriterion("is_require in", values, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireNotIn(List<String> values) {
            addCriterion("is_require not in", values, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireBetween(String value1, String value2) {
            addCriterion("is_require between", value1, value2, "isRequire");
            return (Criteria) this;
        }

        public Criteria andIsRequireNotBetween(String value1, String value2) {
            addCriterion("is_require not between", value1, value2, "isRequire");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryIsNull() {
            addCriterion("up_down_boundary is null");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryIsNotNull() {
            addCriterion("up_down_boundary is not null");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryEqualTo(String value) {
            addCriterion("up_down_boundary =", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryNotEqualTo(String value) {
            addCriterion("up_down_boundary <>", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryGreaterThan(String value) {
            addCriterion("up_down_boundary >", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryGreaterThanOrEqualTo(String value) {
            addCriterion("up_down_boundary >=", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryLessThan(String value) {
            addCriterion("up_down_boundary <", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryLessThanOrEqualTo(String value) {
            addCriterion("up_down_boundary <=", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryLike(String value) {
            addCriterion("up_down_boundary like", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryNotLike(String value) {
            addCriterion("up_down_boundary not like", value, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryIn(List<String> values) {
            addCriterion("up_down_boundary in", values, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryNotIn(List<String> values) {
            addCriterion("up_down_boundary not in", values, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryBetween(String value1, String value2) {
            addCriterion("up_down_boundary between", value1, value2, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andUpDownBoundaryNotBetween(String value1, String value2) {
            addCriterion("up_down_boundary not between", value1, value2, "upDownBoundary");
            return (Criteria) this;
        }

        public Criteria andMaxValueIsNull() {
            addCriterion("max_value is null");
            return (Criteria) this;
        }

        public Criteria andMaxValueIsNotNull() {
            addCriterion("max_value is not null");
            return (Criteria) this;
        }

        public Criteria andMaxValueEqualTo(String value) {
            addCriterion("max_value =", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueNotEqualTo(String value) {
            addCriterion("max_value <>", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueGreaterThan(String value) {
            addCriterion("max_value >", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueGreaterThanOrEqualTo(String value) {
            addCriterion("max_value >=", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueLessThan(String value) {
            addCriterion("max_value <", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueLessThanOrEqualTo(String value) {
            addCriterion("max_value <=", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueLike(String value) {
            addCriterion("max_value like", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueNotLike(String value) {
            addCriterion("max_value not like", value, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueIn(List<String> values) {
            addCriterion("max_value in", values, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueNotIn(List<String> values) {
            addCriterion("max_value not in", values, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueBetween(String value1, String value2) {
            addCriterion("max_value between", value1, value2, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMaxValueNotBetween(String value1, String value2) {
            addCriterion("max_value not between", value1, value2, "maxValue");
            return (Criteria) this;
        }

        public Criteria andMinValueIsNull() {
            addCriterion("min_value is null");
            return (Criteria) this;
        }

        public Criteria andMinValueIsNotNull() {
            addCriterion("min_value is not null");
            return (Criteria) this;
        }

        public Criteria andMinValueEqualTo(String value) {
            addCriterion("min_value =", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueNotEqualTo(String value) {
            addCriterion("min_value <>", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueGreaterThan(String value) {
            addCriterion("min_value >", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueGreaterThanOrEqualTo(String value) {
            addCriterion("min_value >=", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueLessThan(String value) {
            addCriterion("min_value <", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueLessThanOrEqualTo(String value) {
            addCriterion("min_value <=", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueLike(String value) {
            addCriterion("min_value like", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueNotLike(String value) {
            addCriterion("min_value not like", value, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueIn(List<String> values) {
            addCriterion("min_value in", values, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueNotIn(List<String> values) {
            addCriterion("min_value not in", values, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueBetween(String value1, String value2) {
            addCriterion("min_value between", value1, value2, "minValue");
            return (Criteria) this;
        }

        public Criteria andMinValueNotBetween(String value1, String value2) {
            addCriterion("min_value not between", value1, value2, "minValue");
            return (Criteria) this;
        }

        public Criteria andValuePatternIsNull() {
            addCriterion("value_pattern is null");
            return (Criteria) this;
        }

        public Criteria andValuePatternIsNotNull() {
            addCriterion("value_pattern is not null");
            return (Criteria) this;
        }

        public Criteria andValuePatternEqualTo(String value) {
            addCriterion("value_pattern =", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternNotEqualTo(String value) {
            addCriterion("value_pattern <>", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternGreaterThan(String value) {
            addCriterion("value_pattern >", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternGreaterThanOrEqualTo(String value) {
            addCriterion("value_pattern >=", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternLessThan(String value) {
            addCriterion("value_pattern <", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternLessThanOrEqualTo(String value) {
            addCriterion("value_pattern <=", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternLike(String value) {
            addCriterion("value_pattern like", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternNotLike(String value) {
            addCriterion("value_pattern not like", value, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternIn(List<String> values) {
            addCriterion("value_pattern in", values, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternNotIn(List<String> values) {
            addCriterion("value_pattern not in", values, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternBetween(String value1, String value2) {
            addCriterion("value_pattern between", value1, value2, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andValuePatternNotBetween(String value1, String value2) {
            addCriterion("value_pattern not between", value1, value2, "valuePattern");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhIsNull() {
            addCriterion("description_zh is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhIsNotNull() {
            addCriterion("description_zh is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhEqualTo(String value) {
            addCriterion("description_zh =", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhNotEqualTo(String value) {
            addCriterion("description_zh <>", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhGreaterThan(String value) {
            addCriterion("description_zh >", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhGreaterThanOrEqualTo(String value) {
            addCriterion("description_zh >=", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhLessThan(String value) {
            addCriterion("description_zh <", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhLessThanOrEqualTo(String value) {
            addCriterion("description_zh <=", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhLike(String value) {
            addCriterion("description_zh like", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhNotLike(String value) {
            addCriterion("description_zh not like", value, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhIn(List<String> values) {
            addCriterion("description_zh in", values, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhNotIn(List<String> values) {
            addCriterion("description_zh not in", values, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhBetween(String value1, String value2) {
            addCriterion("description_zh between", value1, value2, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andDescriptionZhNotBetween(String value1, String value2) {
            addCriterion("description_zh not between", value1, value2, "descriptionZh");
            return (Criteria) this;
        }

        public Criteria andIsUniqueIsNull() {
            addCriterion("is_unique is null");
            return (Criteria) this;
        }

        public Criteria andIsUniqueIsNotNull() {
            addCriterion("is_unique is not null");
            return (Criteria) this;
        }

        public Criteria andIsUniqueEqualTo(String value) {
            addCriterion("is_unique =", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueNotEqualTo(String value) {
            addCriterion("is_unique <>", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueGreaterThan(String value) {
            addCriterion("is_unique >", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueGreaterThanOrEqualTo(String value) {
            addCriterion("is_unique >=", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueLessThan(String value) {
            addCriterion("is_unique <", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueLessThanOrEqualTo(String value) {
            addCriterion("is_unique <=", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueLike(String value) {
            addCriterion("is_unique like", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueNotLike(String value) {
            addCriterion("is_unique not like", value, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueIn(List<String> values) {
            addCriterion("is_unique in", values, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueNotIn(List<String> values) {
            addCriterion("is_unique not in", values, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueBetween(String value1, String value2) {
            addCriterion("is_unique between", value1, value2, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsUniqueNotBetween(String value1, String value2) {
            addCriterion("is_unique not between", value1, value2, "isUnique");
            return (Criteria) this;
        }

        public Criteria andIsEnumIsNull() {
            addCriterion("is_enum is null");
            return (Criteria) this;
        }

        public Criteria andIsEnumIsNotNull() {
            addCriterion("is_enum is not null");
            return (Criteria) this;
        }

        public Criteria andIsEnumEqualTo(String value) {
            addCriterion("is_enum =", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumNotEqualTo(String value) {
            addCriterion("is_enum <>", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumGreaterThan(String value) {
            addCriterion("is_enum >", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumGreaterThanOrEqualTo(String value) {
            addCriterion("is_enum >=", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumLessThan(String value) {
            addCriterion("is_enum <", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumLessThanOrEqualTo(String value) {
            addCriterion("is_enum <=", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumLike(String value) {
            addCriterion("is_enum like", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumNotLike(String value) {
            addCriterion("is_enum not like", value, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumIn(List<String> values) {
            addCriterion("is_enum in", values, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumNotIn(List<String> values) {
            addCriterion("is_enum not in", values, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumBetween(String value1, String value2) {
            addCriterion("is_enum between", value1, value2, "isEnum");
            return (Criteria) this;
        }

        public Criteria andIsEnumNotBetween(String value1, String value2) {
            addCriterion("is_enum not between", value1, value2, "isEnum");
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

        public Criteria andIsMultiValueIsNull() {
            addCriterion("is_multi_value is null");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueIsNotNull() {
            addCriterion("is_multi_value is not null");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueEqualTo(String value) {
            addCriterion("is_multi_value =", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueNotEqualTo(String value) {
            addCriterion("is_multi_value <>", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueGreaterThan(String value) {
            addCriterion("is_multi_value >", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueGreaterThanOrEqualTo(String value) {
            addCriterion("is_multi_value >=", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueLessThan(String value) {
            addCriterion("is_multi_value <", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueLessThanOrEqualTo(String value) {
            addCriterion("is_multi_value <=", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueLike(String value) {
            addCriterion("is_multi_value like", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueNotLike(String value) {
            addCriterion("is_multi_value not like", value, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueIn(List<String> values) {
            addCriterion("is_multi_value in", values, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueNotIn(List<String> values) {
            addCriterion("is_multi_value not in", values, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueBetween(String value1, String value2) {
            addCriterion("is_multi_value between", value1, value2, "isMultiValue");
            return (Criteria) this;
        }

        public Criteria andIsMultiValueNotBetween(String value1, String value2) {
            addCriterion("is_multi_value not between", value1, value2, "isMultiValue");
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