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
