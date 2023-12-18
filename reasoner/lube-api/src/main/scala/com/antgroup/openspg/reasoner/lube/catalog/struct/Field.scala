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

package com.antgroup.openspg.reasoner.lube.catalog.struct

import scala.util.hashing.MurmurHash3

import com.antgroup.openspg.reasoner.common.types.KgType

/**
 * Field defines a property information of Entity or Relation
 * @param name property name
 * @param kgType the value type of property
 * @param resolved has been resolved, it equals true if the property has been imported to KG
 *                 or has been computed.
 */
class Field(val name: String, val kgType: KgType, val resolved: Boolean)
  extends Serializable {
  override def toString: String = s"$name::$kgType::$resolved"

  override def hashCode(): Int = MurmurHash3.stringHash(name)

  override def equals(obj: Any): Boolean = {
    obj match {
      case field: Field => this.name.equals(field.name)
      case _ => false
    }
  }

  def toTypedTuple: (String, KgType) = name -> kgType
}

/**
 *
 * @param name property name
 * @param kgType the value type of property
 * @param resolved has been resolved, it equals true if the property has been imported to KG
 *                 or has been computed.
 * @param rule the rule for determining this property
 */
final class ConceptField(
    override val name: String,
    override val kgType: KgType,
    override val resolved: Boolean,
    val rule: String)
    extends Field(name, kgType, resolved) {}
