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

import java.time.LocalDateTime;
import org.dozer.DozerConverter;

public class LocalDateTimeToLocalDateTimeDozerConverter
    extends DozerConverter<LocalDateTime, LocalDateTime> {

  public LocalDateTimeToLocalDateTimeDozerConverter() {
    super(LocalDateTime.class, LocalDateTime.class);
  }

  @Override
  public LocalDateTime convertTo(LocalDateTime source, LocalDateTime destination) {
    return source;
  }

  @Override
  public LocalDateTime convertFrom(LocalDateTime source, LocalDateTime destination) {
    return source;
  }
}
