/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct;

import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGDataQueryService;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Provides a generic base implementation for <tt>LPGRecordStruct</tt>. <tt>LPGRecordStruct</tt>
 * constants a set of LPG records, and is used for the query response by {@link LPGDataQueryService
 * LPGDataQueryService}. All types of <tt>LPGRecordStruct</tt> are the following:
 *
 * <ul>
 *   <li><code>TABLE</code>
 *   <li><code>GRAPH</code>
 * </ul>
 */
@Getter
@AllArgsConstructor
public abstract class BaseLPGRecordStruct extends BaseValObj {

  private final LPGRecordStructEnum recordStruct;
}
