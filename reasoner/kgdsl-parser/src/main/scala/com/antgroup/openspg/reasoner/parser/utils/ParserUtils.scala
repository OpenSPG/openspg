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

package com.antgroup.openspg.reasoner.parser.utils

import com.antgroup.openspg.reasoner.common.Utils
import com.antgroup.openspg.reasoner.common.table.Field
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock, TableResultBlock}
import scala.collection.JavaConverters._
import scala.collection.mutable

object ParserUtils {

  def getResultTableColumns(block: Block, param: Map[String, Object]): List[Field] = {
    block match {
      case trb: TableResultBlock =>
        Utils.getResultTableColumns(
          trb.asList,
          if (Utils.getForceOutputString(param)) {
            trb.asList.map(_ => KTString)
          } else {
            List.empty
          })
      case _ => null
    }
  }

  def getAllEntityName(javaBlockList: java.util.List[Block]): java.util.Set[String] = {
    val blockList: List[Block] = javaBlockList.asScala.toList
    val entityNames = new mutable.HashSet[String]()
    if (blockList != null && blockList.nonEmpty) {
      blockList.foreach(block => {
        block.transform[Unit] {
          case (MatchBlock(_, patterns), _) =>
            patterns.values
              .map(_.graphPattern.nodes)
              .flatMap(_.values)
              .foreach(node => entityNames ++= node.typeNames)
          case _ =>
        }
      })
    }
    entityNames.toSet.asJava
  }

  def checkDistinctGet(dsl: String): Boolean = {
    if (null == dsl || dsl.isEmpty) {
      return false
    }
    dsl.toLowerCase().contains("distinctget")
  }

}
