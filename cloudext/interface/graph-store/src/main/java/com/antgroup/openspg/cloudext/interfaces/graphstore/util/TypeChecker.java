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

package com.antgroup.openspg.cloudext.interfaces.graphstore.util;

import java.util.Collection;

public class TypeChecker {
  public static boolean isPrimitiveOrWrapper(Object obj) {
    if (obj == null) {
      return false;
    }
    Class<?> clazz = obj.getClass();
    return clazz.isPrimitive()
        || clazz.equals(Integer.class)
        || clazz.equals(Double.class)
        || clazz.equals(Float.class)
        || clazz.equals(Long.class)
        || clazz.equals(Short.class)
        || clazz.equals(Byte.class)
        || clazz.equals(Boolean.class)
        || clazz.equals(Character.class);
  }

  public static boolean isArrayOrCollectionOfPrimitives(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Object[]) {
      for (Object element : (Object[]) obj) {
        if (!isPrimitiveOrWrapper(element)) {
          return false;
        }
      }
      return true;
    } else if (obj instanceof Collection<?>) {
      for (Object element : (Collection<?>) obj) {
        if (!isPrimitiveOrWrapper(element)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
