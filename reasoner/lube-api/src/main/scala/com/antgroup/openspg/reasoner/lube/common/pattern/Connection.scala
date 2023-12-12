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

package com.antgroup.openspg.reasoner.lube.common.pattern

import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

abstract class Connection extends Serializable {
  def source: String
  def target: String
  def alias: String
  def relTypes: Set[String]
  def direction: Direction
  def limit: Integer
  def rule: Rule
  def update(source: String, target: String): Connection
  def update(rule: Rule): Connection

  def update(direction: Direction): Connection

  def reverse: Connection = {
    val newDirection: Direction = {
      direction match {
        case Direction.OUT =>
          Direction.IN
        case Direction.IN =>
          Direction.OUT
        case Direction.BOTH =>
          Direction.BOTH
      }
    }
    update(target, source).update(newDirection)
  }

}

/**
 * default relation type
 * @param alias
 * @param source
 * @param relTypes
 * @param target
 * @param direction
 * @param rule
 * @param limit
 * @param exists
 * @param optional
 */
case class PatternConnection(
    alias: String,
    source: String,
    relTypes: Set[String],
    target: String,
    direction: Direction,
    rule: Rule,
    limit: Integer = -1,
    exists: Boolean = true,
    optional: Boolean = false)
    extends Connection {

  override def toString: String = {
    val stringBuilder = StringBuilder.newBuilder
    stringBuilder.append("(").append(source)
    direction match {
      case Direction.OUT =>
        stringBuilder.append(")->[")
      case Direction.IN =>
        stringBuilder.append(")<-[")
      case Direction.BOTH =>
        stringBuilder.append(")<->[")
    }
    stringBuilder.append(alias).append(":").append(relTypes.mkString(",")).append("]-")
    stringBuilder.append("(").append(target).append(")")
    if (rule != null) {
      stringBuilder.append(",").append(rule.getExpr.toString)
    }
    stringBuilder.append(")")
    stringBuilder.toString()
  }

  override def update(source: String, target: String): Connection =
    copy(source = source, target = target)

  override def update(rule: Rule): Connection = copy(rule = rule)

  override def update(direction: Direction): Connection = copy(direction = direction)
}

/**
 * linked relation type
 *
 * @param alias
 * @param source
 * @param relTypes
 * @param funcName
 * @param params
 * @param target
 * @param direction
 * @param rule
 * @param limit
 * @param exists
 * @param optional
 */
case class LinkedPatternConnection(
    alias: String,
    source: String,
    relTypes: Set[String] = Set.empty,
    funcName: String,
    params: List[Expr],
    target: String,
    direction: Direction,
    rule: Rule = null,
    limit: Integer = -1)
    extends Connection {

  override def toString: String = {
    val stringBuilder = StringBuilder.newBuilder
    stringBuilder.append("(").append(source)
    direction match {
      case Direction.OUT =>
        stringBuilder.append(")->[")
      case Direction.IN =>
        stringBuilder.append(")<-[")
      case Direction.BOTH =>
        stringBuilder.append(")<->[")
    }
    stringBuilder
      .append(alias)
      .append(":")
      .append(funcName)
      .append("(")
      .append(params.map(expr => expr.toString).mkString(","))
      .append(")")
      .append("]-")
    stringBuilder.append("(").append(target).append(")")
    stringBuilder.append(")")
    stringBuilder.toString()
  }

  override def update(source: String, target: String): Connection =
    copy(source = source, target = target)

  override def update(rule: Rule): Connection = copy(rule = rule)

  override def update(direction: Direction): Connection = copy(direction = direction)

}

/**
 * relation with repeat feature
 *
 * @param alias
 * @param source
 * @param relTypes
 * @param target
 * @param direction
 * @param rule
 * @param limit
 * @param lower
 * @param upper
 */
case class VariablePatternConnection(
    alias: String,
    source: String,
    relTypes: Set[String],
    target: String,
    direction: Direction,
    rule: Rule,
    limit: Integer = -1,
    lower: Int,
    upper: Int)
    extends Connection {

  override def toString: String = {
    val stringBuilder = StringBuilder.newBuilder
    stringBuilder.append("(").append(source)
    direction match {
      case Direction.OUT =>
        stringBuilder.append(")->[")
      case Direction.IN =>
        stringBuilder.append(")<-[")
      case Direction.BOTH =>
        stringBuilder.append(")<->[")
    }
    stringBuilder.append(alias).append(":").append(relTypes.mkString(",")).append("]-")
    stringBuilder.append("(").append(target).append(")")
    if (rule != null) {
      stringBuilder.append(",").append(rule.getExpr.toString)
    }
    stringBuilder.append(s",[$lower,$upper]")
    stringBuilder.append(")")
    stringBuilder.toString()
  }

  override def update(source: String, target: String): Connection =
    copy(source = source, target = target)

  override def update(rule: Rule): Connection = copy(rule = rule)

  override def update(direction: Direction): Connection = copy(direction = direction)
}

object PatternConnection {

  // make sure PatternConnection source is always root alias
  def insureDirection(rootAlias: String, pc: Connection): Connection = {
    if (rootAlias.equals(pc.source)) {
      pc
    } else if (rootAlias.equals(pc.target)) {
      pc.reverse
    } else {
      null
    }
  }

}

/**
 * represents a edge instance
 * @param label
 * @param alias
 * @param source
 * @param target
 * @param direction
 */
case class PredicateElement(
    label: String,
    alias: String,
    source: Element,
    target: Element,
    fields: Map[String, Expr],
    direction: Direction) {

  override def toString: String = {
    "PredicateElement(%s,%s,%s,%s,Map(%s),%s)".format(
      label,
      alias,
      source.toString,
      target.toString,
      fields.map(x => "%s->%s".format(x._1, x._2.toString)).mkString(","),
      direction.toString)
  }

}
