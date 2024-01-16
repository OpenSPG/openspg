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

package com.antgroup.openspg.server.api.http.server;

import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import org.springframework.http.ResponseEntity;

public interface HttpBizCallback<T> {

  /**
   * Perform some pre-validation, and if the validation fails, throw an {@link
   * IllegalParamsException} exception directly.
   */
  void check();

  /**
   * Execute the specific business logic.
   *
   * @return The result of executing the business logic.
   */
  T action();

  /** Return the corresponding ResponseEntity based on the action result. */
  default ResponseEntity<Object> response(T t) {
    if (t == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(t);
  }
}
