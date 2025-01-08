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
    STREAM,
    FILE
  }

  enum DataSourceType {

    /** ODPS */
    ODPS("ODPS", DataSourceCategory.BATCH, null),
    /** Hive */
    HIVE("Hive", DataSourceCategory.BATCH, "org.apache.hive.jdbc.HiveDriver"),
    /** MySQL */
    MYSQL("MySQL", DataSourceCategory.BATCH, "com.mysql.jdbc.Driver"),
    /** Oracle */
    ORACLE("Oracle", DataSourceCategory.BATCH, "oracle.jdbc.driver.OracleDriver"),
    /** PostgreSQL */
    POSTGRESQL("PostgreSQL", DataSourceCategory.BATCH, "org.postgresql.Driver"),
    /** DB2 */
    DB2("DB2", DataSourceCategory.BATCH, "com.ibm.db2.jdbc.app.DB2Driver"),
    /** MariaDB */
    MARIA_DB("MariaDB", DataSourceCategory.BATCH, "org.mariadb.jdbc.Driver"),
    /** MS Sql */
    MS_SQL("MS Sql", DataSourceCategory.BATCH, "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    /** SLS */
    SLS("SLS", DataSourceCategory.STREAM, null),
    /** Kafka */
    KAFKA("Kafka", DataSourceCategory.STREAM, null),
    /** MQ */
    SOFA_MQ("SofaMQ", DataSourceCategory.STREAM, null),
    /** LIGHT_DRC */
    LIGHT_DRC("LIGHT", DataSourceCategory.STREAM, null),
    /** ONS */
    ONS("ONS", DataSourceCategory.STREAM, null),
    /** CSV */
    CSV("CSV", DataSourceCategory.FILE, null),
    /** Text */
    TEXT("Text", DataSourceCategory.FILE, null),
    /** json */
    JSON("JSON", DataSourceCategory.FILE, null),
    /** parquet */
    PARQUET("Parquet", DataSourceCategory.FILE, null),
    /** orc */
    ORC("Orc", DataSourceCategory.FILE, null),
    /** avro */
    AVRO("Avro", DataSourceCategory.FILE, null);

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
