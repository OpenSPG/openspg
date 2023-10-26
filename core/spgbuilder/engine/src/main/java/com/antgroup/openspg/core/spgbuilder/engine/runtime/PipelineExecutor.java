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

package com.antgroup.openspg.core.spgbuilder.engine.runtime;

import com.antgroup.openspg.core.spgbuilder.engine.physical.PhysicalPlan;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.Pipeline;


public interface PipelineExecutor {

    /**
     * Transform the logical execution plan into a physical execution plan.
     *
     * @param pipeline: The pipeline of the logical execution plan.
     * @return Physical execution plan.
     */
    PhysicalPlan plan(Pipeline pipeline);

    /**
     * Execute the physical execution plan, traversing and executing each component.
     *
     * @param plan:    Physical execution plan.
     * @param context: Runtime parameters, such as job info, schema address, etc.
     */
    void execute(PhysicalPlan plan, RuntimeContext context);
}
