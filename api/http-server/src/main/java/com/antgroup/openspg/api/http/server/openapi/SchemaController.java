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

package com.antgroup.openspg.api.http.server.openapi;

import com.antgroup.openspg.api.facade.dto.schema.request.BuiltInPropertyRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.RelationRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.api.http.server.BaseController;
import com.antgroup.openspg.api.http.server.HttpBizCallback;
import com.antgroup.openspg.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.biz.common.util.AssertUtils;
import com.antgroup.openspg.biz.spgschema.SchemaManager;
import com.antgroup.openspg.core.spgschema.model.SchemaExtInfo;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.spgschema.model.type.ProjectSchema;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeAdvancedConfig;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/schema")
public class SchemaController extends BaseController {

  @Autowired private SchemaManager schemaManager;

  @RequestMapping(value = "/alterSchema", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Object> alterSchema(@RequestBody SchemaAlterRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("schemaDraft", request.getSchemaDraft());
          }

          @Override
          public Boolean action() {
            if (request.getSchemaDraft() == null
                || CollectionUtils.isEmpty(request.getSchemaDraft().getAlterSpgTypes())) {
              throw new IllegalArgumentException("schema draft is empty");
            }

            List<BaseAdvancedType> alterSpgTypes = request.getSchemaDraft().getAlterSpgTypes();
            for (BaseAdvancedType advancedType : alterSpgTypes) {
              if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
                for (Property property : advancedType.getProperties()) {
                  property.setSubjectTypeRef(advancedType.toRef());

                  if (CollectionUtils.isNotEmpty(property.getSubProperties())) {
                    for (SubProperty subProperty : property.getSubProperties()) {
                      subProperty.setSubjectTypeRef(property.toRef());
                    }
                  }
                  if (CollectionUtils.isNotEmpty(property.getSemantics())) {
                    for (PredicateSemantic semantic : property.getSemantics()) {
                      semantic.setSubjectTypeRef(property.toRef());
                    }
                  }
                  if (property.getExtInfo() == null) {
                    property.setExtInfo(new SchemaExtInfo());
                  }
                }
              }

              if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
                for (Relation relation : advancedType.getRelations()) {
                  relation.setSubjectTypeRef(advancedType.toRef());

                  if (CollectionUtils.isNotEmpty(relation.getSubProperties())) {
                    for (SubProperty subProperty :
                        relation.getAdvancedConfig().getSubProperties()) {
                      subProperty.setSubjectTypeRef(relation.toRef());
                    }
                  }
                  if (CollectionUtils.isNotEmpty(relation.getSemantics())) {
                    for (PredicateSemantic semantic : relation.getSemantics()) {
                      semantic.setSubjectTypeRef(relation.toRef());
                    }
                  }
                  if (relation.getExtInfo() == null) {
                    relation.setExtInfo(new SchemaExtInfo());
                  }
                }
              }

              if (advancedType.getParentTypeInfo() == null) {
                advancedType.setParentTypeInfo(ParentTypeInfo.THING);
              }
              if (advancedType.getAdvancedConfig() == null) {
                advancedType.setAdvancedConfig(new SPGTypeAdvancedConfig());
              }
              if (advancedType.getExtInfo() == null) {
                advancedType.setExtInfo(new SchemaExtInfo());
              }
            }
            schemaManager.alterSchema(request);
            return true;
          }
        });
  }

  @RequestMapping(value = "/queryProjectSchema", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryProjectSchema(ProjectSchemaRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ProjectSchema>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
          }

          @Override
          public ProjectSchema action() {
            return schemaManager.getProjectSchema(request.getProjectId());
          }
        });
  }

  @RequestMapping(value = "/querySpgType", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> querySpgType(SPGTypeRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<BaseSPGType>() {
          @Override
          public void check() {}

          @Override
          public BaseSPGType action() {
            return schemaManager.getSpgType(request.getName());
          }
        });
  }

  @RequestMapping(value = "/queryRelation", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryRelation(RelationRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Relation>() {
          @Override
          public void check() {}

          @Override
          public Relation action() {
            BaseSPGType spgType = schemaManager.getSpgType(request.getsName());
            if (null == spgType) {
              return null;
            }

            for (Relation relation : spgType.getRelations()) {
              if (relation.getName().equals(request.getRelation())
                  && relation.getObjectTypeRef().getName().equals(request.getoName())) {
                return relation;
              }
            }
            return null;
          }
        });
  }

  @RequestMapping(value = "/queryBuiltInProperty", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryBuiltInProperty(BuiltInPropertyRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<Property>>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("spgTypeEnum", request.getSpgTypeEnum());
          }

          @Override
          public List<Property> action() {
            return schemaManager.getBuiltInProperty(request.getSpgTypeEnum());
          }
        });
  }
}
