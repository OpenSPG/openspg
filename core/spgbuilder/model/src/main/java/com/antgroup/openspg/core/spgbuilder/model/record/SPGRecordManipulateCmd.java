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

package com.antgroup.openspg.core.spgbuilder.model.record;

import com.antgroup.openspg.common.model.base.BaseCmd;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

public class SPGRecordManipulateCmd extends BaseCmd {

  private final List<SPGRecordAlterItem> alterItems;

  public SPGRecordManipulateCmd(SPGRecordAlterItem alterItem) {
    this(Lists.newArrayList(alterItem));
  }

  public SPGRecordManipulateCmd(List<SPGRecordAlterItem> alterItems) {
    this.alterItems = new ArrayList<>(alterItems);
  }

  public List<SPGRecordAlterItem> getAlterItems() {
    return alterItems;
  }
}
