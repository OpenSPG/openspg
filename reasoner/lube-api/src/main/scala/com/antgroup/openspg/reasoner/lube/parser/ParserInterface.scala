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

package com.antgroup.openspg.reasoner.lube.parser

import com.antgroup.openspg.reasoner.lube.block.Block
import com.antgroup.openspg.reasoner.lube.common.expr.Expr

/**
 * interface for a parser, which contains required method used in lube.
 */

trait ParserInterface extends Serializable {

  /**
   * Parser kgdsl or gql to block
   * @param text
   * @return
   */
  def parse(text: String): Block

  /**
   * Parser kgdsl Define task and Compute task,
   * will return more than one blocks
   * @param text
   * @param param
   * @return
   */
  def parseMultipleStatement(text: String, param: Map[String, Object] = Map.empty): List[Block]

  /**
   * Get All parameters from dsl, parameter like '${var_name}'
   * @return
   */
  def getAllParameters(): Set[String]

  /**
   * get all id filter paramters
   * @return
   */
  def getIdFilterParameters(): Map[String, String]

  /**
   * Rule Parser
   * @param rule
   * @return
   */
  def parseExpr(rule: String): Expr
}
