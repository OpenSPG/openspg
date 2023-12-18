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

import click

from knext.command.sub_command.builder import get_job
from knext.command.sub_command.builder import submit_job
from knext.command.sub_command.config import edit_config
from knext.command.sub_command.config import list_config
from knext.command.sub_command.operator import list_operator
from knext.command.sub_command.operator import publish_operator
from knext.command.sub_command.project import create_project
from knext.command.sub_command.project import list_project
from knext.command.sub_command.reasoner import query_reasoner_job
from knext.command.sub_command.reasoner import run_dsl
from knext.command.sub_command.reasoner import submit_reasoner_job
from knext.command.sub_command.schema import commit_schema
from knext.command.sub_command.schema import diff_schema
from knext.command.sub_command.schema import list_schema
from knext.command.sub_command.schema import reg_concept_rule
from knext.command.exception import _ApiExceptionHandler
from knext import __version__
from knext.common.env import init_env, get_config, GLOBAL_CONFIG, CFG_PREFIX


@click.group(cls=_ApiExceptionHandler)
@click.version_option(__version__)
def _main() -> None:
    pass


@_main.group()
def config() -> None:
    """Knext config."""
    pass


config.command("list")(list_config)
config.command("set")(edit_config)


@_main.group()
def builder() -> None:
    """Builder client."""
    init_env()


builder.command("submit")(submit_job)
builder.command("get")(get_job)


@_main.group()
def operator() -> None:
    """Operator client"""
    init_env()


operator.command("publish")(publish_operator)
operator.command("list")(list_operator)


@_main.group()
def project() -> None:
    """Project client."""
    project_cfg, root_path = get_config()

    if project_cfg.has_section("global"):
        for cfg in GLOBAL_CONFIG:
            os.environ[CFG_PREFIX + cfg.upper()] = project_cfg.get("global", cfg)
    pass


project.command("create")(create_project)
project.command("list")(list_project)


@_main.group()
def schema() -> None:
    """Schema client."""
    init_env()
    pass


schema.command("commit")(commit_schema)
schema.command("list")(list_schema)
schema.command("diff")(diff_schema)
schema.command("reg_concept_rule")(reg_concept_rule)


@_main.group()
def reasoner() -> None:
    """Reasoner client."""
    init_env()
    pass


reasoner.command("submit")(submit_reasoner_job)
reasoner.command("query")(run_dsl)
reasoner.command("get")(query_reasoner_job)

if __name__ == "__main__":
    _main()
