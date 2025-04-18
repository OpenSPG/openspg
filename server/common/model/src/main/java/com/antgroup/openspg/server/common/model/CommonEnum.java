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
package com.antgroup.openspg.server.common.model;

/** all Common enum */
public interface CommonEnum {

  /** Status Enum */
  enum Status {
    ENABLE,
    DISABLE
  }

  enum DataSourceCategory {
    BATCH,
    STREAM
  }

  enum DataSourceType {

    /** ODPS */
    ODPS("ODPS", DataSourceCategory.BATCH, null), /*
    */
    /** Hive */
    /*
    Hive("Hive", DataSourceCategory.BATCH, "org.apache.hive.jdbc.HiveDriver"),
    */
    /** MySQL */
    /*
    MySQL("MySQL", DataSourceCategory.BATCH, "com.mysql.jdbc.Driver"),
    */
    /** Oracle */
    /*
    Oracle("Oracle", DataSourceCategory.BATCH, "oracle.jdbc.driver.OracleDriver"),
    */
    /** PostgreSQL */
    /*
    PostgreSQL("PostgreSQL", DataSourceCategory.BATCH, "org.postgresql.Driver"),
    */
    /** DB2 */
    /*
    DB2("DB2", DataSourceCategory.BATCH, "com.ibm.db2.jdbc.app.DB2Driver"),
    */
    /** MariaDB */
    /*
    MariaDB("MariaDB", DataSourceCategory.BATCH, "org.mariadb.jdbc.Driver"),
    */
    /** MS Sql */
    /*
    MSSql("MSSql", DataSourceCategory.BATCH, "com.microsoft.sqlserver.jdbc.SQLServerDriver"),*/
    /** SLS */
    SLS("SLS", DataSourceCategory.STREAM, null) /*,
    */
  /** Kafka */
  /*
  Kafka("Kafka", DataSourceCategory.STREAM, null),
  */
  /** MQ */
  /*
  SofaMQ("SofaMQ", DataSourceCategory.STREAM, null),
  */
  /** LIGHT_DRC */
  /*
  LightDRC("LightDRC", DataSourceCategory.STREAM, null)*/ ;

    private String name;

    private DataSourceCategory category;

    private String driver;

    DataSourceType(String name, DataSourceCategory category, String driver) {
      this.name = name;
      this.category = category;
      this.driver = driver;
    }

    public String getName() {
      return name;
    }

    public DataSourceCategory getCategory() {
      return category;
    }

    public String getDriver() {
      return driver;
    }

    public static DataSourceType toEnum(String name) {
      for (DataSourceType type : DataSourceType.values()) {
        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
      return null;
    }
  }
}
