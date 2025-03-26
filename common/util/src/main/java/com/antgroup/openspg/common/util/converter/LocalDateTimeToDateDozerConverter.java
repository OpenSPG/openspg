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
import java.time.ZoneId;
import java.util.Date;
import org.dozer.DozerConverter;

public class LocalDateTimeToDateDozerConverter extends DozerConverter<LocalDateTime, Date> {

  public LocalDateTimeToDateDozerConverter() {
    super(LocalDateTime.class, Date.class);
  }

  @Override
  public LocalDateTime convertFrom(Date source, LocalDateTime destination) {
    return source == null
        ? null
        : LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
  }

  @Override
  public Date convertTo(LocalDateTime source, Date destination) {
    return source == null ? null : Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
  }
}
