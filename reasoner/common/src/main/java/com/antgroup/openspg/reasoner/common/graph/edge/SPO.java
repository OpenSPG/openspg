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

package com.antgroup.openspg.reasoner.common.graph.edge;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chengqiang.cq
 * @version $Id: SPO.java, v 0.1 2023-02-08 19:02 chengqiang.cq Exp $$
 */
public class SPO implements Serializable {
  public static final String SPLITTER = "_";

  private final String s;

  private final String p;

  private final String o;

  /** spo */
  public SPO(String spo) {
    if (null == spo) {
      this.s = null;
      this.p = null;
      this.o = null;
      return;
    }
    // spo format like 'subject_predicate_object'
    List<String> parts = Lists.newArrayList(Splitter.on(SPLITTER).split(spo));
    int length = parts.size();
    if (length < 3) {
      throw new RuntimeException("spo " + spo + " error");
    }
    // subject always first
    this.s = parts.get(0);
    // object always last
    this.o = parts.get(length - 1);
    this.p = StringUtils.join(parts.subList(1, length - 1), SPLITTER);
  }

  public SPO(String s, String p, String o) {
    this.s = s;
    this.p = p;
    this.o = o;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(s).append(SPLITTER).append(p).append(SPLITTER).append(o);
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(s, p, o);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof SPO)) {
      return false;
    }
    return toString().equals(obj.toString());
  }

  /**
   * Getter method for property <tt>s</tt>.
   *
   * @return property value of s
   */
  public String getS() {
    return s;
  }

  /**
   * Getter method for property <tt>p</tt>.
   *
   * @return property value of p
   */
  public String getP() {
    return p;
  }

  /**
   * Getter method for property <tt>o</tt>.
   *
   * @return property value of o
   */
  public String getO() {
    return o;
  }
}
