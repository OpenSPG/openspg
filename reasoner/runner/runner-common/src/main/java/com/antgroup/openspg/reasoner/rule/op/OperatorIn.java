/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rule.op;

import com.ql.util.express.Operator;

import java.lang.reflect.Array;
import java.util.List;

/**
 * in op without exception
 *
 * @author chengqiang.cq
 * @version $Id: OperatorIn.java, v 0.1 2022-11-22 14:24 chengqiang.cq Exp $$
 */
public class OperatorIn extends Operator {
    public OperatorIn(String aName) {
        this.name = aName;
    }

    public OperatorIn(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        Object obj = list[0];
        if (obj == null) {
            // object is null, can not call method
            return false;
        } else if (!((obj instanceof Number) || (obj instanceof String))) {
            return false;
        } else if (list.length == 2 && (list[1].getClass().isArray() || list[1] instanceof List)) {
            if (obj.equals(list[1])) {
                return true;
            } else if (list[1].getClass().isArray()) {
                int len = Array.getLength(list[1]);
                for (int i = 0; i < len; i++) {
                    boolean f = OperatorEqualsLessMore.executeInner("==", obj,
                            Array.get(list[1], i));
                    if (f) {
                        return Boolean.TRUE;
                    }
                }
            } else {
                @SuppressWarnings("unchecked")
                List<Object> array = (List<Object>) list[1];
                for (Object o : array) {
                    boolean f = OperatorEqualsLessMore.executeInner("==", obj, o);
                    if (f) {
                        return Boolean.TRUE;
                    }
                }
            }
            return false;
        } else if (list.length == 2 && obj instanceof String && list[1] instanceof String) {
            return ((String) list[1]).contains(String.valueOf(obj));
        } else {
            for (int i = 1; i < list.length; i++) {
                boolean f = OperatorEqualsLessMore.executeInner("==", obj, list[i]);
                if (f) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    }
}