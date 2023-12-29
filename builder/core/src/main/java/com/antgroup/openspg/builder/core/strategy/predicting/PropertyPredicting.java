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

package com.antgroup.openspg.builder.core.strategy.predicting;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PredictingException;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import java.util.List;

public interface PropertyPredicting {

  /** Initialize property linking strategy. */
  void init(BuilderContext context) throws BuilderException;

  /** Input an SPG property record and predict the property value. */
  List<BaseAdvancedRecord> predicting(BaseAdvancedRecord record) throws PredictingException;
}
