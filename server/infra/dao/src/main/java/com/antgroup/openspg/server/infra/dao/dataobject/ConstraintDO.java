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

import java.util.Date;

public class ConstraintDO {
  private Long id;

  private String name;

  private String nameZh;

  private String isRequire;

  private String upDownBoundary;

  private String maxValue;

  private String minValue;

  private String valuePattern;

  private String description;

  private String descriptionZh;

  private String isUnique;

  private String isEnum;

  private Date gmtCreate;

  private Date gmtModified;

  private String isMultiValue;

  private String enumValue;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getNameZh() {
    return nameZh;
  }

  public void setNameZh(String nameZh) {
    this.nameZh = nameZh == null ? null : nameZh.trim();
  }

  public String getIsRequire() {
    return isRequire;
  }

  public void setIsRequire(String isRequire) {
    this.isRequire = isRequire == null ? null : isRequire.trim();
  }

  public String getUpDownBoundary() {
    return upDownBoundary;
  }

  public void setUpDownBoundary(String upDownBoundary) {
    this.upDownBoundary = upDownBoundary == null ? null : upDownBoundary.trim();
  }

  public String getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(String maxValue) {
    this.maxValue = maxValue == null ? null : maxValue.trim();
  }

  public String getMinValue() {
    return minValue;
  }

  public void setMinValue(String minValue) {
    this.minValue = minValue == null ? null : minValue.trim();
  }

  public String getValuePattern() {
    return valuePattern;
  }

  public void setValuePattern(String valuePattern) {
    this.valuePattern = valuePattern == null ? null : valuePattern.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public String getDescriptionZh() {
    return descriptionZh;
  }

  public void setDescriptionZh(String descriptionZh) {
    this.descriptionZh = descriptionZh == null ? null : descriptionZh.trim();
  }

  public String getIsUnique() {
    return isUnique;
  }

  public void setIsUnique(String isUnique) {
    this.isUnique = isUnique == null ? null : isUnique.trim();
  }

  public String getIsEnum() {
    return isEnum;
  }

  public void setIsEnum(String isEnum) {
    this.isEnum = isEnum == null ? null : isEnum.trim();
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public String getIsMultiValue() {
    return isMultiValue;
  }

  public void setIsMultiValue(String isMultiValue) {
    this.isMultiValue = isMultiValue == null ? null : isMultiValue.trim();
  }

  public String getEnumValue() {
    return enumValue;
  }

  public void setEnumValue(String enumValue) {
    this.enumValue = enumValue == null ? null : enumValue.trim();
  }
}
