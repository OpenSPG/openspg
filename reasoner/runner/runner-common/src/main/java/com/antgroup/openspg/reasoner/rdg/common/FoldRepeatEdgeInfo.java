/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author donghai.ydh
 * @version FoldRepeatEdgeInfo.java, v 0.1 2023年10月31日 14:44 donghai.ydh
 */
public class FoldRepeatEdgeInfo implements Serializable {
    private final String fromEdgeAlias;
    private final String toEdgeAlias;
    private final String fromVertexAlias;
    private final String toVertexAlias;

    public FoldRepeatEdgeInfo(String fromEdgeAlias, String toEdgeAlias, String fromVertexAlias, String toVertexAlias) {
        if (StringUtils.isEmpty(fromEdgeAlias) || StringUtils.isEmpty(toEdgeAlias)
                || StringUtils.isEmpty(fromVertexAlias) || StringUtils.isEmpty(toEdgeAlias)) {
            throw new IllegalArgumentException("no empty string", "", "", null);
        }
        this.fromEdgeAlias = fromEdgeAlias;
        this.toEdgeAlias = toEdgeAlias;
        this.fromVertexAlias = fromVertexAlias;
        this.toVertexAlias = toVertexAlias;
    }

    /**
     * Getter method for property <tt>fromEdgeAlias</tt>.
     *
     * @return property value of fromEdgeAlias
     */
    public String getFromEdgeAlias() {
        return fromEdgeAlias;
    }

    /**
     * Getter method for property <tt>toEdgeAlias</tt>.
     *
     * @return property value of toEdgeAlias
     */
    public String getToEdgeAlias() {
        return toEdgeAlias;
    }

    /**
     * Getter method for property <tt>fromVertexAlias</tt>.
     *
     * @return property value of fromVertexAlias
     */
    public String getFromVertexAlias() {
        return fromVertexAlias;
    }

    /**
     * Getter method for property <tt>toVertexAlias</tt>.
     *
     * @return property value of toVertexAlias
     */
    public String getToVertexAlias() {
        return toVertexAlias;
    }
}