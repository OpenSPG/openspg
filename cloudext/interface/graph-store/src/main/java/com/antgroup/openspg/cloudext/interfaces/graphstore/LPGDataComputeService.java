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

import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.PageRankCompete;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.ComputeResultRow;
import java.util.List;

/** Provides data compute service for <tt>LPG</tt>. */
public interface LPGDataComputeService {

  /**
   * Run the Page-Rank algorithm .
   *
   * @param compete the Page-Rank compete.
   * @return the list of compute result rows.
   */
  List<ComputeResultRow> runPageRank(PageRankCompete compete);
}
