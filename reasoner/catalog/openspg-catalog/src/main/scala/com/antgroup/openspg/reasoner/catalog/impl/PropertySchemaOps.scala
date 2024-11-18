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

package com.antgroup.openspg.reasoner.catalog.impl

import scala.language.implicitConversions

import com.antgroup.openspg.core.schema.model.`type`._
import com.antgroup.openspg.reasoner.catalog.impl.struct.PropertyMeta
import com.antgroup.openspg.reasoner.common.exception.KGValueException
import com.antgroup.openspg.reasoner.common.types._
import com.antgroup.openspg.reasoner.common.types.KgType.toBasicKgType
import com.antgroup.openspg.reasoner.lube.catalog.struct.NodeType

object PropertySchemaOps {

  implicit def stringToKgType(propertySchema: PropertyMeta): KgType = {
    propertySchema.getCategory match {
      case "BASIC" => propertySchema.getPropRange.getAttrRangeTypeEnum
      case "CONCEPT" =>
        KTConcept(propertySchema.getPropRange.getRangeEntityName)
      case "STANDARD" =>
        KTStd(
          propertySchema.getPropRange.getRangeEntityName,
          propertySchema.getPropRange.getAttrRangeTypeEnum,
          propertySchema.isSpreadable)
      case "PROPERTY" =>
        KTStd(
          propertySchema.getPropRange.getRangeEntityName,
          propertySchema.getPropRange.getAttrRangeTypeEnum,
          propertySchema.isSpreadable)
      case "ENTITY" =>
        KTAdvanced(propertySchema.getPropRange.getRangeEntityName)
      case _ => throw KGValueException(s"unsupported type: ${propertySchema.getCategory}")
    }
  }

  def stringToKgType2(spgType: BaseSPGType): KgType = {
    spgType match {
      case entityType: EntityType =>
        KTAdvanced(entityType.getName)
      case conceptType: ConceptType =>
        KTConcept(conceptType.getName)
      case eventType: EventType =>
        KTAdvanced(eventType.getName)
      case standardType: StandardType =>
        KTStd(spgType.getName, null, standardType.getSpreadable)
      case basicType: BasicType => basicType.getBasicType.name()
      case _ =>
        throw KGValueException(s"unsupported type: ${spgType}")
    }
  }


  def toNodeType(spgType: SPGTypeEnum): NodeType.Value = {
    spgType match {
      case SPGTypeEnum.ENTITY_TYPE =>
        NodeType.ADVANCED
      case SPGTypeEnum.CONCEPT_TYPE =>
        NodeType.CONCEPT
      case SPGTypeEnum.STANDARD_TYPE =>
        NodeType.STANDARD
      case SPGTypeEnum.EVENT_TYPE =>
        NodeType.EVENT
      case _ => throw KGValueException(s"unsupported type: $spgType")
    }
  }

}
