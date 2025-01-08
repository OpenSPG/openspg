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
package com.antgroup.openspg.server.common.service.datasource.meta.client.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class JdbcClient {

  public static Connection getClient(CloudDataSource store, String driver) {
    try {
      Class.forName(driver);
      Connection connection;
      if (StringUtils.isBlank(store.getDbUser()) || StringUtils.isBlank(store.getDbPassword())) {
        connection = DriverManager.getConnection(store.getDbUrl());
      } else {
        connection =
            DriverManager.getConnection(store.getDbUrl(), store.getDbUser(), store.getDbPassword());
      }
      log.info("Success getConnection with url " + store.getDbUrl());
      return connection;
    } catch (ClassNotFoundException e) {
      log.warn("getConnection ClassNotFoundException" + JSON.toJSONString(store), e);
      throw new RuntimeException(e);
    } catch (Exception e) {
      log.warn("getConnection Exception" + JSON.toJSONString(store), e);
      throw new RuntimeException(e);
    }
  }

  public static void closeResultSet(ResultSet res) {
    try {
      if (res == null || res.isClosed()) {
        return;
      }
      res.close();
    } catch (SQLException e) {
      log.warn("close ResultSet Exception" + JSON.toJSONString(res), e);
      throw new RuntimeException(e);
    }
  }

  public static void closeStatement(Statement stmt) {
    try {
      if (stmt == null || stmt.isClosed()) {
        return;
      }
      stmt.close();
    } catch (SQLException e) {
      log.warn("close Statement Exception" + JSON.toJSONString(stmt), e);
      throw new RuntimeException(e);
    }
  }

  public static void closeConnection(Connection conn) {
    try {
      if (conn == null || conn.isClosed()) {
        return;
      }
      conn.close();
    } catch (SQLException e) {
      log.warn("close Connection Exception" + JSON.toJSONString(conn), e);
      throw new RuntimeException(e);
    }
  }
}
