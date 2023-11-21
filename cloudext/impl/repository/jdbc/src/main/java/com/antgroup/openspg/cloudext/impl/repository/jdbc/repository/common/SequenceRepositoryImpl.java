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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.common;

import com.antgroup.openspg.cloudext.interfaces.repository.sequence.SequenceRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceRepositoryImpl implements SequenceRepository {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  @Override
  public Long getSeqIdByTime() {
    LocalDateTime localDateTime = LocalDateTime.now();
    String format = FORMATTER.format(localDateTime);
    String result = format + (int) ((Math.random() * 9 + 1) * 10000000);
    return Long.parseLong(result);
  }
}
