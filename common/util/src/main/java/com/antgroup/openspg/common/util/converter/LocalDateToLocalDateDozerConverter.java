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
package com.antgroup.openspg.common.util.converter;

import java.time.LocalDate;
import org.dozer.DozerConverter;

public class LocalDateToLocalDateDozerConverter extends DozerConverter<LocalDate, LocalDate> {

  public LocalDateToLocalDateDozerConverter() {
    super(LocalDate.class, LocalDate.class);
  }

  @Override
  public LocalDate convertFrom(LocalDate source, LocalDate destination) {
    return source;
  }

  @Override
  public LocalDate convertTo(LocalDate source, LocalDate destination) {
    return source;
  }
}
