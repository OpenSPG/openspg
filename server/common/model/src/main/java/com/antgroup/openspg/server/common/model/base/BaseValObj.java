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

package com.antgroup.openspg.server.common.model.base;

/**
 * Top-level parent class for domain model value objects.
 *
 * <p>Similar to the {@link com.antgroup.openspg.server.common.model.base.BaseModel} class,All
 * domain model value objects inherit from BaseValObj. BaseValObj inherit from BaseToString and
 * provides a unified toString method for all subclasses. Some basic methods can also be added to
 * this class in the future.
 */
public abstract class BaseValObj extends BaseToString {

  private static final long serialVersionUID = 2364139127352876887L;
}
