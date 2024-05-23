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

package com.antgroup.openspg.reasoner.thinker.qlexpress.op;

import com.antgroup.openspg.reasoner.udf.rule.op.OperatorEqualsLessMore;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Custom comparison operator logic:
 *
 * 1. When comparing strings with numerical types, convert the string to a number.
 * 2. Support evaluation of expressions of the type ">150" > 150.
 */
public class RichOperatorEqualsLessMore extends OperatorEqualsLessMore {
  private static final String reg = "[<>]?\\s*\\d+(\\.\\d+)?";
  private Pattern pattern;

  public RichOperatorEqualsLessMore(String aName) {
    super(aName);
    pattern = Pattern.compile(reg);
  }

  public RichOperatorEqualsLessMore(String aAliasName, String aName, String aErrorInfo) {
    super(aAliasName, aName, aErrorInfo);
    pattern = Pattern.compile(reg);
  }

  @Override
  public Object executeInner(Object[] list) throws Exception {
    return executeInner(list[0], list[1]);
  }

  @Override
  public Object executeInner(Object op1, Object op2) {
    if (op1 instanceof Number && op2 instanceof String) {
      String value = getValue((String) op2);
      if (value == null) {
        return null;
      } else if (NumberUtils.isCreatable(value)) {
        return executeInner(this.name, op1, toNumber((String) op2, op1.getClass()));
      } else {
        Number num = toNumber(value.substring(1, value.length()), op1.getClass());
        Pair<Number, Number> right = buildRange(value.substring(0, 1), num);
        Pair<Number, Number> left = buildRange(this.name, (Number) op1);
        if (left == null || right == null) {
          return OperatorEqualsLessMore.executeInner(this.name, op1, op2);
        } else {
          return executeInner(left, right);
        }
      }
    } else if (op1 instanceof String && op2 instanceof Number) {
      String value = getValue((String) op1);
      if (value == null) {
        return null;
      } else if (NumberUtils.isCreatable(value)) {
        return executeInner(this.name, toNumber((String) op1, op2.getClass()), op2);
      } else {
        Number num = toNumber(value.substring(1, value.length()), op2.getClass());
        Pair<Number, Number> right = buildRange(value.substring(0, 1), num);
        Pair<Number, Number> left = buildRange(this.name, (Number) op2);
        if (left == null || right == null) {
          return OperatorEqualsLessMore.executeInner(this.name, op1, op2);
        } else {
          return executeInner(left, right);
        }
      }
    } else {
      return OperatorEqualsLessMore.executeInner(this.name, op1, op2);
    }
  }

  private Pair<Number, Number> buildRange(String opName, Number op) {
    if (">=".equals(opName) || ">".equals(opName)) {
      return new ImmutablePair<>(op, Long.MAX_VALUE);
    } else if ("<=".equals(opName) || "<".equals(opName)){
      return new ImmutablePair<>(Long.MIN_VALUE, op);
    } else {
      return null;
    }
  }


  private Boolean executeInner(Pair<Number, Number> left, Pair<Number, Number> right) {
    if (OperatorEqualsLessMore.executeInner(">", left.getLeft(), right.getRight()) || OperatorEqualsLessMore.executeInner("<", left.getRight(), right.getLeft())) {
      return false;
    } else if (OperatorEqualsLessMore.executeInner("<=", left.getLeft(), right.getLeft()) && OperatorEqualsLessMore.executeInner(">=", left.getRight(), right.getRight())) {
      return true;
    } else {
      return null;
    }
  }


  private String getValue(String content) {
    Matcher matcher = pattern.matcher(content);
    if (matcher.find()) {
      return matcher.group();
    } else {
      return null;
    }
  }


  private Number toNumber(String op, Class<?> numberClass) {
    int type = getSeq(numberClass);
    if (NUMBER_TYPE_BYTE == type) {
      return Byte.valueOf(op);
    }
    if (NUMBER_TYPE_SHORT == type) {
      return Short.valueOf(op);
    }
    if (NUMBER_TYPE_INT == type) {
      return Integer.valueOf(op);
    }
    if (NUMBER_TYPE_LONG == type) {
      return Long.valueOf(op);
    }
    if (NUMBER_TYPE_FLOAT == type) {
      return Float.valueOf(op);
    }
    if (NUMBER_TYPE_DOUBLE == type) {
      return Double.valueOf(op);
    }
    if (NUMBER_TYPE_DECIMAL == type) {
      return new BigDecimal(op);
    }
    return null;
  }

}
