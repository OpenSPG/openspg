/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.catalog;

import java.util.Map;

import com.antgroup.openspg.reasoner.parser.KgDslParser;
import org.apache.commons.lang3.StringUtils;

import com.antgroup.openspg.reasoner.catalog.impl.KGCatalog;
import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.utils.SimpleObjSerde;

import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "userlogger")
public class CatalogFactory {

    /**
     * create catalog
     *
     * @param params
     * @return
     */
    public static Catalog createCatalog(Map<String, Object> params) {
        return createCatalog(new KgDslParser(), params, KgSchemaConnectionInfo.DEFAULT_CONNECTION_INFO());
    }

    public static Catalog createCatalog(Map<String, Object> params,
                                        KgSchemaConnectionInfo connInfo) {
        return createCatalog(new KgDslParser(), params, connInfo);
    }

    public static Catalog createCatalog(ParserInterface parser, Map<String, Object> params,
                                        KgSchemaConnectionInfo connInfo) {
        String catalogStr = (String) params.get(ConfigKey.KG_REASONER_CATALOG);
        if (StringUtils.isNotEmpty(catalogStr)) {
            return (Catalog) SimpleObjSerde.de(catalogStr);
        }

        if (connInfo == null) {
            connInfo = KgSchemaConnectionInfo.DEFAULT_CONNECTION_INFO();
        }
        Catalog catalog = new KGCatalog(
                Long.parseLong(String.valueOf(params.getOrDefault("projId", 0L))), connInfo);
        catalog.init();
        return catalog;
    }
}