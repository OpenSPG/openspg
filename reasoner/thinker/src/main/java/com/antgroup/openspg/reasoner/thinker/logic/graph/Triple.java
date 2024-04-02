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

import lombok.Data;

@Data
public class Triple implements Element {
  private Element subject;
  private Element predicate;
  private Element object;

  public Triple() {}

  public Triple(Element subject, Element predicate, Element object) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }
}
