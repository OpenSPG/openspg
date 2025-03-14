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

package com.antgroup.openspg.reasoner.util

import scala.collection.JavaConverters._

object Convert2ScalaUtil {

  def toScalaImmutableMap[T1, T2](jMap: java.util.Map[T1, T2]): Map[T1, T2] = {
    jMap.asScala.toMap;
  }

  def toScalaSeq[T](jList: java.util.List[T]): Seq[T] = {
    jList.asScala
  }

  def toScalaList[T](jList: java.util.List[T]): List[T] = {
    jList.asScala.toList
  }

  def toScalaImmutableSet[T](jSet: java.util.Set[T]): Set[T] = {
    jSet.asScala.toSet
  }

  def toJavaList[T](scalaList: List[T]): java.util.List[T] = {
    scalaList.asJava
  }

}
