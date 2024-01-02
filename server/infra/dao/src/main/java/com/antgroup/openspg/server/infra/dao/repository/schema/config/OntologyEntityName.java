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

package com.antgroup.openspg.server.infra.dao.repository.schema.config;

import lombok.Getter;

@Getter
public class OntologyEntityName {

  /** The namespace. */
  private final String namespace;

  /** The unique name of schema type. */
  private final String name;

  /** The version id. */
  private final Integer version;

  /**
   * Construct SchemaTypeName by name and version
   *
   * @param name
   * @param version
   */
  public OntologyEntityName(String namespace, String name, Integer version) {
    this.namespace = namespace;
    this.name = name;
    this.version = version;
  }

  /**
   * Construct SchemaTypeName by name with version
   *
   * @param fullName
   */
  public OntologyEntityName(String fullName) {
    String nameWithVersion;
    int dotIndex = fullName.indexOf(Constants.SCHEMA_NAMESPACE_NAME_SPLIT_KEY);
    if (dotIndex < 0) {
      namespace = null;
      nameWithVersion = fullName;
    } else {
      namespace = fullName.substring(0, dotIndex);
      nameWithVersion = fullName.substring(dotIndex + 1);
    }

    int index = nameWithVersion.indexOf(Constants.SCHEMA_VERSION_SPLIT_KEY);
    if (index < 0) {
      name = nameWithVersion;
      version = null;
    } else {
      name = nameWithVersion.substring(0, index);
      version = Integer.parseInt(nameWithVersion.substring(index + 2));
    }
  }

  /**
   * Get full name.
   *
   * @return
   */
  public String getFullName() {
    StringBuilder sb = new StringBuilder();
    if (namespace != null) {
      sb.append(namespace);
      sb.append(Constants.SCHEMA_NAMESPACE_NAME_SPLIT_KEY);
    }
    sb.append(name);
    if (version != null && version > 1) {
      sb.append(Constants.SCHEMA_VERSION_SPLIT_KEY);
      sb.append(version);
    }
    return sb.toString();
  }

  /**
   * Get unqiue name.
   *
   * @return
   */
  public String getUniqueName() {
    StringBuilder sb = new StringBuilder();
    if (namespace != null) {
      sb.append(namespace);
      sb.append(Constants.SCHEMA_NAMESPACE_NAME_SPLIT_KEY);
    }
    sb.append(name);
    return sb.toString();
  }

  public static String getUniqueName(String fullName) {
    OntologyEntityName ontologyEntityName = new OntologyEntityName(fullName);
    return ontologyEntityName.getUniqueName();
  }
}
