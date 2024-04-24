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

package com.antgroup.openspg.reasoner.session

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.types.KTObject
import com.antgroup.openspg.reasoner.common.utils.ParameterUtils
import com.antgroup.openspg.reasoner.lube.Logging
import com.antgroup.openspg.reasoner.lube.block._
import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.catalog.struct.{Field, NodeType}
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.logical.operators.LogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.optimizer.LogicalOptimizer
import com.antgroup.openspg.reasoner.lube.logical.planning.{
  LogicalPlanner,
  LogicalPlannerContext,
  SubQueryMerger
}
import com.antgroup.openspg.reasoner.lube.logical.validate.Validator
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface
import com.antgroup.openspg.reasoner.lube.physical.{GraphSession, PropertyGraph}
import com.antgroup.openspg.reasoner.lube.physical.operators.{PhysicalOperator, Select}
import com.antgroup.openspg.reasoner.lube.physical.planning.{
  PhysicalPlanner,
  PhysicalPlannerContext
}
import com.antgroup.openspg.reasoner.lube.physical.rdg.{RDG, Result}
import com.antgroup.openspg.reasoner.util.LoaderUtil
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig

/**
 * Base class for KGReasoner pipeline.
 * The class provides a generic implementation of the necessary steps to
 * execute a KGDSL/GQL query on Knowledge Graph through tabular based engine, including parsing,
 * unresolved logical planning, logical planning an physical planning
 *
 * @param parser
 * @param catalog
 * @param typeTag$T$0
 * @tparam T
 */
abstract class KGReasonerSession[T <: RDG[T]: TypeTag](
    val parser: ParserInterface,
    val catalog: Catalog)
    extends Logging {
  private val graphSession: GraphSession[T] = new GraphSession()
  private var loaderConfig: GraphLoaderConfig = new GraphLoaderConfig()
  private var variableParameters: Set[String] = Set.empty
  private var idFilterParameters: Map[String, String] = Map.empty
  private var optimizedLogicalPlan: List[LogicalOperator] = List.empty

  def this(
      parser: ParserInterface,
      catalog: Catalog,
      optimizedLogicalPlan: List[LogicalOperator],
      variableParameters: Set[String],
      idFilterParameters: Map[String, String]) {
    this(parser, catalog)
    this.optimizedLogicalPlan = optimizedLogicalPlan
    this.variableParameters = variableParameters
    this.idFilterParameters = idFilterParameters
  }

  /**
   * Init KGReasonerSession.
   */
  def init(): Unit = {
    catalog.init()
  }

  /**
   * Execute a KGDSL/GQL query, return [[Result]]
   * step1: Parse query to unresolved logical plan, which is represented by [[Block]].
   * step2: Use [[LogicalPlanner]] to plan unresolved logical plan to logical plan,
   * which is represented by [[LogicalOperator]]
   * step3: Use [[PhysicalPlanner]] to plan logical plan to physical plan,
   * which is represented by [[PhysicalOperator]]
   *
   * @param query KGDSL/GQL query
   * @param params runtime config
   * @return
   */
  def getResult(query: String, params: Map[String, Object]): Result = {
    val physicalPlan = plan(query, params)
    var result: Result = null
    for (physicalOp <- physicalPlan) {
      result = getResult(physicalOp)
    }
    result
  }

  def getResult(physicalPlan: PhysicalOperator[T]): Result = {
    physicalPlan match {
      case select: Select[T] =>
        select.row
      case _ =>
        physicalPlan.rdg
    }
  }

  /**
   * get variable parameter name
   * @return
   */
  def getParameterVariable(): Set[String] = variableParameters

  /**
   * get id filter parameters
   * @return
   */
  def getIdFilterParameters(): Map[String, String] = idFilterParameters

  /**
   * get optimized logical plan tree
   * @return
   */
  def getOptimizedLogicalPlan(): List[LogicalOperator] = optimizedLogicalPlan

  /**
   * get load config from parse plan
   */
  def getLoaderConfig(): GraphLoaderConfig = loaderConfig

  /**
   * Generate the optimization physical plan of giving KGDSL/GQL query.
   * step1: Parse query to unresolved logical plan, which is represented by [[Block]].
   * step2: Use [[LogicalPlanner]] to plan unresolved logical plan to logical plan,
   * which is represented by [[LogicalOperator]]
   * step3: Use [[PhysicalPlanner]] to plan logical plan to physical plan,
   * which is represented by [[PhysicalOperator]]
   *
   * @param query
   * @param params
   * @return
   */
  def plan(query: String, params: Map[String, Object]): List[PhysicalOperator[T]] = {
    val start = System.currentTimeMillis()
    val blocks = plan2UnresolvedLogicalPlan(query, params)
    if (ParameterUtils.isEnableSPGPlanPrettyPrint(params)) {
      for (block: Block <- blocks) {
        logger.info(block.pretty)
      }
    }
    logger.info(
      "benchmark main plan plan2UnresolvedLogicalPlan cost = "
        + (System.currentTimeMillis() - start))
    planBlock(blocks, params)
  }

  /**
   * Generate the optimization physical plan from Blocks.
   */
  def planBlock(blocks: List[Block], params: Map[String, Object]): List[PhysicalOperator[T]] = {
    optimizedLogicalPlan = plan2OptimizedLogicalPlan(blocks, params)
    planLogicalPlan2PhysicalPlan(optimizedLogicalPlan, params)
  }

  /**
   * Generate the optimization logical plan of giving KGDSL/GQL query.
   * step1: Parse query to unresolved logical plan, which is represented by [[Block]].
   * step2: Use [[LogicalPlanner]] to plan unresolved logical plan to logical plan,
   * which is represented by [[LogicalOperator]]
   * @param query
   * @param params
   * @return
   */
  def plan2OptimizedLogicalPlan(
      blocks: List[Block],
      params: Map[String, Object]): List[LogicalOperator] = {
    val start = System.currentTimeMillis()
    val optimizedLogicalPlan = plan2LogicalPlan(blocks, params)
    logger.info(
      "benchmark main plan plan2LogicalPlan cost = "
        + (System.currentTimeMillis() - start))
    optimizedLogicalPlan
  }

  def planLogicalPlan2PhysicalPlan(
      optimizedLogicalPlan: List[LogicalOperator],
      params: Map[String, Object]): List[PhysicalOperator[T]] = {
    val start = System.currentTimeMillis()
    val physicalPlan = plan2PhysicalPlan(optimizedLogicalPlan, params)
    logger.info(
      "benchmark main plan plan2PhysicalPlan cost = "
        + (System.currentTimeMillis() - start))
    if (!graphSession.hasGraph(Catalog.defaultGraphName)) {
      logger.info(s"begin to load graph data for ${Catalog.defaultGraphName}")
      val loaderConfig = LoaderUtil.getLoaderConfig(optimizedLogicalPlan, catalog)
      this.loaderConfig = loaderConfig
      val rdg = loadGraph(loaderConfig)
      graphSession.register(Catalog.defaultGraphName, rdg)
    }
    physicalPlan
  }

  /**
   * Load a graph from Knowledge Graph to [[KGReasonerSession]]
   *
   * @param graphLoaderConfig
   * @return
   */
  def loadGraph(graphLoaderConfig: GraphLoaderConfig): PropertyGraph[T]

  /**
   * Return the specific [[PropertyGraph]]
   *
   * @param graphName
   * @return
   */
  def getGraph(graphName: String): PropertyGraph[T] = {
    graphSession.getGraph(graphName)
  }

  /**
   * Return if has load KG, which is the default graph.
   * @return
   */
  def hasLoadGraph: Boolean = {
    graphSession.hasGraph(Catalog.defaultGraphName)
  }

  /**
   * get the new added property in node or edge
   *
   * @param physicalOperator
   * @return
   */
  protected def getNewProperty(
      query: String,
      params: Map[String, Object]): mutable.Map[String, mutable.HashSet[Field]] = {
    val unresolvedLogicalPlan = plan2UnresolvedLogicalPlan(query, params)
    val logicalPlanList = plan2LogicalPlan(unresolvedLogicalPlan, params)
    val entityType2FiledMap: mutable.HashMap[String, mutable.HashSet[Field]] =
      new mutable.HashMap()

    for (logicalPlan: LogicalOperator <- logicalPlanList) {
      val partEntityType2FiledMap =
        logicalPlan.transform[mutable.HashMap[String, mutable.HashSet[Field]]] {
          case (logicalOp: LogicalOperator, mapList) =>
            var type2FiledMap: mutable.HashMap[String, mutable.HashSet[Field]] = null
            if (mapList.isEmpty) {
              type2FiledMap = new mutable.HashMap[String, mutable.HashSet[Field]]()
            } else {
              type2FiledMap = mapList.head
            }

            val varList: List[Var] = logicalOp.fields
            val solvedModel: SolvedModel = logicalOp.solved
            varList.foreach(v =>
              v match {
                case NodeVar(name, fields) =>
                  type2FiledMap = addFieldToMap(type2FiledMap, name, fields, solvedModel)
                case EdgeVar(name, fields) =>
                  type2FiledMap = addFieldToMap(type2FiledMap, name, fields, solvedModel)
                case PropertyVar(name, field) =>
                  type2FiledMap =
                    addFieldToMap(type2FiledMap, name, Set.apply(field), solvedModel)
                case _ => type2FiledMap
              })
            type2FiledMap
          case (_, mapList) => mapList.head
        }
      for (entityType: String <- partEntityType2FiledMap.keySet) {
        val fieldSet = partEntityType2FiledMap(entityType)
        if (entityType2FiledMap.contains(entityType)) {
          val existFiledSet = entityType2FiledMap(entityType)
          existFiledSet ++= fieldSet
          entityType2FiledMap += (entityType -> existFiledSet)
        } else {
          entityType2FiledMap += (entityType -> fieldSet)
        }
      }
    }
    entityType2FiledMap
  }

  private def addFieldToMap(
      type2FieldMap: mutable.HashMap[String, mutable.HashSet[Field]],
      alias: String,
      newFields: Set[Field],
      solvedModel: SolvedModel): mutable.HashMap[String, mutable.HashSet[Field]] = {
    val entityTypeSet: Set[String] = solvedModel.alias2Types(alias)
    for (entityType: String <- entityTypeSet) {
      val existFiledSet: mutable.HashSet[Field] = new mutable.HashSet[Field]()
      if (type2FieldMap.contains(entityType)) {
        existFiledSet ++= type2FieldMap(entityType)
      }
      existFiledSet ++= newFields
      type2FieldMap += (entityType -> existFiledSet)
    }
    type2FieldMap
  }

  private def plan2LogicalPlan(
      blockList: List[Block],
      params: Map[String, Object]): List[LogicalOperator] = {
    var start = System.currentTimeMillis()
    // plan to logical plan
    implicit val context: LogicalPlannerContext = LogicalPlannerContext(catalog, parser, params)
    logger.info(
      "benchmark LogicalPlannerContext cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    val dag = if (blockList.size > 1) {
      Validator.validate(blockList)
    } else {
      Validator.validate(parser, blockList.head)
    }
    logger.info(
      "benchmark validate cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    val logicalPlans = LogicalPlanner.plan(dag)
    logger.info(
      "benchmark LogicalPlanner.plan cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    val optimizedLogicalDag = logicalPlans.map(LogicalOptimizer.optimize(_))
    logger.info(
      "benchmark logicalPlans.map cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    var optimizedLogicalPlans: List[LogicalOperator] = null
    val subQueryEnable = context.params
      .getOrElse(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, "false")
      .toString
    logger.info("subQueryEnable " + subQueryEnable)
    if ("true".equals(subQueryEnable)) {
      val subQueryMerger = new SubQueryMerger(optimizedLogicalDag)
      optimizedLogicalPlans = List.apply(subQueryMerger.plan)
    } else {
      optimizedLogicalPlans =
        optimizedLogicalDag.order().reverse.map(optimizedLogicalDag.popNode(_))
    }
    logger.info(
      "benchmark optimizedLogicalPlans cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    if (ParameterUtils.isEnableSPGPlanPrettyPrint(params)) {
      for (optimizedLogicalPlan <- optimizedLogicalPlans) {
        logger.info(s"optimized logical plan:\n${optimizedLogicalPlan.pretty}\n")
      }
    }
    logger.info(
      "benchmark logical show cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    optimizedLogicalPlans
  }

  def plan2UnresolvedLogicalPlan(query: String, params: Map[String, Object]): List[Block] = {
    // parser query to unresolved logical plan
    var start = System.currentTimeMillis()
    val blocks = planToBlock(query, params)
    logger.info(
      "benchmark planToBlock cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    registerSchema(blocks)
    logger.info(
      "benchmark registerSchema cost = "
        + (System.currentTimeMillis() - start))
    start = System.currentTimeMillis()
    blocks
  }

  private def planToBlock(query: String, param: Map[String, Object] = Map.empty): List[Block] = {
    // parser query to unresolved logical plan
    // check
    val blocks = parser.parseMultipleStatement(query, param)
    // parameter parse
    variableParameters = parser.getAllParameters()
    idFilterParameters = parser.getIdFilterParameters()
    if (blocks.size == 1) {
      blocks
    } else {
      legalMultiStatement(blocks)
      blocks
    }
  }

  /**
   * Only the last block can be result block
   *
   * @param blocks
   * @return
   */
  private def legalMultiStatement(blocks: List[Block]) = {
    for (i <- 0 until blocks.size) {
      val block = blocks(i)
      if (i < blocks.size - 1 && !block.isInstanceOf[DDLBlock]) {
        throw UnsupportedOperationException(
          "multiple statement requires the only the last statement is get action")
      } else if (i == blocks.size - 1 && !block.isInstanceOf[TableResultBlock]) {
        throw UnsupportedOperationException(
          "multiple statement requires the only the last statement is get action")
      }
    }
  }

  private def registerSchema(blocks: List[Block]) = {
    for (block <- blocks) {
      block match {
        case DDLBlock(ddlOps, _) =>
          val ddlOp = ddlOps.head
          ddlOp match {
            case AddProperty(s, propertyName, propertyType) =>
              val graph = catalog.getGraph(block.graph.graphName)
              for (label <- s.typeNames) {
                graph.addProperty(label, propertyName, propertyType, false)
              }
            case AddPredicate(predicate) =>
              val graph = catalog.getGraph(block.graph.graphName)
              predicate match {
                case PredicateElement(label, _, src: Element, dst: Element, fields, direction) =>
                  val srcTypes = getType(src)
                  val dstTypes = getType(dst)
                  for (s <- srcTypes) {
                    for (o <- dstTypes) {
                      graph.addEdge(
                        s,
                        label,
                        o,
                        direction,
                        fields.keySet.map(name => new Field(name, KTObject, false)),
                        false)
                    }
                  }
                  if (src.isInstanceOf[EntityElement]) {
                    srcTypes.foreach(graph.addNode(_, NodeType.CONCEPT, Set.empty))
                  }
                  if (dst.isInstanceOf[EntityElement]) {
                    dstTypes.foreach(graph.addNode(_, NodeType.CONCEPT, Set.empty))
                  }
              }
            case _ =>
          }
        case _ =>
      }
    }
  }

  private def getType(element: Element): Set[String] = {
    element match {
      case EntityElement(id, label, _) => Set.apply(label + "/" + id, label)
      case PatternElement(_, typeNames, _) => typeNames
    }
  }

  private def plan2PhysicalPlan(
      logicalPlanList: List[LogicalOperator],
      params: Map[String, Object]): List[PhysicalOperator[T]] = {
    val physicalPlanList = new ListBuffer[PhysicalOperator[T]]()
    for (logicalPlan: LogicalOperator <- logicalPlanList) {
      val physicalPlan =
        PhysicalPlanner
          .plan[T](logicalPlan)(
            implicitly[TypeTag[T]],
            PhysicalPlannerContext(catalog, graphSession, params))
      physicalPlanList.+=(physicalPlan)
      if (ParameterUtils.isEnableSPGPlanPrettyPrint(params)) {
        logger.info(s"kgreasoner physical plan:\n${physicalPlan.pretty}\n")
      }
    }
    physicalPlanList.toList
  }

  /**
   * get the graph loader config from query dsl
   * @param query
   * @param params
   * @return
   */
  def getGraphLoaderConfig(query: String, params: Map[String, Object]): GraphLoaderConfig = {
    val blocks = plan2UnresolvedLogicalPlan(query, params)
    val optimizedLogicalPlan = plan2LogicalPlan(blocks, params)
    LoaderUtil.getLoaderConfig(optimizedLogicalPlan, catalog)
  }

}
