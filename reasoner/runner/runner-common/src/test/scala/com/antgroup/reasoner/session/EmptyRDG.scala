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

package com.antgroup.reasoner.session

import com.antgroup.openspg.reasoner.lube.block.{DDLOp, SortItem}
import com.antgroup.openspg.reasoner.lube.common.expr.{Aggregator, Expr}
import com.antgroup.openspg.reasoner.lube.common.pattern.{EdgePattern, LinkedPatternConnection, Pattern, PatternElement}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.logical.planning.JoinType
import com.antgroup.openspg.reasoner.lube.logical.{RichVar, Var}
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

class EmptyRDG extends RDG[EmptyRDG] {
  override type Records = EmptyRow

  /**
   * Match the giving pattern on Graph
   *
   * @param pattern specific pattern, see more [[Pattern]]
   * @return
   */
  override def patternScan(pattern: Pattern): EmptyRDG = this

  /**
   * Grow a new pattern from the matched patterns, which means that the new pattern is connected
   * to the matched patterns.
   *
   * @param target  the target of growth
   * @param pattern specific pattern, see more [[Pattern]]
   * @return
   */
  override def expandInto(target: PatternElement, pattern: Pattern): EmptyRDG = this

  /**
   * Returns a tabular data containing only the given columns.
   * The column order within the table is aligned with the argument.
   *
   * @param cols columns to select.
   * @return
   */
  override def select(cols: List[Var], as: List[String]): EmptyRow = new EmptyRow(cols, this)

  /**
   * Returns a [[RDG]] containing only data where the given expression evaluates to
   *
   * @param expr filter expression
   * @return
   */
  override def filter(expr: Rule): EmptyRDG = this

  /**
   * returns a [[RDG]] that is ordered by the given columns.
   *
   * @param groupKey  query variables to group, return global sort if groupKey is empty
   * @param sortItems a sequence of column names and their order
   *                  (i.e.[[com.antgroup.openspg.reasoner.lube.block.Asc]]
   *                  [[com.antgroup.openspg.reasoner.lube.block.Desc]]
   * @param limit     return limit number of sorted data
   * @return
   */
  override def orderBy(groupKey: List[Var], sortItems: List[SortItem], limit: Int): EmptyRDG =
    this

  /**
   * Groups the intermediate data by the given query variables.
   * Additionally a set of aggregations can be apply on the grouped [[RDG]].
   *
   * @param by           query variables to group by.
   * @param aggregations map of aggregations functions, the key of map is [[Var]] of output
   * @return
   */
  override def groupBy(by: List[Var], aggregations: Map[Var, Aggregator]): EmptyRDG = this

  /**
   * Add fields in [[RDG]]
   *
   * @param fields the key of map is [[Var]] of output
   * @return
   */
  override def addFields(fields: Map[Var, Expr]): EmptyRDG = this

  /**
   * Remove fields in [[RDG]] according to fields.
   *
   * @param fields
   * @return
   */
  override def dropFields(fields: Set[Var]): EmptyRDG = this

  /**
   * Returns a [[RDG]] containing the first n rows of the current RDG.
   *
   * @param n number of rows to return.
   * @return
   */
  override def limit(n: Long): EmptyRDG = this

  /**
   * Print the result, usually used for debug.
   *
   * @param rows number of rows to print
   */
  override def show(rows: Int): Unit = {}

  /**
   * compute the linked edge based on pattern
   *
   * @param pattern
   * @return
   */
  override def linkedExpand(pattern: EdgePattern[LinkedPatternConnection]): EmptyRDG = this

  /**
   * cache current RDG
   */
  override def cache(): EmptyRDG = this

  /**
   * Do DDL operations on the basis of GraphState, such as adding vertices,
   * adding attributes to vertex, adding edges, etc. The main goal is to reuse.
   *
   * @param ddlOps
   * @return
   */
  override def ddl(ddlOps: List[DDLOp]): EmptyRDG = this

  /**
   * fold left var to the right var.
   * eg.
   * a RDG with schema "A -[E]-> B -[E1] -> B1", then fold ((E1 -> E, B1 -> B))
   * the schema with output rdg is "A -[E]-> B"
   * the data of E1 merge in E, B1 merge in B
   *
   * @param windMapping
   * @return
   */
  def fold(foldMapping: List[(List[Var], RichVar)]): EmptyRDG = this

  /**
   * Join current rdg with an other rdg
   *
   * @param other            other rdg
   * @param joinType         see more [[JoinType]]
   * @param onAlias          Joining two RDGs based on onAlias
   * @param lhsSchemaMapping left rdg schema mapping after join.
   * @param rhsSchemaMapping right rdg schema mapping after join.
   */
  override def join(
      other: EmptyRDG,
      joinType: JoinType,
      onAlias: List[(String, String)],
      lhsSchemaMapping: Map[Var, Var],
      rhsSchemaMapping: Map[Var, Var]): EmptyRDG = this

  override def unfold(mapping: List[(RichVar, List[Var])]): EmptyRDG = this

  /**
   * Merge another RDG with current RDG.
   *
   * @param other
   * @return
   */
  override def union(other: EmptyRDG): EmptyRDG = this
}
