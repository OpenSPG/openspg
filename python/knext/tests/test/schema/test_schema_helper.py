# -*- coding: utf-8 -*-
# Copyright 2023 OpenSPG Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

# ATTENTION!
# This file is generated by Schema automatically, it will be refreshed after schema has been committed
# PLEASE DO NOT MODIFY THIS FILE!!!
#

from knext.schema.model.schema_helper import (
    SPGTypeHelper,
    PropertyHelper,
    RelationHelper,
)


class TEST:
    class CenterEvent(SPGTypeHelper):
        class conceptProperty(PropertyHelper):
            source = PropertyHelper("source")

        class eventRelation(RelationHelper):
            source = PropertyHelper("source")
            confidence = PropertyHelper("confidence")

        conceptProperty = conceptProperty("conceptProperty")

        id = PropertyHelper("id")
        standardProperty = PropertyHelper("standardProperty")
        name = PropertyHelper("name")
        eventTime = PropertyHelper("eventTime")
        basicTextProperty = PropertyHelper("basicTextProperty")
        subject = PropertyHelper("subject")
        description = PropertyHelper("description")
        basicIntegerProperty = PropertyHelper("basicIntegerProperty")
        basicFloatProperty = PropertyHelper("basicFloatProperty")

        eventRelation = eventRelation("eventRelation")

    class Concept1(SPGTypeHelper):

        id = PropertyHelper("id")
        alias = PropertyHelper("alias")
        name = PropertyHelper("name")
        stdId = PropertyHelper("stdId")
        description = PropertyHelper("description")

        leadTo = RelationHelper("leadTo")

    class Concept2(SPGTypeHelper):

        id = PropertyHelper("id")
        alias = PropertyHelper("alias")
        name = PropertyHelper("name")
        stdId = PropertyHelper("stdId")
        description = PropertyHelper("description")

    class Concept3(SPGTypeHelper):

        id = PropertyHelper("id")
        alias = PropertyHelper("alias")
        name = PropertyHelper("name")
        stdId = PropertyHelper("stdId")
        description = PropertyHelper("description")

    class Entity1(SPGTypeHelper):

        description = PropertyHelper("description")
        name = PropertyHelper("name")
        id = PropertyHelper("id")

        predictRelation = RelationHelper("predictRelation")
        entityRelation = RelationHelper("entityRelation")

    class Entity2(SPGTypeHelper):

        description = PropertyHelper("description")
        name = PropertyHelper("name")
        id = PropertyHelper("id")

    class Entity3(SPGTypeHelper):

        description = PropertyHelper("description")
        name = PropertyHelper("name")
        id = PropertyHelper("id")

    CenterEvent = CenterEvent("TEST.CenterEvent")
    Concept1 = Concept1("TEST.Concept1")
    Concept2 = Concept2("TEST.Concept2")
    Concept3 = Concept3("TEST.Concept3")
    Entity1 = Entity1("TEST.Entity1")
    Entity2 = Entity2("TEST.Entity2")
    Entity3 = Entity3("TEST.Entity3")

    pass
