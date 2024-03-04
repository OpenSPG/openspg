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

package com.antgroup.openspg.reasoner.udf.impl;

import java.util.Locale;

public class UdfName {
  private final String name;

  public UdfName(String name) {
    this.name = name;
  }

  public static UdfName from(String name) {
    return new UdfName(name);
  }

  @Override
  public int hashCode() {
    return this.name.toLowerCase(Locale.ROOT).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UdfName)) {
      return false;
    }
    UdfName that = (UdfName) obj;
    return this.name.toLowerCase(Locale.ROOT).equals(that.name.toLowerCase(Locale.ROOT));
  }

  @Override
  public String toString() {
    return this.name;
  }
}
