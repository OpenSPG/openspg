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

import scala.language.implicitConversions

import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.rule.{LogicRule, Rule}

/**
 * Entity or concept
 */
trait Element extends Serializable {
  def alias: String

  def typeNames: Set[String]

  def rule: Rule
}

/**
 * represents a node， like "(A:label)"
 * @param alias
 * @param typeNames
 * @param rule
 */
case class PatternElement(alias: String, typeNames: Set[String], var rule: Rule) extends Element {

  override def toString: String = {
    val stringBuilder = StringBuilder.newBuilder
    stringBuilder.append("(").append(alias).append(":")
    stringBuilder.append(typeNames.mkString(","))
    if (rule != null) {
      stringBuilder.append(",").append(rule.getExpr.toString)
    }
    stringBuilder.append(")")
    stringBuilder.toString()
  }

}

/**
 * represents a entity instance,we can use id to recall instance
 * @param id
 * @param label
 * @param patternElement
 */
case class EntityElement(id: String, label: String, alias: String) extends Element {

  override def toString: String = {
    "EntityElement(%s,%s,%s)".format(id, label, alias)
  }

  override def typeNames: Set[String] = Set.apply(s"$label/$id")

  override def rule: Rule = null
}

object ElementOps {
  implicit def toPattenElement(element: Element): PatternElement = {
    element match {
      case EntityElement(id, label, alias) =>
        val rule = BinaryOpExpr(BEqual, UnaryOpExpr(GetField("id"), Ref(alias)), VString(id))
        PatternElement(alias, Set.apply(label), LogicRule(s"R_$alias", "", rule))
      case patternElement: PatternElement => patternElement
    }
  }
}
