/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yangjin
 * @version : CommonUtils.java, v 0.1 2023年12月01日 14:19 yangjin Exp $
 */
public class CommonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * merge two bean by discovering differences
     *
     * @param dest
     * @param orig
     * @param <M>
     * @throws Exception
     */
    public static <M> M merge(M dest, M orig) {
        if (dest == null) {
            return orig;
        }
        if (orig == null) {
            return dest;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(dest.getClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (descriptor.getWriteMethod() == null) {
                    continue;
                }
                Object originalValue = descriptor.getReadMethod().invoke(orig);
                if (originalValue == null) {
                    continue;
                }
                descriptor.getWriteMethod().invoke(dest, originalValue);
            }
        } catch (Exception e) {
            LOGGER.error("merge bean exception", e);
        }
        return dest;
    }

    /**
     * content contains key
     *
     * @param content
     * @param key
     * @return
     */
    public static boolean contains(String content, String key) {
        if (StringUtils.isBlank(key)) {
            return true;
        }
        if (StringUtils.isBlank(content)) {
            return false;
        }
        if (content.contains(key)) {
            return true;
        }
        return false;
    }

    /**
     * content equals key
     *
     * @param content
     * @param key
     * @return
     */
    public static boolean equals(Object content, Object key) {
        if (key == null) {
            return true;
        }
        if (content == null) {
            return false;
        }
        if (content.equals(key)) {
            return true;
        }
        return false;
    }

    /**
     * content Date after key Date
     *
     * @param content
     * @param key
     * @return
     */
    public static boolean after(Date content, Date key) {
        if (key == null) {
            return true;
        }
        if (content == null) {
            return false;
        }
        if (content.after(key)) {
            return true;
        }
        return false;
    }

    /**
     * content Date before key Date
     *
     * @param content
     * @param key
     * @return
     */
    public static boolean before(Date content, Date key) {
        if (key == null) {
            return true;
        }
        if (content == null) {
            return false;
        }
        if (content.before(key)) {
            return true;
        }
        return false;
    }

}
