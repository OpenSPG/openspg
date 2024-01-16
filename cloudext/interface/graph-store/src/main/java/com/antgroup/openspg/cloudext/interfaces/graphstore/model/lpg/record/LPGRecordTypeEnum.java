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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record;

/**
 * Differentiates between {@link VertexRecord VertexRecord} and {@link EdgeRecord EdgeRecord} in the
 * labeled property graph<tt>(LPG)</tt>.
 */
public enum LPGRecordTypeEnum {
  VERTEX,
  EDGE,
}
