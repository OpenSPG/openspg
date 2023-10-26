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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.core.spgschema.model.type.BasicTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * Provides a generic base implementation for <tt>LPGOntology</tt>.
 * <tt>LPGOntology</tt> is defined to contain some {@link LPGProperty LPGProperties}, and provides methods
 * to check if LPG property is existed. All types of <tt>LPGOntology</tt> are the following:
 *     <ul>
 *         <li><code>VertexType</code></li>
 *         <li><code>EdgeType</code></li>
 *     </ul>
 * </P>
 */
@Getter
@AllArgsConstructor
public abstract class BaseLPGOntology extends BaseValObj {

    private final LPGOntologyTypeEnum type;

    /**
     * Map of properties, whose key is name of {@link LPGProperty LPGProperty}, and value is
     * {@link LPGProperty LPGProperty}.
     */
    protected final Map<String, LPGProperty> properties;

    /**
     * Get type name of this <tt>LPGOntology</tt>.
     *
     * @return the type name of this <tt>LPGOntology</tt>
     */
    public abstract String getTypeName();

    /**
     * <p>
     * Check if this <tt>LPGOntology</tt> is with definition of {@link LPGProperty LPGProperty} by property's name.
     * </P>
     *
     * @param propertyName the name of property
     * @return <code>true</code> if this <tt>LPGOntology</tt> is with definition of {@link LPGProperty LPGProperty},
     * <code>false</code> otherwise
     */
    public boolean isWithProperty(String propertyName) {
        if (MapUtils.isEmpty(properties)) {
            return false;
        }
        return properties.containsKey(propertyName);
    }

    /**
     * <p>
     * Check if this <tt>LPGOntology</tt> is with definition of {@link LPGProperty LPGProperty} by property's name.
     * </P>
     *
     * @param propertyName the name of property
     * @param propertyType the type of property
     * @return <code>true</code> if this <tt>LPGOntology</tt> is with definition of {@link LPGProperty LPGProperty},
     * <code>false</code> otherwise
     */
    public boolean isWithProperty(String propertyName, BasicTypeEnum propertyType) {
        if (!isWithProperty(propertyName)) {
            return false;
        }
        return Objects.equals(properties.get(propertyName).getType(), propertyType);
    }
}
