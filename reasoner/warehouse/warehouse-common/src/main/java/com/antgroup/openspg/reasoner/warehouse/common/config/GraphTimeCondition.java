/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.warehouse.common.config;

import com.google.common.base.Splitter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author donghai.ydh
 * @version GraphTimeCondition.java, v 0.1 2024-05-11 09:50 donghai.ydh
 */
@Data
public class GraphTimeCondition implements Serializable {
  private long startMs;
  private long endMs;

  public GraphTimeCondition(String configStr) {
    List<Long> msList = new ArrayList<>(2);
    Splitter.on(",")
        .split(configStr)
        .forEach(
            s -> {
              s = s.trim();
              if (StringUtils.isEmpty(s) || "null".equals(s.toLowerCase(Locale.ROOT))) {
                msList.add(null);
                return;
              }
              msList.add(Long.parseLong(s));
            });
    this.startMs = msList.get(0);
    this.endMs = msList.get(1);
  }
}
