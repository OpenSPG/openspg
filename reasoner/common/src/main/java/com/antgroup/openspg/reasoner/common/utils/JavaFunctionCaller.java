/** Alipay.com Inc. Copyright (c) 2004-2024 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.utils;
/**
 * @author peilong.zpl
 * @version $Id: FunctionUtils.java, v 0.1 2024-03-19 21:42 peilong.zpl Exp $$
 */
@FunctionalInterface
public interface JavaFunctionCaller<T, R> {
  R apply(T t);
}
