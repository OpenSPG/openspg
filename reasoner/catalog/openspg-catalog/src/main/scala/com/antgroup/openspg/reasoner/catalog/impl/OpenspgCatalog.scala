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

package com.antgroup.openspg.reasoner.catalog.impl

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.core.schema.model.`type`.{BaseAdvancedType, BaseSPGType, BasicType, ProjectSchema}
import com.antgroup.openspg.core.schema.model.predicate.{Property, Relation}
import com.antgroup.openspg.core.schema.model.semantic.DynamicTaxonomySemantic
import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.SchemaException
import com.antgroup.openspg.reasoner.common.graph.edge.SPO
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.catalog.{AbstractConnection, Catalog, GeneralSemanticRule, PropertyGraphSchema, SemanticPropertyGraph, SemanticRule}
import com.antgroup.openspg.reasoner.lube.catalog.struct.{Edge, Field, Node}
import com.antgroup.openspg.server.api.facade.ApiResponse
import com.antgroup.openspg.server.api.facade.client.{ConceptFacade, SchemaFacade}
import com.antgroup.openspg.server.api.facade.dto.schema.request.{ConceptRequest, ProjectSchemaRequest}
import com.antgroup.openspg.server.api.http.client.{HttpConceptFacade, HttpSchemaFacade}
import com.antgroup.openspg.server.api.http.client.util.{ConnectionInfo, HttpClientBootstrap}
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils


class OpenspgCatalog(val projectId: Long,
                     val connInfo: KgSchemaConnectionInfo,
                     val projectSchema: ProjectSchema = null) extends Catalog {

  if (projectSchema == null) {
    HttpClientBootstrap.init(new ConnectionInfo(connInfo.uri))
  }

  private val spgSchemaFacade: SchemaFacade = new HttpSchemaFacade()
  private val spgConceptFacade: ConceptFacade = new HttpConceptFacade()

  private val defineRules = new mutable.HashMap[String, SemanticRule]()

  /**
   * Get schema from knowledge graph
   */
  override def getKnowledgeGraph(): SemanticPropertyGraph = {
    val realProjectSchema: ProjectSchema =
      if (projectSchema == null) {
        val request = new ProjectSchemaRequest()
        request.setProjectId(projectId)
        resultOf(spgSchemaFacade.queryProjectSchema(request))
      } else {
        projectSchema
      }

    val nodes: mutable.Map[String, Node] = new mutable.HashMap[String, Node]()
    val edges: mutable.Map[SPO, Edge] = new mutable.HashMap[SPO, Edge]
    realProjectSchema.getSpgTypes.asScala.foreach(spgType => {
      val node = toNode(realProjectSchema, spgType)
      if (node != null) {
        nodes.put(spgType.getName, node)

        val vertexEdges = toEdges(realProjectSchema, spgType)
        for (e <- vertexEdges) {
          if (edges.contains(e._1)) {
            val edgeInfo = edges(e._1)
            val mergeSet = edgeInfo.properties ++ e._2.properties
            edges += (e._1 -> Edge(
              edgeInfo.startNode,
              edgeInfo.typeName,
              edgeInfo.endNode,
              mergeSet,
              edgeInfo.resolved))
          } else {
            edges += e
          }
        }
      }
    })

    new SemanticPropertyGraph(
      Catalog.defaultGraphName,
      new PropertyGraphSchema(nodes, edges),
      defineRules,
      null)
  }

  private def toNode(projectSchema: ProjectSchema, spgType: BaseSPGType): Node = {
    val attrList = new ListBuffer[Field]()
    attrList.+=(defaultTypeField)
    spgType match {
      case _: BasicType =>
        null
      case advancedType: BaseAdvancedType =>
        attrList.++=(advancedType.getProperties.asScala.map(spgProperty => {
          toField(projectSchema, spgType, spgProperty)
        }))
        attrList.++=(getDefaultNodeProperties())
        Node(
          advancedType.getName,
          PropertySchemaOps.toNodeType(spgType.getSpgTypeEnum),
          attrList.toSet,
          true)
    }
  }

  private def toField(projectSchema: ProjectSchema,
                      spgType: BaseSPGType,
                      spgProperty: Property): Field = {
    val propertyType = PropertySchemaOps
      .stringToKgType2(projectSchema.getByRef(spgProperty.getObjectTypeRef))
    val rule = spgProperty.getLogicalRule
    val predicateName = spgProperty.getName
    if (rule != null && StringUtils.isNotBlank(rule.getContent)) {
      defineRules.put(s"${spgType.getName}.${predicateName}", GeneralSemanticRule(rule.getContent))
      new Field(predicateName, propertyType, false)
    } else {
      new Field(predicateName, propertyType, true)
    }
  }

  private def defaultTypeField: Field = {
    new Field(Constants.CONTEXT_LABEL, KTString, true)
  }

  private def toEdges(projectSchema: ProjectSchema, spgType: BaseSPGType): Map[SPO, Edge] = {
    if (CollectionUtils.isEmpty(spgType.getRelations)) {
      Map.empty
    } else {
      spgType.getRelations.asScala
        .flatMap(relation => toEdge(projectSchema, spgType, relation))
        .map(e => (new SPO(e.startNode, e.typeName, e.endNode), e))
        .toMap
    }
  }

  private def toEdge(projectSchema: ProjectSchema,
                     spgType: BaseSPGType,
                     rel: Relation): List[Edge] = {
    val attrList = rel.getSubProperties.asScala.toList
    val fields = new ListBuffer[Field]()
    val s = spgType.getName
    val p = rel.getName
    val o = rel.getObjectTypeRef.getName
    val spo = new SPO(s, p, o).toString

    fields.++=(attrList.map(att => {
      val relationType = PropertySchemaOps
        .stringToKgType2(projectSchema.getByRef(att.getObjectTypeRef))
      new Field(att.getName, relationType, true)
    }))
    fields.+=(defaultTypeField)
    fields.++=(getDefaultEdgeProperties())

    val rule = rel.getLogicalRule
    if (rule != null && StringUtils.isNotBlank(rule.getContent)) {
      defineRules.put(spo, GeneralSemanticRule(rule.getContent))
      List.apply(Edge(s, p, o, fields.toSet, false))
    } else if (p.equals("belongTo")) {
      val request = new ConceptRequest()
      request.setConceptTypeName(o)

      val concept = resultOf(spgConceptFacade.queryConcept(request))
      if (CollectionUtils.isNotEmpty(concept.getConcepts)) {
        concept.getConcepts.asScala
          .map(r => {
            r.getSemantics.asScala.foreach {
              case belong: DynamicTaxonomySemantic =>
                if (belong.getLogicalRule != null) {
                  defineRules.put(spo + "/" + r.getName,
                    GeneralSemanticRule(belong.getLogicalRule.getContent))
                }
              case _ =>
            }
            Edge(s, p, o + "/" + r.getName, fields.toSet, false)
          })
          .toList
      } else {
        List.apply(Edge(s, p, o, fields.toSet, true))
      }
    } else {
      List.apply(Edge(s, p, o, fields.toSet, true))
    }
  }

  /**
   * Get connections of knowledge graph
   *
   * @return
   */
  override def getConnections(): Map[AbstractConnection, Set[String]] = {
    val connections = new mutable.HashMap[AbstractConnection, Set[String]]()
    graphRepository
      .get("KG")
      .map(graph => {
        val graphSchema = graph.graphSchema

        val types = new mutable.HashSet[String]()
        types.++=(graphSchema.nodes.keySet)
        types.++=(graphSchema.edges.map(x => x._1.toString).toSet)

        connections.put(new AbstractConnection {}, types.toSet)
      })
    connections.toMap
  }

  private def resultOf[T](apiResponse: ApiResponse[T]): T = {
    if (apiResponse.isSuccess) {
      apiResponse.getData
    } else {
      throw SchemaException("Get Schema failed")
    }
  }

  /**
   * get default node properties
   *
   * @return
   */
  override def getDefaultNodeProperties()
  : Set[Field] = {
    Set.apply(
      new Field(Constants.NODE_ID_KEY, KTString, true),
      new Field(Constants.VERTEX_INTERNAL_ID_KEY, KTString, true),
      new Field(Constants.CONTEXT_LABEL, KTString, true))
  }

  /**
   * get default edge properties
   */
  override def getDefaultEdgeProperties()
  : Set[Field] = {
    Set.apply(
      new Field(Constants.CONTEXT_LABEL, KTString, true),
      new Field(Constants.EDGE_FROM_ID_KEY, KTString, true),
      new Field(Constants.EDGE_TO_ID_KEY, KTString, true),
      new Field(Constants.EDGE_FROM_INTERNAL_ID_KEY, KTString, true),
      new Field(Constants.EDGE_TO_INTERNAL_ID_KEY, KTString, true),
      new Field(Constants.EDGE_FROM_ID_TYPE_KEY, KTString, true),
      new Field(Constants.EDGE_TO_ID_TYPE_KEY, KTString, true)
    )
  }

}
