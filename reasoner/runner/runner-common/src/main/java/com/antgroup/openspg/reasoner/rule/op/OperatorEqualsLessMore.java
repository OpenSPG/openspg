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

package com.antgroup.openspg.reasoner.rule.op;

import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Optimize qlexpress code, return false when exception
 * ==,>,>=,<,<=,!=
 */
public class OperatorEqualsLessMore extends Operator {
    public OperatorEqualsLessMore(String aName) {
        this.name = aName;
    }

    public OperatorEqualsLessMore(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) {
        return executeInner(this.name, op1, op2);
    }

    public static boolean executeInner(String opStr, Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return "==".equals(opStr);
        } else if (obj1 == null || obj2 == null) {
            if ("==".equals(opStr)) {
                return false;
            } else if ("!=".equals(opStr) || "<>".equals(opStr)) {
                return true;
            } else if (">".equals(opStr) || "<".equals(opStr) || ">=".equals(opStr) || "<=".equals(opStr)) {
                return false;
            }
        }

        Integer i = compareDataWithoutException(obj1, obj2);
        if (i == null) {
            return false;
        }
        boolean result;
        if (i > 0) {
            result = ">".equals(opStr) || ">=".equals(opStr) || "!=".equals(opStr) || "<>".equals(opStr);
        } else if (i == 0) {
            result = "==".equals(opStr) || ">=".equals(opStr) || "<=".equals(opStr);
        } else {
            result = "<".equals(opStr) || "<=".equals(opStr) || "!=".equals(opStr) || "<>".equals(opStr);
        }
        return result;
    }

    public static Integer compareDataWithoutException(Object op1, Object op2) {
        Integer compareResult = -1;
        if (op1 instanceof String) {
            compareResult = ((String) op1).compareTo(op2.toString());
        } else if (op2 instanceof String) {
            compareResult = op1.toString().compareTo((String) op2);
        } else if (op1 instanceof Character && op2 instanceof Character) {
            compareResult = ((Character) op1).compareTo((Character) op2);
        } else if (op1 instanceof Number && op2 instanceof Number) {
            // Make a numerical comparison
            compareResult = compareNumber((Number) op1, (Number) op2);
        } else if ((op1 instanceof Boolean) && (op2 instanceof Boolean)) {
            if (((Boolean) op1).booleanValue() == ((Boolean) op2).booleanValue()) {
                compareResult = 0;
            } else {
                compareResult = -1;
            }
        } else if ((op1 instanceof Date) && (op2 instanceof Date)) {
            compareResult = ((Date) op1).compareTo((Date) op2);
        } else if ((op1 instanceof Comparable)) {
            compareResult = ((Comparable) op1).compareTo(op2);
        } else if ((op2 instanceof Comparable)) {
            compareResult = -((Comparable) op2).compareTo(op1);
        } else {
            return null;
        }
        return compareResult;

    }

    private static final int NUMBER_TYPE_BYTE    = 1;
    private static final int NUMBER_TYPE_SHORT   = 2;
    private static final int NUMBER_TYPE_INT     = 3;
    private static final int NUMBER_TYPE_LONG    = 4;
    private static final int NUMBER_TYPE_FLOAT   = 5;
    private static final int NUMBER_TYPE_DOUBLE  = 6;
    private static final int NUMBER_TYPE_DECIMAL = 7;

    public static int getSeq(Class<?> aClass) {
        if (aClass == Byte.class || aClass == byte.class) {return NUMBER_TYPE_BYTE;}
        if (aClass == Short.class || aClass == short.class) {return NUMBER_TYPE_SHORT;}
        if (aClass == Integer.class || aClass == int.class) {return NUMBER_TYPE_INT;}
        if (aClass == Long.class || aClass == long.class) {return NUMBER_TYPE_LONG;}
        if (aClass == Float.class || aClass == float.class) {return NUMBER_TYPE_FLOAT;}
        if (aClass == Double.class || aClass == double.class) {return NUMBER_TYPE_DOUBLE;}
        if (aClass == BigDecimal.class) {return NUMBER_TYPE_DECIMAL;}
        throw new NotImplementedException("unsupported data type:" + aClass.getName(), null);
    }

    public static Integer compareNumber(Number op1, Number op2) {
        int type1 = getSeq(op1.getClass());
        int type2 = getSeq(op2.getClass());
        int type = Math.max(type1, type2);
        if (NUMBER_TYPE_BYTE == type) {
            byte o1 = op1.byteValue();
            byte o2 = op2.byteValue();
            return Byte.compare(o1, o2);
        }
        if (NUMBER_TYPE_SHORT == type) {
            short o1 = op1.shortValue();
            short o2 = op2.shortValue();
            return Short.compare(o1, o2);
        }
        if (NUMBER_TYPE_INT == type) {
            int o1 = op1.intValue();
            int o2 = op2.intValue();
            return Integer.compare(o1, o2);
        }
        if (NUMBER_TYPE_LONG == type) {
            long o1 = op1.longValue();
            long o2 = op2.longValue();
            return Long.compare(o1, o2);
        }
        if (NUMBER_TYPE_FLOAT == type) {
            float o1 = op1.floatValue();
            float o2 = op2.floatValue();
            return Float.compare(o1, o2);
        }
        if (NUMBER_TYPE_DOUBLE == type) {
            double o1 = op1.doubleValue();
            double o2 = op2.doubleValue();
            return Double.compare(o1, o2);
        }
        if (NUMBER_TYPE_DECIMAL == type) {
            BigDecimal o1 = new BigDecimal(op1.toString());
            BigDecimal o2 = new BigDecimal(op2.toString());
            return o1.compareTo(o2);
        }
        return null;
    }
}
