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

package com.antgroup.openspg.cloudext.interfaces.searchengine;

import com.antgroup.openspg.common.model.datasource.connection.SearchEngineConnectionInfo;
import com.antgroup.openspg.common.model.exception.CloudExtException;
import com.antgroup.openspg.common.util.DriverManagerUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class SearchEngineClientDriverManager {

    private final static CopyOnWriteArrayList<SearchEngineClientDriver>
        registeredDrivers = new CopyOnWriteArrayList<>();

    private SearchEngineClientDriverManager() {

    }

    static {
        DriverManagerUtils.loadDrivers(
            "cloudext.searchengine.drivers", SearchEngineClientDriver.class);
        log.info("search-engine DriverManager initialized");
    }

    public static synchronized void registerDriver(SearchEngineClientDriver driver) {
        if (driver != null) {
            registeredDrivers.addIfAbsent(driver);
        } else {
            throw new NullPointerException();
        }
        log.info("registerDriver: {}", driver);
    }

    public static SearchEngineClient getClient(SearchEngineConnectionInfo config) {
        for (SearchEngineClientDriver driver : registeredDrivers) {
            if (driver.acceptsConfig(config)) {
                return driver.connect(config);
            }
        }
        throw CloudExtException.driverNotExist(config);
    }
}
