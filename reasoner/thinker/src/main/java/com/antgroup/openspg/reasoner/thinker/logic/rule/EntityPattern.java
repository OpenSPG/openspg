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

package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.Objects;

public class EntityPattern implements ClauseEntry {
  private Entity entity;

  public EntityPattern() {}

  public EntityPattern(Entity entity) {
    this.entity = entity;
  }

  /**
   * Getter method for property <tt>entity</tt>.
   *
   * @return property value of entity
   */
  public Entity getEntity() {
    return entity;
  }

  /**
   * Setter method for property <tt>entity</tt>.
   *
   * @param entity value to be assigned to property entity
   */
  public void setEntity(Entity entity) {
    this.entity = entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EntityPattern)) {
      return false;
    }
    EntityPattern that = (EntityPattern) o;
    return Objects.equals(entity, that.entity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entity);
  }

  @Override
  public Triple toTriple() {
    return Triple.create(entity);
  }
}
