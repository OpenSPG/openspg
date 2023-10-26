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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


public class GraphConfiguration implements Serializable {

    /**
     * Description of graph instance.
     */
    @JSONField(name = "description")
    private String description;

    /**
     * Max size of graph instance (GB).
     */
    @JSONField(name = "max_size_GB")
    private Integer maxSizeGb;

    /**
     * Getter method for property <tt>description</tt>.
     *
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter method for property <tt>description</tt>.
     *
     * @param description value to be assigned to property description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter method for property <tt>maxSizeGb</tt>.
     *
     * @return property value of maxSizeGb
     */
    public Integer getMaxSizeGb() {
        return maxSizeGb;
    }

    /**
     * Setter method for property <tt>maxSizeGb</tt>.
     *
     * @param maxSizeGb value to be assigned to property maxSizeGb
     */
    public void setMaxSizeGb(Integer maxSizeGb) {
        this.maxSizeGb = maxSizeGb;
    }
}
