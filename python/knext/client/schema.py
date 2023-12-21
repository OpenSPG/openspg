# -*- coding: utf-8 -*-
# Copyright 2023 Ant Group CO., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

import os
from typing import List

from knext import rest
from knext.client.base import Client
from knext.client.model.base import BaseSpgType, AlterOperationEnum, SpgTypeEnum
from knext.client.model.relation import Relation


class SchemaClient(Client):
    """ """

    _rest_client = rest.SchemaApi()

    def __init__(self, host_addr: str = None, project_id: int = None):
        super().__init__(host_addr, project_id)

        self._session = None

    def query_spg_type(self, spg_type_name: str) -> BaseSpgType:
        """Query SPG type by name."""
        rest_model = self._rest_client.schema_query_spg_type_get(spg_type_name)
        type_class = BaseSpgType.by_type_enum(f"{rest_model.spg_type_enum}")

        if rest_model.spg_type_enum == SpgTypeEnum.Concept:
            return type_class(
                name=spg_type_name,
                hypernym_predicate=rest_model.concept_layer_config.hypernym_predicate,
                rest_model=rest_model,
            )
        else:
            return type_class(name=spg_type_name, rest_model=rest_model)

    def query_relation(
        self, subject_name: str, predicate_name: str, object_name: str
    ) -> Relation:
        """Query relation type by s_p_o name."""
        rest_model = self._rest_client.schema_query_relation_get(
            subject_name, predicate_name, object_name
        )
        return Relation(
            name=predicate_name, object_type_name=object_name, rest_model=rest_model
        )

    def create_session(self):
        """Create session for altering schema."""
        return self.SchemaSession(self._rest_client, self._project_id)

    class SchemaSession:
        def __init__(self, client, project_id):
            self._alter_spg_types: List[BaseSpgType] = []
            self._rest_client = client
            self._project_id = project_id

            self._spg_types = {}
            self.__spg_types = {}
            self._init_spg_types()

        def _init_spg_types(self):
            """Query project schema and init SPG types in session."""
            project_schema = self._rest_client.schema_query_project_schema_get(
                self._project_id
            )
            for spg_type in project_schema.spg_types:
                spg_type_name = spg_type.basic_info.name.name
                type_class = BaseSpgType.by_type_enum(spg_type.spg_type_enum)
                if spg_type.spg_type_enum == SpgTypeEnum.Concept:
                    self._spg_types[spg_type_name] = type_class(
                        name=spg_type_name,
                        hypernym_predicate=spg_type.concept_layer_config.hypernym_predicate,
                        rest_model=spg_type,
                    )
                else:
                    self._spg_types[spg_type_name] = type_class(
                        name=spg_type_name, rest_model=spg_type
                    )

        def get(self, spg_type_name) -> BaseSpgType:
            """Get SPG type by name from project schema."""
            spg_type = self._spg_types.get(spg_type_name)
            if spg_type is None:
                spg_type = self.__spg_types.get(spg_type_name)
                if spg_type is None:
                    raise ValueError(f"{spg_type_name} is not existed")
                else:
                    return self.__spg_types.get(spg_type_name)
            return self._spg_types.get(spg_type_name)

        def create_type(self, spg_type: BaseSpgType):
            """Add an SPG type in session with `CREATE` operation."""
            spg_type.alter_operation = AlterOperationEnum.Create
            self.__spg_types[spg_type.name] = spg_type
            self._alter_spg_types.append(spg_type)
            return self

        def update_type(self, spg_type: BaseSpgType):
            """Add an SPG type in session with `UPDATE` operation."""
            spg_type.alter_operation = AlterOperationEnum.Update
            self._alter_spg_types.append(spg_type)
            return self

        def delete_type(self, spg_type: BaseSpgType):
            """Add an SPG type in session with `DELETE` operation."""
            spg_type.alter_operation = AlterOperationEnum.Delete
            self._alter_spg_types.append(spg_type)
            return self

        def commit(self):
            """Commit all altered schemas to server."""
            schema_draft = []
            for spg_type in self._alter_spg_types:
                for prop in spg_type.properties.values():
                    if prop.object_spg_type is None:
                        object_spg_type = self.get(prop.object_type_name)
                        prop.object_spg_type = object_spg_type.spg_type_enum
                    for sub_prop in prop.sub_properties.values():
                        if sub_prop.object_spg_type is None:
                            object_spg_type = self.get(sub_prop.object_type_name)
                            sub_prop.object_spg_type = object_spg_type.spg_type_enum
                for rel in spg_type.relations.values():
                    if rel.is_dynamic is None:
                        rel.is_dynamic = False
                    if rel.object_spg_type is None:
                        object_spg_type = self.get(rel.object_type_name)
                        rel.object_spg_type = object_spg_type.spg_type_enum
                    for sub_prop in rel.sub_properties.values():
                        if sub_prop.object_spg_type is None:
                            object_spg_type = self.get(sub_prop.object_type_name)
                            sub_prop.object_spg_type = object_spg_type.spg_type_enum
                schema_draft.append(spg_type.to_rest())
            if len(schema_draft) == 0:
                return

            request = rest.SchemaAlterRequest(
                project_id=self._project_id, schema_draft=rest.SchemaDraft(schema_draft)
            )
            self._rest_client.schema_alter_schema_post(schema_alter_request=request)
