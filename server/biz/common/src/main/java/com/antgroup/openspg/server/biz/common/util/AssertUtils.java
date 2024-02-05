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

package com.antgroup.openspg.server.biz.common.util;

import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class AssertUtils {

  /**
   * Asserts that an object is not null
   *
   * @param objectName The name of the object
   * @param object The object to be validated
   */
  public static void assertParamObjectIsNotNull(String objectName, Object object) {
    if (null == object) {
      throw new IllegalParamsException(objectName + " is null");
    }
  }

  /**
   * Asserts that an string is not an empty string
   *
   * @param objectName The name of the object
   * @param object The object to be validated
   */
  public static void assertParamStringIsNotBlank(String objectName, String object) {
    if (StringUtils.isBlank(object)) {
      throw new IllegalParamsException(objectName + " is blank");
    }
  }

  /**
   * Asserts that a string is an empty string
   *
   * @param objectName The name of the object
   * @param object The object to be validated
   */
  public static void assertParamStringIsBlank(String objectName, String object) {
    if (StringUtils.isNotBlank(object)) {
      throw new IllegalParamsException(objectName + " is not blank");
    }
  }

  /**
   * Asserts that an object is true.
   *
   * @param objectName The name of the object
   * @param object The object to be validated
   */
  public static void assertParamIsTrue(String objectName, boolean object) {
    if (!object) {
      throw new IllegalParamsException(objectName + " is false");
    }
  }

  /**
   * Asserts that a collection is not empty.
   *
   * @param objectName The name of the collection
   * @param collection The collection to be validated
   */
  public static void assertParamCollectionIsNotEmpty(String objectName, Collection<?> collection) {
    if (CollectionUtils.isEmpty(collection)) {
      throw new IllegalParamsException(objectName + " is empty");
    }
    collection.forEach(e -> assertParamObjectIsNotNull(objectName, e));
  }

  /**
   * Asserts that the length of a collection is less than a given size
   *
   * @param objectName The name of the collection
   * @param collection The collection to be validated
   */
  public static void assertParamCollectionMustBeLessThan(
      String objectName, Collection<?> collection, int size) {
    if (size == 0 && CollectionUtils.isEmpty(collection)) {
      return;
    }
    if (CollectionUtils.isNotEmpty(collection) && collection.size() > size) {
      throw new IllegalParamsException(objectName + "'s size must be <=" + size);
    }
  }

  /**
   * Asserts that a number is less than or equal to a maximum value
   *
   * @param objectName The name of the object
   * @param object The object to be validated
   * @param max The maximum value
   */
  public static void assertIntegerIsLessThan(String objectName, Integer object, int max) {
    if (object == null) {
      throw new IllegalParamsException(objectName + " is null");
    }
    if (object > max) {
      throw new IllegalParamsException(objectName + " must <= " + max);
    }
  }

  /**
   * Asserts that a number is greater than or equal to a minimum value.
   *
   * @param objectName The name of the object
   * @param object The object to be validated
   * @param min The minimum value
   */
  public static void assertLongIsGreaterThan(String objectName, Long object, Long min) {
    if (object == null) {
      throw new IllegalParamsException(objectName + " is null");
    }
    if (object < min) {
      throw new IllegalParamsException(objectName + " must >= " + min);
    }
  }

  /**
   * Asserts that a number is within a given range, where the minimum value is less than or equal to
   * the number and the maximum value is greater than or equal to the number.
   *
   * @param objectName The name of the object
   * @param object The object to be validated
   * @param min The minimum value
   * @param max The maximum value
   */
  public static void assertIntegerIsBetween(String objectName, Integer object, int min, int max) {
    if (object == null) {
      throw new IllegalParamsException(objectName + " is null");
    }
    if (object > max || object < min) {
      throw new IllegalParamsException(objectName + " must <= " + max + " and >=" + min);
    }
  }
}
