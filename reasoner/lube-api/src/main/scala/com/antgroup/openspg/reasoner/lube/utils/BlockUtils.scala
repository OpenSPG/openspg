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

package com.antgroup.openspg.reasoner.lube.utils

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.graph.edge.SPO
import com.antgroup.openspg.reasoner.lube.block._
import com.antgroup.openspg.reasoner.lube.common.expr.{BEqual, BIn, BinaryOpExpr}
import com.antgroup.openspg.reasoner.lube.common.graph.IRNode
import com.antgroup.openspg.reasoner.lube.common.pattern.GraphPath
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Block2GraphPathTransformer

object BlockUtils {

  def transBlock2Graph(block: Block): List[GraphPath] = {
    val blockTransformer = new Block2GraphPathTransformer()
    blockTransformer.transform(block)
  }

  def getDefine(block: Block): Set[String] = {
    val defines = new mutable.HashSet[String]()
    block match {
      case DDLBlock(ddlOps, _) =>
        ddlOps.foreach(op => {
          op match {
            case AddPredicate(predicate) =>
              defines.add(
                new SPO(
                  predicate.source.typeNames.head,
                  predicate.label,
                  predicate.target.typeNames.head).toString)
            case AddProperty(s, propertyName, _) =>
              defines.add(s.typeNames.head + "." + propertyName)
            case _ =>
          }
        })
      case _ => defines.add("result")
    }
    if (defines.isEmpty) {
      Set.apply("result")
    } else {
      defines.toSet
    }
  }

  def getStarts(block: Block): Set[String] = {
    val start = block.transform[Set[String]] {
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
            case AddProperty(s, _, _) => starts.add(s.alias)
            case AddPredicate(p) =>
              starts.add(p.source.alias)
              starts.add(p.target.alias)
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
    if (start.isEmpty) {
      getFilterStarts(block)
    } else {
      start
    }
  }

  private def getFilterStarts(block: Block): Set[String] = {
    block.transform[Set[String]] {
      case (FilterBlock(_, rule), list) =>
        rule.getExpr match {
          case BinaryOpExpr(BEqual | BIn, _, _) =>
            val irFields = ExprUtils.getAllInputFieldInRule(rule.getExpr, null, null)
            if (irFields.size != 1 || !irFields.head.isInstanceOf[IRNode] || !irFields.head
              .asInstanceOf[IRNode]
              .fields
              .equals(Set.apply(Constants.NODE_ID_KEY))) {
              list.head
            } else {
              if (list.head.isEmpty) {
                Set.apply(irFields.head.name)
              } else {
                val commonStart = list.head.intersect(Set.apply(irFields.head.name))
                if (commonStart.isEmpty) {
                  list.head
                } else {
                  commonStart
                }
              }
            }
          case _ => list.head
        }
      case (SourceBlock(_), _) => Set.empty
      case (_, groupList) => groupList.head
    }
  }

}
