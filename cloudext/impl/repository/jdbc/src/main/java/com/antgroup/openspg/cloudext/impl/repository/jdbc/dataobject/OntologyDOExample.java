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

public class OntologyDOExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OntologyDOExample() {
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

        public Criteria andEntityCategoryIsNull() {
            addCriterion("entity_category is null");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryIsNotNull() {
            addCriterion("entity_category is not null");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryEqualTo(String value) {
            addCriterion("entity_category =", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryNotEqualTo(String value) {
            addCriterion("entity_category <>", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryGreaterThan(String value) {
            addCriterion("entity_category >", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryGreaterThanOrEqualTo(String value) {
            addCriterion("entity_category >=", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryLessThan(String value) {
            addCriterion("entity_category <", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryLessThanOrEqualTo(String value) {
            addCriterion("entity_category <=", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryLike(String value) {
            addCriterion("entity_category like", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryNotLike(String value) {
            addCriterion("entity_category not like", value, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryIn(List<String> values) {
            addCriterion("entity_category in", values, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryNotIn(List<String> values) {
            addCriterion("entity_category not in", values, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryBetween(String value1, String value2) {
            addCriterion("entity_category between", value1, value2, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andEntityCategoryNotBetween(String value1, String value2) {
            addCriterion("entity_category not between", value1, value2, "entityCategory");
            return (Criteria) this;
        }

        public Criteria andLayerIsNull() {
            addCriterion("layer is null");
            return (Criteria) this;
        }

        public Criteria andLayerIsNotNull() {
            addCriterion("layer is not null");
            return (Criteria) this;
        }

        public Criteria andLayerEqualTo(String value) {
            addCriterion("layer =", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerNotEqualTo(String value) {
            addCriterion("layer <>", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerGreaterThan(String value) {
            addCriterion("layer >", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerGreaterThanOrEqualTo(String value) {
            addCriterion("layer >=", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerLessThan(String value) {
            addCriterion("layer <", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerLessThanOrEqualTo(String value) {
            addCriterion("layer <=", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerLike(String value) {
            addCriterion("layer like", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerNotLike(String value) {
            addCriterion("layer not like", value, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerIn(List<String> values) {
            addCriterion("layer in", values, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerNotIn(List<String> values) {
            addCriterion("layer not in", values, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerBetween(String value1, String value2) {
            addCriterion("layer between", value1, value2, "layer");
            return (Criteria) this;
        }

        public Criteria andLayerNotBetween(String value1, String value2) {
            addCriterion("layer not between", value1, value2, "layer");
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

        public Criteria andWithIndexIsNull() {
            addCriterion("with_index is null");
            return (Criteria) this;
        }

        public Criteria andWithIndexIsNotNull() {
            addCriterion("with_index is not null");
            return (Criteria) this;
        }

        public Criteria andWithIndexEqualTo(String value) {
            addCriterion("with_index =", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexNotEqualTo(String value) {
            addCriterion("with_index <>", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexGreaterThan(String value) {
            addCriterion("with_index >", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexGreaterThanOrEqualTo(String value) {
            addCriterion("with_index >=", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexLessThan(String value) {
            addCriterion("with_index <", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexLessThanOrEqualTo(String value) {
            addCriterion("with_index <=", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexLike(String value) {
            addCriterion("with_index like", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexNotLike(String value) {
            addCriterion("with_index not like", value, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexIn(List<String> values) {
            addCriterion("with_index in", values, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexNotIn(List<String> values) {
            addCriterion("with_index not in", values, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexBetween(String value1, String value2) {
            addCriterion("with_index between", value1, value2, "withIndex");
            return (Criteria) this;
        }

        public Criteria andWithIndexNotBetween(String value1, String value2) {
            addCriterion("with_index not between", value1, value2, "withIndex");
            return (Criteria) this;
        }

        public Criteria andScopeIsNull() {
            addCriterion("scope is null");
            return (Criteria) this;
        }

        public Criteria andScopeIsNotNull() {
            addCriterion("scope is not null");
            return (Criteria) this;
        }

        public Criteria andScopeEqualTo(String value) {
            addCriterion("scope =", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeNotEqualTo(String value) {
            addCriterion("scope <>", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeGreaterThan(String value) {
            addCriterion("scope >", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeGreaterThanOrEqualTo(String value) {
            addCriterion("scope >=", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeLessThan(String value) {
            addCriterion("scope <", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeLessThanOrEqualTo(String value) {
            addCriterion("scope <=", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeLike(String value) {
            addCriterion("scope like", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeNotLike(String value) {
            addCriterion("scope not like", value, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeIn(List<String> values) {
            addCriterion("scope in", values, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeNotIn(List<String> values) {
            addCriterion("scope not in", values, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeBetween(String value1, String value2) {
            addCriterion("scope between", value1, value2, "scope");
            return (Criteria) this;
        }

        public Criteria andScopeNotBetween(String value1, String value2) {
            addCriterion("scope not between", value1, value2, "scope");
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

        public Criteria andUniqueNameIsNull() {
            addCriterion("unique_name is null");
            return (Criteria) this;
        }

        public Criteria andUniqueNameIsNotNull() {
            addCriterion("unique_name is not null");
            return (Criteria) this;
        }

        public Criteria andUniqueNameEqualTo(String value) {
            addCriterion("unique_name =", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameNotEqualTo(String value) {
            addCriterion("unique_name <>", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameGreaterThan(String value) {
            addCriterion("unique_name >", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameGreaterThanOrEqualTo(String value) {
            addCriterion("unique_name >=", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameLessThan(String value) {
            addCriterion("unique_name <", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameLessThanOrEqualTo(String value) {
            addCriterion("unique_name <=", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameLike(String value) {
            addCriterion("unique_name like", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameNotLike(String value) {
            addCriterion("unique_name not like", value, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameIn(List<String> values) {
            addCriterion("unique_name in", values, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameNotIn(List<String> values) {
            addCriterion("unique_name not in", values, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameBetween(String value1, String value2) {
            addCriterion("unique_name between", value1, value2, "uniqueName");
            return (Criteria) this;
        }

        public Criteria andUniqueNameNotBetween(String value1, String value2) {
            addCriterion("unique_name not between", value1, value2, "uniqueName");
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