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

package com.antgroup.openspg.server.core.schema.model.type;

import com.antgroup.openspg.server.core.schema.model.BasicInfo;
import com.antgroup.openspg.server.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.core.schema.model.predicate.Property;
import com.antgroup.openspg.server.core.schema.model.predicate.Relation;
import java.util.List;

/**
 * Class definition of the Concept.<br>
 *
 * <p>A concept is an abstract generalization of a type of entity with similar features, expressing
 * a semantic understanding or classification of the entity type. Concepts can be divided into two
 * types: common sense concepts and classification concepts. The concept of common sense generally
 * refers to a common sense summary of a certain entity type, such as brand, administration, etc.
 * And the concept of classification expresses the classification of a certain ntity type, such as
 * user classification, drug classification etc. As the type definition of a concept, {@link
 * ConceptType} usually has the following configuration information:
 *
 * <ul>
 *   <li>{@link ConceptType#conceptLayerConfig}：include the hypernym predicate and name of each
 *       layer.
 *   <li>{@link ConceptType#conceptTaxonomicConfig}: taxonomy configuration of the concept.
 *   <li>{@link ConceptType#conceptMultiVersionConfig}: multi version configuration.
 * </ul>
 *
 * <br>
 * Take Administration as an example:<br>
 *
 * <pre>
 * Administration   country  province  city   county <br>
 *                    China(postCode: CHN, alias: 中华, 华夏, 中华人民共和国) <br>
 *                      ｜-----ZheJiang (postCode: 310000, alias: 浙, 浙江) <br>
 *                              ｜------HangZhou (postCode: 310000, alias: 杭州) <br>
 *                                        ｜-----XiHu (postcode: 310012)
 * </pre>
 *
 * <ul>
 *   <li>unique name: AdminArea
 *   <li>hypernym predicate: locateAt
 *   <li>layer names: [country, province, city, county]
 * </ul>
 */
public class ConceptType extends BaseAdvancedType {

  private static final long serialVersionUID = 4965144707881225899L;

  /** The relationship between child concept and its parent concept. */
  private final ConceptLayerConfig conceptLayerConfig;

  /** The configuration of taxonomic concept. */
  private final ConceptTaxonomicConfig conceptTaxonomicConfig;

  /** The multi version configuration. */
  private final MultiVersionConfig conceptMultiVersionConfig;

  public ConceptType(
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      List<Property> properties,
      List<Relation> relations,
      SPGTypeAdvancedConfig advancedConfig,
      ConceptLayerConfig conceptLayerConfig,
      ConceptTaxonomicConfig conceptTaxonomicConfig,
      MultiVersionConfig multiVersionConfig) {
    super(
        basicInfo, parentTypeInfo, SPGTypeEnum.CONCEPT_TYPE, properties, relations, advancedConfig);
    this.conceptLayerConfig = conceptLayerConfig;
    this.conceptTaxonomicConfig = conceptTaxonomicConfig;
    this.conceptMultiVersionConfig = multiVersionConfig;
  }

  public ConceptLayerConfig getConceptLayerConfig() {
    return conceptLayerConfig;
  }

  public ConceptTaxonomicConfig getConceptTaxonomicConfig() {
    return conceptTaxonomicConfig;
  }

  public MultiVersionConfig getConceptMultiVersionConfig() {
    return conceptMultiVersionConfig;
  }

  public boolean isTaxonomicConcept() {
    return conceptTaxonomicConfig != null
        && conceptTaxonomicConfig.getTaxonomicTypeIdentifier() != null;
  }
}
