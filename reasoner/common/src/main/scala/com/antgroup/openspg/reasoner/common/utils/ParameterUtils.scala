package com.antgroup.openspg.reasoner.common.utils

import com.antgroup.openspg.reasoner.common.constants.Constants

object ParameterUtils {
  /**
   * check spg plan pretty print enable
   * @param params
   * @return
   */
  def isEnableSPGPlanPrettyPrint(params: Map[String, Object]): Boolean = {
    "true".equals(params
      .getOrElse(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, "true")
      .toString)
  }

  /**
   * parse string format: format alias_name=id1,id2,id3,...,idn;alias_name2=id3,id5
   *
   * @return
   */
  def parseKvSetFormat(str: String): Map[String, Set[String]] = {
    val pairs = str.split(";")
    val result = pairs.map(pair => {
      val keyValue = pair.split("=")
      val key = keyValue(0)
      val values = keyValue(1).split(",").toSet
      (key, values)
    }).toMap
    result
  }
}
