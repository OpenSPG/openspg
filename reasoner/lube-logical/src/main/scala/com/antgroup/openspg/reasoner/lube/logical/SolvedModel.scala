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

import com.antgroup.openspg.reasoner.common.exception.{InvalidRefVariable, UnsupportedOperationException}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.graph.IRVariable

/**
 * SolvedModel holds solved nodes, edges and attributes
 *
 * @param alias2Types alias to types, If the type is an edge type, it is expressed as s_p_o
 * @param fields      alias to Var
 * @param tmpFields   Temporary variables
 */
case class SolvedModel(
    alias2Types: Map[String, Set[String]],
    fields: Map[String, Var],
    tmpFields: Map[IRVariable, PropertyVar]) {

  def addField(field: Tuple2[IRVariable, PropertyVar]): SolvedModel = {
    copy(alias2Types, fields, tmpFields = tmpFields + field)
  }

  def getField(field: IRVariable): PropertyVar = {
    if (!tmpFields.contains(field)) {
      throw InvalidRefVariable(s"not found variable: ${field}")
    }
    tmpFields.get(field).get
  }

  def getVar(name: String): Var = {
    val value = fields.get(name)
    if (value.isDefined) {
      value.get
    } else if (tmpFields.contains(IRVariable(name))) {
      tmpFields(IRVariable(name))
    } else {
      throw InvalidRefVariable(s"not found variable: ${name}")
    }
  }

  def getField(alias: String, fieldName: String): Field = {
    fields(alias) match {
      case NodeVar(_, fields) => fields.filter(_.name.equals(fieldName)).head
      case EdgeVar(_, fields) => fields.filter(_.name.equals(fieldName)).head
      case RepeatPathVar(pathVar, _, _) =>
        pathVar.elements(1).asInstanceOf[EdgeVar].fields.filter(_.name.equals(fieldName)).head
      case _ => throw UnsupportedOperationException(s"connot support ${fields(alias)}")
    }
  }

  def solve: SolvedModel = {
    val tmp = tmpFields.values.map(p => fields(p.name).merge(Option.apply(p))).toList
    var newFields = fields
    for (t <- tmp) {
      newFields = newFields.updated(t.name, newFields(t.name).merge(Option.apply(t)))
    }
    SolvedModel(alias2Types, newFields, tmpFields)
  }

  def getNodeAliasSet: Set[String] = {
    fields.filter(pair => pair._2.isInstanceOf[NodeVar]).map(_._1).toSet
  }

  def getEdgeAliasSet: Set[String] = {
    fields
      .filter(pair => pair._2.isInstanceOf[EdgeVar] || pair._2.isInstanceOf[RepeatPathVar])
      .map(_._1)
      .toSet
  }

  def getTypes(alias: String): Set[String] = {
    alias2Types(alias)
  }

}

object SolvedModel {

  implicit class SolvedModelOps(solvedModel: SolvedModel) {

    implicit def merge(other: SolvedModel): SolvedModel = {
      if (other == null) {
        solvedModel
      } else {
        val fields = fieldsMerge(solvedModel.fields, other.fields)
        solvedModel.copy(fields = fields)
      }
    }

    private def fieldsMerge(
        fields: Map[String, Var],
        other: Map[String, Var]): Map[String, Var] = {
      val varMap = new mutable.HashMap[String, Var]()
      for (field <- fields) {
        varMap.put(field._1, field._2)
      }
      for (field <- other) {
        if (varMap.contains(field._1)) {
          varMap.put(field._1, varMap(field._1).merge(Option.apply(field._2)))
        }
      }
      varMap.toMap
    }

  }

}
