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

package com.antgroup.openspg.reasoner.thinker.logic.graph;

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException;
import java.io.Serializable;

public abstract class Element implements Serializable {
  public static final Element ANY = new Any();

  protected static final int HASH_ANY = 28;

  public boolean matches(Element other) {
    return equals(other);
  }

  public Element bind(Element pattern) {
    return this;
  }

  public String alias() {
    throw new UnsupportedOperationException(
        this.getClass().getSimpleName() + " cannot support", null);
  }

  public String shortString() {
    return toString();
  }
}
