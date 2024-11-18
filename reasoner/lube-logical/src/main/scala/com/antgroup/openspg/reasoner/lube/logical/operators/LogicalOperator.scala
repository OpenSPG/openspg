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

package com.antgroup.openspg.reasoner.lube.logical.operators

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.trees.AbstractTreeNode
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph
import com.antgroup.openspg.reasoner.lube.logical.{SolvedModel, Var}

abstract class LogicalOperator extends AbstractTreeNode[LogicalOperator] {
  /**
   * the binding graph in current operator
   * @return
   */
  def graph: SemanticPropertyGraph

  /**
   * the nodes, edges, attributes has been solved in currently
   * @return
   */
  def solved: SolvedModel

  /**
   * the reference fields in current operator
   * @return
   */
  def refFields: List[Var]

  /**
   * the output fields of current operator
   * @return
   */
  def fields: List[Var]
}

abstract class LogicalLeafOperator extends LogicalOperator {
  override def children: Array[LogicalOperator] = Array.empty

  override def withNewChildren(newChildren: Array[LogicalOperator]): LogicalOperator = {
    throw UnsupportedOperationException("LogicalLeafOperator cannot construct children")
  }
}

abstract class StackingLogicalOperator extends LogicalOperator {
  def in: LogicalOperator

  override def graph: SemanticPropertyGraph = in.graph

  override def children: Array[LogicalOperator] = Array.apply(in)
}

abstract class BinaryLogicalOperator extends LogicalOperator {
  def lhs: LogicalOperator

  def rhs: LogicalOperator

  override def graph: SemanticPropertyGraph = rhs.graph

  override def children: Array[LogicalOperator] = Array.apply(lhs, rhs)
}

trait EmptyFields extends LogicalOperator {
  self: LogicalOperator =>

  override val fields: List[Var] = List.empty
}

trait EmptyRefFields extends LogicalOperator {
  self: LogicalOperator =>

  override val refFields: List[Var] = List.empty
}
