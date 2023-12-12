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
