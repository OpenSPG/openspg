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

package com.antgroup.openspg.core.spgschema.service.predicate.repository;

import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;

import java.util.List;

/**
 * The read-write interface for constraint in the database, provides methods for saving, updating, deleting, and
 * querying constraints.
 */
public interface ConstraintRepository {

    /**
     * Save or update a constraint in db.
     *
     * @param constraint constraint detail
     * @return record count that added or update
     */
    int upsert(Constraint constraint);

    /**
     * Delete a constraint by id.
     *
     * @param id constraint id
     * @return record count that deleted
     */
    int deleteById(Long id);

    /**
     * Query constraint by id.
     *
     * @param constraintIds list of constraint id
     * @return list of constraint
     */
    List<Constraint> queryById(List<Long> constraintIds);

    /**
     * Delete constraint by id
     *
     * @param ids list of id
     * @return record cnt that deleted
     */
    int deleteById(List<Long> ids);
}
