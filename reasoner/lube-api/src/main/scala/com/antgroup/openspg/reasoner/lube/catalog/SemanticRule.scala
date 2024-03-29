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

package com.antgroup.openspg.reasoner.lube.catalog

import java.io.StringWriter

import scala.collection.JavaConverters._

import com.github.pfmiles.minvelocity.TemplateUtil

sealed trait SemanticRule extends Serializable

case class GeneralSemanticRule(rule: String) extends SemanticRule

case class TemplateSemanticRule(template: String, params: Map[String, String])
    extends SemanticRule {

  def constructDsl(): String = {
    val w = new StringWriter
    TemplateUtil.renderString(template, params.asJava, w)
    w.toString
  }

  def constructReverseDsl(): String = {
    val w = new StringWriter
    val newParams = onlyReverseAliasAndProperty(params)
    TemplateUtil.renderString(template, newParams.asJava, w)
    w.toString
  }

  private def onlyReverseAliasAndProperty(params: Map[String, String]): Map[String, String] = {
    var newParams: Map[String, String] = Map.empty ++ params
    newParams += ("sAlias" -> "o")
    newParams += ("oAlias" -> "s")
    val tmp: String = newParams("sProperty")
    newParams += ("sProperty" -> newParams("oProperty"))
    newParams += ("oProperty" -> tmp)
    newParams
  }

}
