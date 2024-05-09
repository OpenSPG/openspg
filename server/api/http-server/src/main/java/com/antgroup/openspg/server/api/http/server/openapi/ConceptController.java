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

package com.antgroup.openspg.server.api.http.server.openapi;

import com.antgroup.openspg.core.schema.model.semantic.request.DefineDynamicTaxonomyRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.DefineTripleSemanticRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveDynamicTaxonomyRequest;
import com.antgroup.openspg.core.schema.model.semantic.request.RemoveTripleSemanticRequest;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.biz.schema.ConceptManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/concept")
public class ConceptController extends BaseController {

  @Autowired private ConceptManager conceptManager;

  @RequestMapping(value = "/queryConcept", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryConcept(ConceptRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ConceptList>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("conceptTypeName", request.getConceptTypeName());
          }

          @Override
          public ConceptList action() {
            return conceptManager.getConceptDetail(
                request.getConceptTypeName(), request.getConceptName());
          }
        });
  }

  @RequestMapping(value = "/defineDynamicTaxonomy", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Object> defineDynamicTaxonomy(
      @RequestBody DefineDynamicTaxonomyRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("conceptTypeName", request.getConceptTypeName());
            AssertUtils.assertParamStringIsNotBlank("conceptName", request.getConceptName());
          }

          @Override
          public Boolean action() {
            conceptManager.defineDynamicTaxonomy(request);
            return Boolean.TRUE;
          }
        });
  }

  @RequestMapping(value = "/defineLogicalCausation", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Object> defineLogicalCausation(
      @RequestBody DefineTripleSemanticRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull(
                "subjectConceptTypeName", request.getSubjectConceptTypeName());
            AssertUtils.assertParamStringIsNotBlank(
                "subjectConceptName", request.getSubjectConceptName());
            AssertUtils.assertParamObjectIsNotNull(
                "objectConceptTypeName", request.getObjectConceptTypeName());
            AssertUtils.assertParamStringIsNotBlank(
                "objectConceptName", request.getObjectConceptName());
            AssertUtils.assertParamStringIsNotBlank("predicateName", request.getPredicateName());
            AssertUtils.assertParamStringIsNotBlank("dslRule", request.getDsl());
          }

          @Override
          public Boolean action() {
            conceptManager.defineLogicalCausation(request);
            return Boolean.TRUE;
          }
        });
  }

  @RequestMapping(value = "/removeDynamicTaxonomy", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Object> removeDynamicTaxonomy(
      @RequestBody RemoveDynamicTaxonomyRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
          }

          @Override
          public Boolean action() {
            conceptManager.removeDynamicTaxonomy(request);
            return Boolean.TRUE;
          }
        });
  }

  @RequestMapping(value = "/removeLogicalCausation", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Object> removeLogicalCausation(
      @RequestBody RemoveTripleSemanticRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull(
                "subjectConceptTypeName", request.getSubjectConceptTypeName());
            AssertUtils.assertParamStringIsNotBlank(
                "subjectConceptName", request.getSubjectConceptName());
            AssertUtils.assertParamObjectIsNotNull(
                "objectConceptTypeName", request.getObjectConceptTypeName());
            AssertUtils.assertParamStringIsNotBlank(
                "objectConceptName", request.getObjectConceptName());
            AssertUtils.assertParamStringIsNotBlank("predicateName", request.getPredicateName());
          }

          @Override
          public Boolean action() {
            conceptManager.removeLogicalCausation(request);
            return Boolean.TRUE;
          }
        });
  }
}
