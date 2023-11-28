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

package com.antgroup.openspg.common.model.base;

import com.antgroup.openspg.common.model.base.BaseToString;

/**
 * Top-level parent class for domain models.
 *
 * <p>The code follows Domain-Driven Design (DDD) domain design specifications. All domain model
 * classes inherit from BaseModel. BaseModel inherit from BaseToString and provides a unified
 * toString method for all subclasses. In the future, some basic methods can also be added to this
 * class.
 */
public abstract class BaseModel extends BaseToString {

  private static final long serialVersionUID = 4683437081011010505L;
}
