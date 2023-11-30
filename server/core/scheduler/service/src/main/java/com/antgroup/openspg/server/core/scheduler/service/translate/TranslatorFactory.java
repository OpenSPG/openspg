/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.translate;

import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Translator Factory
 *
 * @author yangjin
 * @Title: TranslatorFactory.java
 * @Description:
 * @date 2022/3/11 16:39
 */
public class TranslatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslatorFactory.class);

    private static final String TRANSLATE = "Translate";

    /**
     * Translate
     *
     * @param type
     * @return
     */
    public static Translate getTranslator(String type) {
        String translator = getTranslatorNameByType(type);
        Translate dagTranslate = SpringContextHolder.getBean(translator, Translate.class);
        if (dagTranslate == null) {
            LOGGER.error(String.format("getTranslator bean error type:%s", type));
            throw new RuntimeException("not find bean type:" + type);
        }
        return dagTranslate;
    }

    private static String getTranslatorNameByType(String type) {
        return type + TRANSLATE;
    }
}