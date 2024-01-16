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

package com.antgroup.openspg.cloudext.interfaces.graphstore;

import com.antgroup.openspg.cloudext.interfaces.graphstore.impl.MurmurHashIdGenerator;
import com.antgroup.openspg.cloudext.interfaces.graphstore.impl.NoChangedIdGenerator;

/**
 * {@link LPGInternalIdGenerator} provides an API for generate internal id by the type name of
 * vertex and the value of vertex's ID specified by the user, and is used as an input parameter when
 * initializing a <tt>LPGGraphStoreClient</tt>.
 *
 * <p>If value of vertex's ID can be assigned as {@link java.lang.String} to store in
 * <tt>LPGEngine</tt>, just set {@link NoChangedIdGenerator NoChangedIdGenerator} into
 * <tt>LPGGraphStoreClient</tt> for the <tt>LPGEngine</tt>.
 *
 * <p>Otherwise, the custom implementation of {@link LPGInternalIdGenerator#gen
 * LPGInternalIdGenerator.gen(..)} (such as {@link MurmurHashIdGenerator#gen
 * MurmurHashIdGenerator.gen(..)}) can be used to generate value of the vertex's internal ID for
 * <tt>LPGEngine</tt>.
 */
public interface LPGInternalIdGenerator {

  /**
   * This method is used to generate value of vertex's ID that are actually stored in
   * <tt>LPGEngine</tt>.
   *
   * @param type the type name of vertex
   * @param id the value of vertex's ID specified by the user, and of type {@link java.lang.String}.
   * @return the value of vertex's ID that are actually stored in LPGEngine.
   */
  Object gen(String type, String id);
}
