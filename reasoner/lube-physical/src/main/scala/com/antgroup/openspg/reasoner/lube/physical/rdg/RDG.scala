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

package com.antgroup.openspg.reasoner.lube.physical.rdg

import com.antgroup.openspg.reasoner.lube.block.{DDLOp, SortItem}
import com.antgroup.openspg.reasoner.lube.common.expr.{Aggregator, Expr}
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  EdgePattern,
  LinkedPatternConnection,
  Pattern,
  PatternElement
}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.logical.{RichVar, Var}
import com.antgroup.openspg.reasoner.lube.logical.planning.JoinType

/**
 * Resilient Distributed Graphs
 * Main abstraction of tabular based engine.
 * operators need to be implemented by specific engine.(eg geaflow/spark/blink)
 */
abstract class RDG[T <: RDG[T]] extends Result {
  this: T =>

  type Records <: Row[T]

  /**
   * Match the giving pattern on Graph
   * @param pattern specific pattern, see more [[Pattern]]
   * @return
   */
  def patternScan(pattern: Pattern): T

  /**
   * Grow a new pattern from the matched patterns, which means that the new pattern is connected
   * to the matched patterns.
   * @param target the target of growth
   * @param pattern specific pattern, see more [[Pattern]]
   * @return
   */
  def expandInto(target: PatternElement, pattern: Pattern): T

  /**
   * Returns a tabular data containing only the given columns.
   * The column order within the table is aligned with the argument.
   * @param cols columns to select.
   * @return
   */
  def select(cols: List[Var], as: List[String]): Records

  /**
   * Returns a [[RDG]] containing only data where the given expression evaluates to
   * @param expr filter expression
   * @return
   */
  def filter(expr: Rule): T

  /**
   * returns a [[RDG]] that is ordered by the given columns.
   * @param groupKey query variables to group, return global sort if groupKey is empty
   * @param sortItems a sequence of column names and their order
   *                  (i.e.[[com.antgroup.openspg.reasoner.lube.block.Asc]]
   *                  [[com.antgroup.openspg.reasoner.lube.block.Desc]]
   * @param limit return limit number of sorted data
   * @return
   */
  def orderBy(groupKey: List[Var], sortItems: List[SortItem], limit: Int = 100): T

  /**
   * Groups the intermediate data by the given query variables.
   * Additionally a set of aggregations can be apply on the grouped [[RDG]].
   *
   * @param by query variables to group by.
   * @param aggregations map of aggregations functions, the key of map is [[Var]] of output
   * @return
   */
  def groupBy(by: List[Var], aggregations: Map[Var, Aggregator]): T

  /**
   * Add fields in [[RDG]]
   * @param fields the key of map is [[Var]] of output
   * @return
   */
  def addFields(fields: Map[Var, Expr]): T

  /**
   * Remove fields in [[RDG]] according to fields.
   * @param fields
   * @return
   */
  def dropFields(fields: Set[Var]): T

  /**
   * Returns a [[RDG]] containing the first n rows of the current RDG.
   * @param n number of rows to return.
   * @return
   */
  def limit(n: Long): T

  /**
   * Print the result, usually used for debug.
   * @param rows number of rows to print
   */
  def show(rows: Int = 20): Unit

  /**
   * Do DDL operations on the basis of GraphState, such as adding vertices,
   * adding attributes to vertex, adding edges, etc. The main goal is to reuse.
   * @param ddlOps
   * @return
   */
  def ddl(ddlOps: List[DDLOp]): T

  /**
   * compute the linked edge based on pattern
   *
   * @param pattern
   * @return
   */
  def linkedExpand(pattern: EdgePattern[LinkedPatternConnection]): T

  /**
   * cache current RDG
   */
  def cache(): T

  /**
   * Join current rdg with an other rdg
   * @param other other rdg
   * @param joinType see more [[JoinType]]
   * @param onAlias Joining two RDGs based on onAlias
   * @param lhsSchemaMapping left rdg schema mapping after join.
   * @param rhsSchemaMapping right rdg schema mapping after join.
   */
  def join(
      other: T,
      joinType: JoinType,
      onAlias: List[(String, String)],
      lhsSchemaMapping: Map[Var, Var],
      rhsSchemaMapping: Map[Var, Var]): T

  /**
   * fold left var to the right var.
   * eg.
   * a RDG with schema "A -[E]-> B -[F1] -> C", then fold [ [F1,C]->PathVar(F) ]
   * the schema with output rdg is
   * [ NodeVar(A), EdgeVar(E), NodeVar(B), EdgeVar(F1), NodeVar(C),
   * PathVar(F,[EdgeVar(F1), NodeVar(C)]) ]
   *
   * the data of E1 merge in E, B1 merge in B
   * @param foldMapping
   * @return
   */
  def fold(foldMapping: List[(List[Var], RichVar)]): T

  /**
   * The "unfold" operation is the inverse operation of "fold",
   * which expands a RichVar into a list of Vars.
   * @param foldMapping
   * @return
   */
  def unfold(mapping: List[(RichVar, List[Var])]): T

  /**
   * Merge another RDG with current RDG.
   * @param other
   * @return
   */
  def union(other: T): T
}
