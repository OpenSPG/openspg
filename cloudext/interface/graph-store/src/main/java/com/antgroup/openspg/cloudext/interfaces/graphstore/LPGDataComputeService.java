/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
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
