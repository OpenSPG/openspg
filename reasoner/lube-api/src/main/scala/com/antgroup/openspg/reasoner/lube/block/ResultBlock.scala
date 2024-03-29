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

package com.antgroup.openspg.reasoner.lube.block

import com.antgroup.openspg.reasoner.common.types.KgType
import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.common.pattern.{Element, PatternElement, PredicateElement}

/**
 * every operator block tree of root is result block
 */
abstract class ResultBlock[B <: Binds] extends BasicBlock[B](BlockType("result"))

/**
 * output as table
 * @param dependencies
 * @param selectList
 * @param graph
 */
final case class TableResultBlock(
    dependencies: List[Block],
    selectList: OrderedFields,
    asList: List[String],
    distinct: Boolean)
    extends ResultBlock[OrderedFields] {

  /**
   * The metadata output by the current block
   *
   * @return
   */
  override def binds: OrderedFields = selectList

  override def withNewChildren(newChildren: Array[Block]): Block = {
    this.copy(dependencies = newChildren.toList)
  }

}

/**
 * output as graph
 * @param dependencies
 * @param outputGraphPath the path name array for output
 * @param graph
 */
final case class GraphResultBlock(dependencies: List[Block], outputGraphPath: List[String])
    extends ResultBlock[Binds] {
  override val binds: Binds = dependencies.head.binds

  override def withNewChildren(newChildren: Array[Block]): Block = {
    this.copy(dependencies = newChildren.toList)
  }

}

/**
 * DDL operator set
 */
sealed trait DDLOp

/**
 * like "(A:label)-[p:property_name]->(V:String)",will convert to add property operator
 * @param s
 * @param propertyName
 * @param propertyType
 */
case class AddProperty(s: Element, propertyName: String, propertyType: KgType) extends DDLOp

/**
 * add vertex in graph state.
 *
 * @param s
 * @param props
 */
case class AddVertex(s: PatternElement, props: Map[String, Expr]) extends DDLOp

/**
 * like "(A:label)-[p:belongTo]->(B:Concept)",will convert to add predicate operator
 * @param predicate
 */
case class AddPredicate(predicate: PredicateElement) extends DDLOp

/**
 * output is add a property or add a predicate instance
 * @param ddlOp
 * @param dependencies
 * @param graph
 */
case class DDLBlock(ddlOp: Set[DDLOp], dependencies: List[Block]) extends ResultBlock[Fields] {

  /**
   * The metadata output by the current block
   *
   * @return
   */
  override def binds: Fields = Fields.empty

  override def withNewChildren(newChildren: Array[Block]): Block =
    this.copy(dependencies = newChildren.toList)

}

final case class OrderedFields(orderedFields: List[IRField] = List.empty) extends Binds {
  override def fields: List[IRField] = orderedFields

}

object OrderedFields {
  def fieldsFrom[E](fields: IRField*): OrderedFields = OrderedFields(fields.toList)

  def unapplySeq(arg: OrderedFields): Option[Seq[IRField]] = Some(arg.orderedFields)
}
