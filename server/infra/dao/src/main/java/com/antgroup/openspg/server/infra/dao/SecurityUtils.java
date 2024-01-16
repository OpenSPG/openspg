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

package com.antgroup.openspg.server.infra.dao;

import java.util.regex.Pattern;

public class SecurityUtils {

  private static final Pattern PATTERN = Pattern.compile("[^a-zA-Z0-9_\\s+]");

  public static String trimSql4OrderBy(String orderBy) {
    return PATTERN.matcher(orderBy).replaceAll("");
  }
}
