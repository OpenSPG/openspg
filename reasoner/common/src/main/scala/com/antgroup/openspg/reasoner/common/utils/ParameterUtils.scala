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
