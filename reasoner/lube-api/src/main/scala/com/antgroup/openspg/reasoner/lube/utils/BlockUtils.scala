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

import com.antgroup.openspg.reasoner.common.graph.edge.SPO
import com.antgroup.openspg.reasoner.lube.block._
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
            case AddVertex(s, _) =>
              // defines.add(s.typeNames.head)
              return Set.apply("result")
            case _ =>
          }
        })
      case _ => defines.add("result")
    }
    defines.toSet
  }

}
