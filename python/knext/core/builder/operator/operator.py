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
from enum import Enum
from typing import Dict

from knext import rest
from knext.common.class_register import register_from_package
from knext.core.builder.operator.model.op import BaseOp


class OperatorTypeEnum(str, Enum):
    EntityLinkOp = "ENTITY_LINK"
    EntityFuseOp = "ENTITY_FUSE"
    PropertyNormalizeOp = "PROPERTY_NORMALIZE"
    KnowledgeExtractOp = "KNOWLEDGE_EXTRACT"


class Operator(object):
    """SPG Operator Client."""

    def __init__(
        self,
    ):
        self._client = rest.OperatorApi()
        self._project_id = os.environ.get("KNEXT_PROJECT_ID")
        self._builder_operator_path = os.path.join(
            os.environ["KNEXT_ROOT_PATH"], os.environ["KNEXT_BUILDER_OPERATOR_DIR"]
        )

        register_from_package(self._builder_operator_path, BaseOp)

    def publish(self, op_name: str):
        """Upload operator files and publish a new version.
        If the operator has not been published, this method will create an operator overview firstly.

        """
        op = BaseOp.by_name(op_name)()

        operator_list = self._client.operator_overview_get(name=op.name)
        if len(operator_list) == 0:
            self._client.operator_overview_post(
                operator_create_request=rest.OperatorCreateRequest(
                    name=op.name, desc=op.desc, operator_type=op._type
                )
            )
            operator_id = self._client.operator_overview_get(name=op.name)[0].id
        else:
            operator_id = operator_list[0].id

        add_response = self._client.operator_version_post(
            project_id=self._project_id, operator_id=operator_id, file=op._local_path
        )
        op._version = add_response.latest_version

        if op.bind_to is not None:
            from knext.core.schema import Schema
            from knext.core.schema.model.base import SpgTypeEnum

            schema_session = Schema().create_session()
            spg_type = schema_session.get(op.bind_to)
            if spg_type.spg_type_enum in [SpgTypeEnum.Entity, SpgTypeEnum.Event]:
                spg_type.bind_link_operator(op)
            elif spg_type.spg_type_enum == SpgTypeEnum.Concept:
                spg_type.bind_normalize_operator(op)
            else:
                pass
            schema_session.update_type(spg_type)
            schema_session.commit()

        return op

    def _generate_op_config(
        self, op_name: str, version: int = None, params: Dict[str, str] = None
    ):
        """Transforms a list of components to REST model `OperatorConfig`."""
        overviews = self._client.operator_overview_get(op_name)
        if not overviews:
            raise ValueError(
                f"Operator [{op_name}] is not published."
                f" Use ` knext operator publish {op_name}` to publish this operator."
            )
        op = None
        operator_versions = self._client.operator_version_get(op_name)
        if not operator_versions:
            raise ValueError(
                f"Operator [{op_name}] is not published."
                f" Use ` knext operator publish {op_name}` to publish this operator."
            )
        if version:
            # Pull operator from server with specified version.
            for operator_version in operator_versions:
                if operator_version.version == version:
                    op = operator_version
                    break
            if not op:
                raise ValueError(
                    f"Operator [{op_name}] with Version [{version}] is not published."
                    f" Use ` knext operator publish {op_name} ` to publish this operator."
                )
        else:
            # Pull operator from server with the latest version.
            op = self._client.operator_version_get(op_name)[0]

        return rest.OperatorConfig(
            name=op_name,
            version=op.version,
            jar_address=f"./objectStore/operator/{op.file_path}",
            main_class=op.main_class,
            lang_type=overviews[0].lang_type,
            operator_type=overviews[0].type,
            params=params,
        )
