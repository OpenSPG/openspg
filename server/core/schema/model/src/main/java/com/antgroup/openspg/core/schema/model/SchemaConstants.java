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

package com.antgroup.openspg.core.schema.model;

/** Constants used in schema modeling. */
public class SchemaConstants {

  /** The root type in the SPG schema, which is the root node of each advanced type. */
  public static final String ROOT_TYPE_UNIQUE_NAME = "Thing";

  /** The key name of common index config. */
  public static final String WITH_COMMON_INDEX = "commonIndex";

  /** The key name of concept layer config. */
  public static final String CONCEPT_LAYER_KEY = "conceptLayerConfig";

  /** The key name of concept taxonomic config. */
  public static final String CONCEPT_TAXONOMIC_KEY = "conceptTaxonomicConfig";

  /** The key name of multi version config. */
  public static final String MULTI_VERSION_CONFIG_KEY = "multipleVersionConfig";

  /** The key name of mounted concept config. */
  public static final String MOUNT_CONCEPT_CONFIG_KEY = "mountConceptConfig";

  /** The key name of constraints defined on standard types. */
  public static final String STANDARD_CONSTRAINT_KEY = "constrains";

  /** The key name of wether the standard type is spreadable */
  public static final String SPREADABLE = "spreadable";

  /** The key name of rule id. */
  public static final String PROPERTY_RULE_CONFIG_KEY = "ruleId";

  /** The key name of value type */
  public static final String VALUE_TYPE_KEY = "valueType";

  /** The key name of property group. */
  public static final String PROPERTY_GROUP_KEY = "propertyTag";

  /** The name property of concept type. */
  public static final String CONCEPT_NAME_PROPERTY_NAME = "name";

  /** The built-in user id */
  public static final String DEFAULT_USER_ID = "182320";

  /** The maximum length of the English name attribute for SPG types. */
  public static final int SCHEMA_SPG_TYPE_MAX_NAME = 60;

  /** The maximum length of the Chinese name attribute for SPG types */
  public static final int SCHEMA_SPG_TYPE_MAX_NAME_ZH = 80;

  /** The maximum length of the description attribute for SPG types */
  public static final int SCHEMA_SPG_TYPE_MAX_DESCRIPTION = 100;

  /** The maximum length of the Chinese name attribute for properties and relations */
  public static final int SCHEMA_PROPERTY_MAX_NAME_ZH = 150;

  /** The maximum length of the English name attribute for properties and relations */
  public static final int SCHEMA_PROPERTY_MAX_NAME = 120;

  /**
   * Default version of ontology record in db. Currently, system does not support multiple versions
   * of ontology, so it's fixed to be 1.
   */
  public static final int DEFAULT_ONTOLOGY_VERSION = 1;
}
