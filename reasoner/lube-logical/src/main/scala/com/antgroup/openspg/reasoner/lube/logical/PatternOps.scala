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

package com.antgroup.openspg.reasoner.lube.logical

import scala.collection.mutable
import scala.language.implicitConversions

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.graph.edge.{Direction, SPO}
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph
import com.antgroup.openspg.reasoner.lube.common.graph.IRNode
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils

/**
 * Operators for pattern extension
 */
object PatternOps {

  implicit class PatternOps(pattern: Pattern) {

    /**
     * convertor used to convert pattern to var
     * @param solved
     * @return
     */
    implicit def toVar(implicit solved: SolvedModel, graph: SemanticPropertyGraph): List[Var] = {
      val varMap = new mutable.HashMap[String, Var]
      varMap.put(pattern.root.alias, toNodeVar(pattern.root))
      for (conn <- pattern.topology.values.flatten) {
        val value = toEdgeVar(conn)
        varMap.put(value.name, value)
        varMap.put(conn.target, NodeVar(conn.target, Set.empty))
      }
      varMap.++=(pattern.topology.values.flatten.map(toEdgeVar(_)).map(v => (v.name, v)))
      val rules = new mutable.HashSet[Rule]()
      if (pattern.root.rule != null) {
        rules.add(pattern.root.rule)
      }
      pattern.topology.values.flatten.filter(_.rule != null).foreach(r => rules.add(r.rule))
      val types = pattern.patternTypes()
      for (rule <- rules) {
        val nodes = RuleUtils
          .getAllInputFieldInRule(rule, null, null)
          .filter(_.isInstanceOf[IRNode])
          .map(_.asInstanceOf[IRNode])
        for (node <- nodes) {
          varMap(node.name) match {
            case NodeVar(name, _) =>
              val fields =
                node.fields.map(graph.graphSchema.getNodeField(types(name), _)).toSet
              val newVar = varMap(name).merge(Option.apply(NodeVar(name, fields)))
              varMap.put(name, newVar)
            case EdgeVar(name, _) =>
              val fields =
                node.fields.map(graph.graphSchema.getEdgeField(types(name), _)).toSet
              val newVar = varMap(name).merge(Option.apply(EdgeVar(name, fields)))
              varMap.put(name, newVar)
            case _ =>
          }
        }
      }
      varMap.values.toList
    }

    implicit def removeConnection(source: String, target: String): Pattern = {
      pattern match {
        case PartialGraphPattern(rootAlias, nodes, topology) =>
          val newConnections = new mutable.HashSet[Connection]()
          for (conn <- topology(rootAlias)) {
            if (!conn.target.equals(target)) {
              newConnections.add(conn)
            }
          }
          if (newConnections.isEmpty) {
            NodePattern(pattern.root)
          } else {
            PartialGraphPattern(rootAlias, nodes, Map.apply((rootAlias -> newConnections.toSet)))
          }
        case _ => throw UnsupportedOperationException(s"Unsupport $pattern")
      }
    }

    def patternTypes(): Map[String, Set[String]] = {
      pattern match {
        case PartialGraphPattern(_, nodes, edges) =>
          val typeMap = new mutable.HashMap[String, Set[String]]()
          nodes.foreach(node => {
            val sourceSet = node._2.typeNames
            typeMap.put(node._2.alias, sourceSet)
          })

          for (edge <- edges) {
            edge._2
              .filter(!_.isInstanceOf[LinkedPatternConnection])
              .foreach(conn => {
                val sourceSet = typeMap(conn.source)
                val typeSet = conn.relTypes
                val targetSet = typeMap(conn.target)
                val spoSet = sourceSet.flatMap(x =>
                  typeSet.flatMap(y =>
                    targetSet.map(z => {
                      if (conn.direction == Direction.OUT) {
                        new SPO(x, y, z).toString
                      } else {
                        new SPO(z, y, x).toString
                      }
                    })))
                typeMap.put(conn.alias, spoSet)
              })
          }
          typeMap.toMap
        case _ =>
          val typeMap = new mutable.HashMap[String, Set[String]]()
          val sourceSet = pattern.root.typeNames
          typeMap.put(pattern.root.alias, sourceSet)
          for (edge <- pattern.topology) {
            edge._2.foreach(conn => {
              val typeSet = conn.relTypes
              val targetSet = pattern.getNode(conn.target).typeNames
              val spoSet = sourceSet.flatMap(s =>
                typeSet.flatMap(p => targetSet.map(o => new SPO(s, p, o).toString)))
              typeMap.put(conn.alias, spoSet)
            })
          }
          typeMap.toMap
      }

    }

    private def toNodeVar(node: PatternElement)(implicit solved: SolvedModel): Var = {
      solved.fields.get(node.alias).get
    }

    private def toEdgeVar(conn: Connection)(implicit solved: SolvedModel): Var = {
      solved.fields.get(conn.alias).get
    }

    /**
     * Fill rule in pattern
     * @param rule
     * @param alias
     * @return
     */
    implicit def fillInRule(rule: Rule, alias: String): Pattern = {
      pattern match {
        case NodePattern(node) =>
          NodePattern(fillInPatternElement(node, rule))
        case EdgePattern(src, dst, edge) =>
          EdgePattern(src, dst, fillInPatternConnection(edge, rule))
        case partialGraphPattern @ PartialGraphPattern(rootAlias, nodes, edges) =>
          fillInPartialGraphPattern(partialGraphPattern, alias, rule)
      }
    }

    private def fillInPatternElement(node: PatternElement, rule: Rule): PatternElement = {
      PatternElement(node.alias, node.typeNames, ruleMerge(rule, node.rule))
    }

    private def fillInPatternConnection(edge: Connection, rule: Rule): Connection = {
      edge.update(ruleMerge(rule, edge.rule))
    }

    private def fillInPartialGraphPattern(
        pattern: PartialGraphPattern,
        alias: String,
        rule: Rule): PartialGraphPattern = {
      val rootAlias = pattern.rootAlias
      val nodes = pattern.nodes
      val edges = pattern.edges
      if (rootAlias.contains(alias)) {
        val root = fillInPatternElement(nodes(rootAlias), rule)
        val newNodes = new mutable.HashMap[String, PatternElement]()
        newNodes.++=(nodes)
        newNodes.remove(rootAlias)
        newNodes.put(rootAlias, root)
        PartialGraphPattern(rootAlias, newNodes.toMap, edges)
      } else {
        val newEdges: Map[String, Set[Connection]] = edges.map(pair =>
          (
            pair._1,
            pair._2.map(rel => {
              if (rel.alias.equals(alias)) {
                rel.update(ruleMerge(rule, rel.rule))
              } else {
                rel
              }
            })))
        PartialGraphPattern(rootAlias, nodes, newEdges)
      }
    }

    private def ruleMerge(rule: Rule, oldRule: Rule): Rule = {
      if (oldRule == null) {
        rule
      } else {
        rule.andRule(oldRule)
      }
    }

  }

}
