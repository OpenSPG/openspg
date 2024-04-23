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

package com.antgroup.openspg.reasoner.lube.logical.planning

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.{
  NotImplementedException,
  SchemaException,
  UnsupportedOperationException
}
import com.antgroup.openspg.reasoner.common.graph.edge.SPO
import com.antgroup.openspg.reasoner.lube.block._
import com.antgroup.openspg.reasoner.lube.catalog.{Catalog, SemanticPropertyGraph}
import com.antgroup.openspg.reasoner.lube.common.expr.VConstant
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.validate.Dag
import com.antgroup.openspg.reasoner.lube.utils.{BlockUtils, ExprUtils, RuleUtils}

/**
 * Logical planner for KGReasoner, generate an optimal logical plan for KGDSL or GQL.
 * Logical planner generate logical plan for giving unresolved logical plan,
 * which is represented by [[Block]].
 */
object LogicalPlanner {

  /**
   * Generate logical plan for giving unresolved logical plan.
   * @param dag contains a output unresolved logical plan and it's dependencies.
   * @param context
   * @return
   */
  def plan(dag: Dag[Block])(implicit context: LogicalPlannerContext): Dag[LogicalOperator] = {
    val subQueryPlanner = new SubQueryPlanner(dag)
    subQueryPlanner.plan
  }

  /**
   * Generate logical plan for giving unresolved logical plan.
   *
   * @param input unresolved logical plan
   * @return
   */
  def plan(input: Block)(implicit context: LogicalPlannerContext): List[LogicalOperator] = {
    val source = resolve(input)
    val groups = BlockUtils.getStarts(input)
    val planWithoutResult = if (groups.isEmpty) {
      planBlock(input.dependencies.head, input, None, source)
    } else {
      planBlock(input.dependencies.head, input, None, source)(
        context.addParam(Constants.START_ALIAS, groups.head))
    }
    val plan = input match {
      case t: TableResultBlock =>
        Select(
          planWithoutResult,
          t.binds.fields.map(x =>
            x match {
              case IRProperty(name, field) =>
                PropertyVar(name, planWithoutResult.solved.getField(name, field))
              case IRVariable(name) =>
                planWithoutResult.solved.getField(x.asInstanceOf[IRVariable])
              case IRPath(name, elements) =>
                PathVar(name, elements.map(e => planWithoutResult.solved.getVar(e.name)))
              case _ => throw UnsupportedOperationException(s"unsupported ${x}")
            }),
          t.asList,
          t.distinct)
      case d: DDLBlock =>
        val newDDLs = new mutable.HashSet[DDLOp]()
        for (ddl <- d.ddlOp) {
          ddl match {
            case ddlOp @ AddProperty(_, _, _) => newDDLs.add(ddlOp)
            case ddlOp @ AddPredicate(predicate) =>
              val map = predicate.fields.map(tuple => {
                tuple._2 match {
                  case expr: VConstant => tuple
                  case _ =>
                    val ref = ExprUtils.getAllInputFieldInRule(tuple._2, null, null).head
                    ref match {
                      case ir: IRVariable =>
                        val realRef = planWithoutResult.solved.getField(ir)
                        val map: Map[IRField, IRProperty] =
                          Map.apply((ref, IRProperty(realRef.name, realRef.field.name)))
                        val newExpr = ExprUtils.renameVariableInExpr(tuple._2, map)
                        (tuple._1, newExpr)
                      case _ => tuple
                    }
                }
              })
              newDDLs.add(AddPredicate(predicate.copy(fields = map)))
            case ddlOp @ AddVertex(s, props) =>
              val map = props.map(tuple => {
                tuple._2 match {
                  case expr: VConstant => tuple
                  case _ =>
                    val ref = ExprUtils.getAllInputFieldInRule(tuple._2, null, null).head
                    ref match {
                      case ir: IRVariable =>
                        val realRef = planWithoutResult.solved.getField(ir)
                        val map: Map[IRField, IRProperty] =
                          Map.apply((ref, IRProperty(realRef.name, realRef.field.name)))
                        val newExpr = ExprUtils.renameVariableInExpr(tuple._2, map)
                        (tuple._1, newExpr)
                      case _ => tuple
                    }
                }
              })
              newDDLs.add(AddVertex(s, map))
          }
        }
        DDL(planWithoutResult, newDDLs.toSet)
      case other =>
        throw NotImplementedException(
          s"Support for logical planning of $other not yet implemented. Tree:\n${other.pretty}")
    }
    List(plan)
  }

  /**
   * Resolve unresolved logical plan.
   *
   * @param input
   * @return
   */
  private def resolve(input: Block)(implicit context: LogicalPlannerContext): SolvedModel = {
    input.transform[SolvedModel] {
      case (matchBlock @ MatchBlock(_, patterns), _) =>
        val types = patterns.values.head.graphPattern
          .generateGraphPatternTypesBySchema(context.catalog.getGraph(Catalog.defaultGraphName))
        val fields = matchBlock.binds.fields
        fieldToVar(fields, types)
      case (_, list) =>
        if (!list.isEmpty) {
          list.head
        } else {
          null
        }
    }
  }

  private def fieldToVar(fields: List[IRField], types: Map[String, Set[String]])(implicit
      context: LogicalPlannerContext): SolvedModel = {
    val graph = context.catalog.getGraph(Catalog.defaultGraphName)
    val varMap = new mutable.HashMap[String, Var]
    fields.foreach(field => {
      val list = fieldToVar(field, types, graph)
      varMap.put(field.name, list)
    })
    SolvedModel(types, varMap.toMap, Map.empty)
  }

  private def fieldToVar(
      field: IRField,
      types: Map[String, Set[String]],
      graph: SemanticPropertyGraph): Var = {
    field match {
      case node: IRNode =>
        if (types(node.name).isEmpty && node.fields.nonEmpty) {
          throw SchemaException(s"Cannot find ${node.name} in $types")
        }
        val fields = node.fields.map(graph.graphSchema.getNodeField(types(node.name), _))
        NodeVar(node.name, fields)
      case edge: IREdge =>
        if (types(edge.name).isEmpty && edge.fields.nonEmpty) {
          throw SchemaException(s"Cannot find ${edge.name} in $types")
        }
        val fullFields = edge.fields ++ Set.apply(
          Constants.EDGE_FROM_ID_KEY,
          Constants.EDGE_FROM_ID_TYPE_KEY,
          Constants.EDGE_TO_ID_KEY,
          Constants.EDGE_TO_ID_TYPE_KEY)
        val fields = fullFields.map(graph.graphSchema.getEdgeField(types(edge.name), _))
        EdgeVar(edge.name, fields)
      case array: IRRepeatPath =>
        RepeatPathVar(
          fieldToVar(array.element, types, graph).asInstanceOf[PathVar],
          array.lower,
          array.upper)
      case path: IRPath =>
        PathVar(path.name, path.elements.map(p => fieldToVar(p, types, graph)))
      case _ =>
        throw UnsupportedOperationException(s"$field not implement")
    }
  }

  private def planBlock(
      input: Block,
      root: Block,
      plan: Option[LogicalOperator],
      solvedModel: SolvedModel)(implicit context: LogicalPlannerContext): LogicalOperator = {
    if (input.dependencies.isEmpty) {
      planLeaf(input, solvedModel)
    } else {
      // plan one of the block dependencies
      val dependency = planBlock(input.dependencies.head, root, plan, solvedModel)
      planNonLeaf(input, root, solvedModel, dependency)
    }
  }

  private def planLeaf(block: Block, solvedModel: SolvedModel)(implicit
      context: LogicalPlannerContext): LogicalOperator = {
    block match {
      case SourceBlock(graph) =>
        Start(context.catalog.getGraph(graph.graphName), null, Set.empty, solvedModel)
      case other =>
        throw NotImplementedException(
          s"Support for logical planning of $other not yet implemented. Tree:\n${other.pretty}")
    }
  }

  private def planNonLeaf(
      block: Block,
      root: Block,
      solvedModel: SolvedModel,
      plan: LogicalOperator)(implicit context: LogicalPlannerContext): LogicalOperator = {
    block match {
      case MatchBlock(_, matches) =>
        // TODO: plan the first one in current
        planMatchPattern(solvedPattern(matches.head._2, solvedModel), plan)
      case FilterBlock(_, rule) =>
        planFilter(rule, plan)
      case ProjectBlock(_, projects) =>
        planProject(projects, root, plan)
      case AggregationBlock(_, aggregations, group) =>
        planAggregate(aggregations, group, plan)
      case OrderAndSliceBlock(_, orderBy, limit, group) =>
        planOrderAndSlice(orderBy, group.map(NodeVar(_, null)), limit, plan)
      case other =>
        throw NotImplementedException(
          s"Support for logical planning of $other not yet implemented. Tree:\n${other.pretty}")
    }
  }

  private def solvedPattern(path: GraphPath, solvedModel: SolvedModel): GraphPath = {
    val graphPattern = path.graphPattern
    val solvedNodes = new mutable.HashMap[String, Element]()
    graphPattern.nodes.foreach(x =>
      x._2 match {
        case PatternElement(alias, _, rule) =>
          solvedNodes.put(alias, PatternElement(alias, solvedModel.alias2Types(alias), rule))
        case _ =>
          solvedNodes.put(x._1, x._2)
      })

    val solvedEdges = new mutable.HashMap[String, Set[Connection]]()
    graphPattern.edges.foreach(x => {
      var solvedEdgeSets = new mutable.HashSet[Connection]()
      x._2.foreach {
        case c: LinkedPatternConnection =>
          solvedEdgeSets = solvedEdgeSets + c
        case c: PatternConnection =>
          val relTypes = solvedModel
            .alias2Types(c.alias)
            .map(typeName => {
              val spo = new SPO(typeName)
              spo.getP
            })
          solvedEdgeSets = solvedEdgeSets + new PatternConnection(
            c.alias,
            c.source,
            relTypes,
            c.target,
            c.direction,
            c.rule,
            c.limit,
            c.exists,
            c.optional)
        case y => solvedEdgeSets = solvedEdgeSets + y
      }
      solvedEdges.put(x._1, solvedEdgeSets.toSet)
    })
    GraphPath(
      path.pathName,
      GraphPattern(
        graphPattern.rootAlias,
        solvedNodes.toMap,
        solvedEdges.toMap,
        graphPattern.properties),
      path.optional)
  }

  /**
   * QueryGraph splitting
   * @param pattern
   * @param dependency
   * @param context
   * @return
   */
  private def planMatchPattern(pattern: GraphPath, dependency: LogicalOperator)(implicit
      context: LogicalPlannerContext): LogicalOperator = {
    val patternMatchPlanner = new PatternMatchPlanner(pattern.graphPattern)
    patternMatchPlanner.plan(dependency)
  }

  private def planFilter(rule: Rule, dependency: LogicalOperator)(implicit
      context: LogicalPlannerContext): LogicalOperator = {
    val reference =
      RuleUtils
        .getAllInputFieldInRule(
          rule,
          dependency.solved.getNodeAliasSet,
          dependency.solved.getEdgeAliasSet)
        .filter(_.isInstanceOf[IRVariable])
    if (reference.isEmpty) {
      Filter(dependency, rule)
    } else {
      val replaceVar = reference
        .map(varName => (varName.name, dependency.solved.getField(IRVariable(varName.name))))
        .toMap
      Filter(dependency, ExprUtil.transExpr(rule, replaceVar))
    }
  }

  /**
   * Aggregation plan
   * @param aggregations
   * @param group
   * @param dependency
   * @param context
   * @return
   */
  private def planAggregate(
      aggregations: Aggregations,
      group: List[IRField],
      dependency: LogicalOperator)(implicit context: LogicalPlannerContext): LogicalOperator = {
    val aggregationPlanner = new AggregationPlanner(group, aggregations)
    aggregationPlanner.plan(dependency)
  }

  private def planOrderAndSlice(
      sortItem: Seq[SortItem],
      group: List[Var],
      limit: Option[Int],
      dependency: LogicalOperator)(implicit context: LogicalPlannerContext): LogicalOperator = {
    val resolved = dependency.solved
    val sortItemWithNewName = sortItem.map(x => {
      var expr = x.expr
      val alias = ExprUtils.getRefVariableByExpr(expr).head
      if (!resolved.alias2Types.isDefinedAt(alias)) {
        val tmpPropertyVar = resolved.tmpFields(IRVariable(alias))
        expr = ExprUtils.renameVariableInExpr(
          expr,
          Map
            .apply(
              (IRVariable(alias) -> IRProperty(tmpPropertyVar.name, tmpPropertyVar.field.name)))
            .asInstanceOf[Map[IRField, IRProperty]])
      }
      x match {
        case Desc(_) => Desc(expr)
        case Asc(_) => Asc(expr)
      }
    })

    OrderAndLimit(dependency, group, sortItemWithNewName, limit)
  }

  /**
   * @param projects
   * @param dependency
   * @param context
   * @return
   */
  private def planProject(projects: ProjectFields, root: Block, dependency: LogicalOperator)(
      implicit context: LogicalPlannerContext): LogicalOperator = {
    val projectPlanner = new ProjectPlanner(projects, root)
    projectPlanner.plan(dependency)
  }

  private def getStarts(block: Block): Set[String] = {
    block.transform[Set[String]] {
      case (AggregationBlock(_, _, group), groupList) =>
        val groupAlias = group.map(_.name).toSet
        if (groupList.head.isEmpty) {
          groupAlias
        } else {
          val commonGroups = groupList.head.intersect(groupAlias)
          if (commonGroups.isEmpty) {
            throw UnsupportedOperationException(
              s"cannot support groups ${groupAlias}, ${groupList.head}")
          } else {
            commonGroups
          }
        }
      case (DDLBlock(ddlOp, _), list) =>
        val starts = new mutable.HashSet[String]()
        for (ddl <- ddlOp) {
          ddl match {
            case AddProperty(s, _, _) =>
              if (starts.isEmpty) {
                starts.add(s.alias)
              } else {
                val common = starts.intersect(Set.apply(s.alias))
                starts.clear()
                starts.++=(common)
              }
            case AddPredicate(p) =>
              if (starts.isEmpty) {
                starts.++=(Set.apply(p.source.alias, p.target.alias))
              } else {
                val common = starts.intersect(Set.apply(p.source.alias, p.target.alias))
                starts.clear()
                starts.++=(common)
              }
            case _ =>
          }
        }
        if (list.head.isEmpty) {
          starts.toSet
        } else if (starts.isEmpty) {
          list.head
        } else {
          val commonStart = list.head.intersect(starts)
          if (commonStart.isEmpty) {
            throw UnsupportedOperationException(
              s"cannot support non-common starts ${list.head}, ${starts}")
          } else {
            commonStart
          }
        }
      case (SourceBlock(_), _) => Set.empty
      case (_, groupList) => groupList.head
    }
  }

}
