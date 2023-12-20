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

package com.antgroup.openspg.reasoner.lube.logical

import scala.collection.mutable
import scala.language.implicitConversions

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field

sealed trait Var {
  def name: String
  def isEmpty: Boolean

  def rename(name: String): Var
}

sealed trait RichVar extends Var

/**
 * Used to represent node in QueryGraph, the name of VarNode is represented by alias
 * @param name alias
 * @param fields the field names of node
 */
case class NodeVar(name: String, fields: Set[Field]) extends Var {
  override def isEmpty: Boolean = if (fields == null) true else fields.isEmpty

  override def rename(name: String): Var = copy(name = name)
}

/**
 * Used to represent Edge in QueryGraph, the name of VarEdge is represented by alias
 * @param name alias
 * @param fields the field names of edge
 */
case class EdgeVar(name: String, fields: Set[Field]) extends Var {
  override def isEmpty: Boolean = if (fields == null) true else fields.isEmpty

  override def rename(name: String): Var = copy(name = name)
}

/**
 * Used to represent prop of [[NodeVar]] and [[EdgeVar]]
 * @param name the alias of NodeVar and EdgeVar
 * @param field the specific filed of NodeVar and EdgeVar
 */
case class PropertyVar(name: String, field: Field) extends Var {
  override def isEmpty: Boolean = (field == null)

  override def rename(name: String): Var = copy(name = name)
}

case class PathVar(name: String, elements: List[Var]) extends RichVar {
  override def isEmpty: Boolean = (elements == null) || elements.isEmpty

  override def rename(name: String): Var = copy(name = name)
}

case class RepeatPathVar(pathVar: PathVar, lower: Int, upper: Int) extends RichVar {
  override def isEmpty: Boolean = pathVar.isEmpty

  override def name: String = pathVar.name

  override def rename(name: String): Var =
    RepeatPathVar(pathVar.rename(name).asInstanceOf[PathVar], lower, upper)

}

/**
 * Used to represent variable during compute or data in row format.
 * @param name
 */
case class Variable(field: Field) extends Var {
  override def name: String = field.name

  override def isEmpty: Boolean = (field == null)

  override def rename(name: String): Var = throw UnsupportedOperationException("unsupport")
}

/**
 * A variable representing an external input as parameter.
 * @param name
 */
case class Parameter(field: Field) extends Var {
  override def name: String = field.name

  override def isEmpty: Boolean = (field == null)

  override def rename(name: String): Var = throw UnsupportedOperationException("unsupport")
}

object Var {

  implicit class VarOps(field: Var) {

    implicit def merge(other: Option[Var]): Var = {
      if (other.isEmpty) {
        return field
      }
      if (field.isInstanceOf[PropertyVar]) {
        throw UnsupportedOperationException(s"Unsupport $field")
      }

      field match {
        case NodeVar(name, fields) =>
          val props = new mutable.HashSet[Field]
          props.++=(fields)
          props.++=(getFields(other.get))
          NodeVar(name, props.toSet)
        case EdgeVar(name, fields) =>
          val props = new mutable.HashSet[Field]
          props.++=(fields)
          props.++=(getFields(other.get))
          EdgeVar(name, props.toSet)
        case PathVar(name, elements) =>
          PathVar(name, elements ++ (other.asInstanceOf[PathVar].elements))
        case RepeatPathVar(_, _, _) =>
          field
        case _ => throw UnsupportedOperationException(s"Unsupport $field")
      }

    }

    implicit def intersect(other: Var): Var = {
      field match {
        case NodeVar(name, fields) =>
          NodeVar(name, fields.intersect(other.asInstanceOf[NodeVar].fields))
        case EdgeVar(name, fields) =>
          EdgeVar(name, fields.intersect(other.asInstanceOf[EdgeVar].fields))
        case PathVar(name, elements) =>
          PathVar(name, elements.intersect(other.asInstanceOf[PathVar].elements))
        case RepeatPathVar(_, _, _) =>
          field
        case _ => throw UnsupportedOperationException(s"Unsupport $field")
      }
    }

    implicit def diff(other: Var): Var = {
      field match {
        case NodeVar(name, fields) =>
          NodeVar(name, fields.diff(other.asInstanceOf[NodeVar].fields))
        case EdgeVar(name, fields) =>
          EdgeVar(name, fields.diff(other.asInstanceOf[EdgeVar].fields))
        case PathVar(name, elements) =>
          PathVar(name, elements.diff(other.asInstanceOf[PathVar].elements))
        case RepeatPathVar(_, _, _) =>
          field
        case _ => throw UnsupportedOperationException(s"Unsupport $field")
      }
    }

    private def getFields(v: Var): Set[Field] = {
      v match {
        case NodeVar(name, fields) => fields
        case EdgeVar(name, fields) => fields
        case PropertyVar(name, field) => Set.apply(field)
        case _ => Set.empty
      }
    }

  }

}
