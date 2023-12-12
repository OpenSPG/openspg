/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.warehouse.common.config;

import java.io.Serializable;


public class StartVertexConfig implements Serializable {
    private String vertexTypeName;
    private String bizId;
    private String aliasName;
    private int maxDepth;

    /**
     * getter
     * @return
     */
    public String getVertexTypeName() {
        return vertexTypeName;
    }

    /**
     * setter
     * @param vertexTypeName
     */
    public void setVertexTypeName(String vertexTypeName) {
        this.vertexTypeName = vertexTypeName;
    }

    /**
     * getter
     * @return
     */
    public String getBizId() {
        return bizId;
    }

    /**
     * setter
     * @param bizId
     */
    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    /**
     * getter
     * @return
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * setter
     * @param aliasName
     */
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * getter
     * @return
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * setter
     * @param maxDepth
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}