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

package com.antgroup.openspg.server.schema.core.model.type;

import com.antgroup.openspg.server.schema.core.model.BasicInfo;
import com.antgroup.openspg.server.schema.core.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.schema.core.model.predicate.Property;
import com.antgroup.openspg.server.schema.core.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.server.schema.core.model.predicate.Relation;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Class definition of temporal events<br>
 *
 * <p>An event is a special entity with temporal characteristics, sometimes referred to as
 * HyperGraph, expressing a certain behavior that occurs between multiple entities in a certain
 * spatiotemporal environment. Event type, as the type definition of an event, generally includes
 * the following configuration information:
 *
 * <ul>
 *   <li>the occur time: the begin time, end time when the event occurs.
 *   <li>the occur address: the LBS information where the event occurs.
 *   <li>the subject of an event: subject entity of the event, may include multiple entities.
 *   <li>the object of an event: object entity of event, may include multiple entities.
 * </ul>
 *
 * Take epidemic event as an example: On December 27, 2019, Director Zhang Jixian of the Respiratory
 * Department of Hubei Hospital detected that Mark was infected with the new coronavirus;<br>
 * we can define an event type named EpidemicEvent<br>
 *
 * <ul>
 *   <li>subject: Director Zhang Jixian
 *   <li>object: Mark
 *   <li>event time: December 27, 2019
 *   <li>event address: Hubei Hospital
 * </ul>
 */
public class EventType extends BaseAdvancedType {

  private static final long serialVersionUID = -3556413141077741935L;

  public EventType(
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      List<Property> properties,
      List<Relation> relations,
      SPGTypeAdvancedConfig advancedConfig) {
    super(basicInfo, parentTypeInfo, SPGTypeEnum.EVENT_TYPE, properties, relations, advancedConfig);
  }

  public List<Property> getTimeProperties() {
    return this.getPropertyByGroup(PropertyGroupEnum.TIME);
  }

  public List<Property> getSubjectProperties() {
    return this.getPropertyByGroup(PropertyGroupEnum.SUBJECT);
  }

  public List<Property> getObjectProperties() {
    return this.getPropertyByGroup(PropertyGroupEnum.OBJECT);
  }

  private List<Property> getPropertyByGroup(PropertyGroupEnum groupEnum) {
    List<Property> properties = getProperties();
    if (CollectionUtils.isEmpty(properties) || null == groupEnum) {
      return properties;
    }

    return properties.stream()
        .filter(e -> groupEnum.equals(e.getPropertyGroup()))
        .collect(Collectors.toList());
  }
}
