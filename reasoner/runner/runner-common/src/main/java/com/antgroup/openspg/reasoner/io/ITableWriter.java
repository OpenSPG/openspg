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

package com.antgroup.openspg.reasoner.io;

import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;

public interface ITableWriter {
  /** open a table writer */
  void open(int taskIndex, int parallel, AbstractTableInfo tableInfo);

  /** write a row data into table */
  void write(Object[] data);

  /** close writer, commit data */
  void close();

  /** get write count */
  long writeCount();
}
