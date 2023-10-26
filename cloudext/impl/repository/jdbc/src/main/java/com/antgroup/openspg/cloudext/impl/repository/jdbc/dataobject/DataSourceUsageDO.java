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

import java.util.Date;

public class DataSourceUsageDO {
    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String dataSourceName;

    private String usageType;

    private Byte isDefault;

    private String mountObjectId;

    private String mountObjectType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName == null ? null : dataSourceName.trim();
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType == null ? null : usageType.trim();
    }

    public Byte getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
    }

    public String getMountObjectId() {
        return mountObjectId;
    }

    public void setMountObjectId(String mountObjectId) {
        this.mountObjectId = mountObjectId == null ? null : mountObjectId.trim();
    }

    public String getMountObjectType() {
        return mountObjectType;
    }

    public void setMountObjectType(String mountObjectType) {
        this.mountObjectType = mountObjectType == null ? null : mountObjectType.trim();
    }
}