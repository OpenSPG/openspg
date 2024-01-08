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

package com.antgroup.openspg.reasoner.io.model;

import java.util.Arrays;

public class HiveTableInfo extends AbstractTableInfo {
  private String hadoopUserName = null;

  // jdbc info
  private String jdbcUrl;
  private String jdbcUser;
  private String jdbcPasswd;

  // output path info
  private String outputParquetPath; // can be null
  private String coreSiteXml;
  private String hdfsSiteXml;

  /**
   * Getter method for property <tt>jdbcUrl</tt>.
   *
   * @return property value of jdbcUrl
   */
  public String getJdbcUrl() {
    return jdbcUrl;
  }

  /**
   * Setter method for property <tt>jdbcUrl</tt>.
   *
   * @param jdbcUrl value to be assigned to property jdbcUrl
   */
  public void setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
  }

  /**
   * Getter method for property <tt>hadoopUserName</tt>.
   *
   * @return property value of hadoopUserName
   */
  public String getHadoopUserName() {
    return hadoopUserName;
  }

  /**
   * Setter method for property <tt>hadoopUserName</tt>.
   *
   * @param hadoopUserName value to be assigned to property hadoopUserName
   */
  public void setHadoopUserName(String hadoopUserName) {
    this.hadoopUserName = hadoopUserName;
  }

  /**
   * Getter method for property <tt>parquetPath</tt>.
   *
   * @return property value of parquetPath
   */
  public String getOutputParquetPath() {
    return outputParquetPath;
  }

  /**
   * Setter method for property <tt>parquetPath</tt>.
   *
   * @param outputParquetPath value to be assigned to property parquetPath
   */
  public void setOutputParquetPath(String outputParquetPath) {
    this.outputParquetPath = outputParquetPath;
  }

  /**
   * Getter method for property <tt>coreSiteXml</tt>.
   *
   * @return property value of coreSiteXml
   */
  public String getCoreSiteXml() {
    return coreSiteXml;
  }

  /**
   * Setter method for property <tt>coreSiteXml</tt>.
   *
   * @param coreSiteXml value to be assigned to property coreSiteXml
   */
  public void setCoreSiteXml(String coreSiteXml) {
    this.coreSiteXml = coreSiteXml;
  }

  /**
   * Getter method for property <tt>hdfsSiteXml</tt>.
   *
   * @return property value of hdfsSiteXml
   */
  public String getHdfsSiteXml() {
    return hdfsSiteXml;
  }

  /**
   * Setter method for property <tt>hdfsSiteXml</tt>.
   *
   * @param hdfsSiteXml value to be assigned to property hdfsSiteXml
   */
  public void setHdfsSiteXml(String hdfsSiteXml) {
    this.hdfsSiteXml = hdfsSiteXml;
  }

  @Override
  public int hashCode() {
    Object[] item = new Object[] {table, project, jdbcUrl, getPartitionString()};
    return Arrays.hashCode(item);
  }

  /**
   * Getter method for property <tt>user</tt>.
   *
   * @return property value of user
   */
  public String getJdbcUser() {
    return jdbcUser;
  }

  /**
   * Setter method for property <tt>user</tt>.
   *
   * @param jdbcUser value to be assigned to property user
   */
  public void setJdbcUser(String jdbcUser) {
    this.jdbcUser = jdbcUser;
  }

  /**
   * Getter method for property <tt>passwd</tt>.
   *
   * @return property value of passwd
   */
  public String getJdbcPasswd() {
    return jdbcPasswd;
  }

  /**
   * Setter method for property <tt>passwd</tt>.
   *
   * @param jdbcPasswd value to be assigned to property passwd
   */
  public void setJdbcPasswd(String jdbcPasswd) {
    this.jdbcPasswd = jdbcPasswd;
  }
}
