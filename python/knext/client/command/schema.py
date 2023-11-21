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
from pathlib import Path

import click

from knext.core.schema.concept_rule_ml import SPGConceptRuleMarkLang
from knext.core.schema.schema_ml import SPGSchemaMarkLang


def list_schema():
    """
    List all server-side schemas.
    """
    click.secho("Not support yes.", fg="bright_yellow")


def diff_schema():
    """
    Print differences of schema between local and server.
    """
    schema_path = os.path.join(
        os.environ["KNEXT_ROOT_PATH"],
        os.environ["KNEXT_SCHEMA_DIR"],
        os.environ["KNEXT_SCHEMA_FILE"],
    )
    ml = SPGSchemaMarkLang(schema_path)
    ml.print_diff()


def commit_schema():
    """
    Commit local schema and generate schema helper.
    """
    schema_path = os.path.join(
        os.environ["KNEXT_ROOT_PATH"],
        os.environ["KNEXT_SCHEMA_DIR"],
        os.environ["KNEXT_SCHEMA_FILE"],
    )
    if not Path(schema_path).exists():
        click.secho(f"ERROR: File {schema_path} not exists.", fg="bright_red")
        return

    ml = SPGSchemaMarkLang(schema_path)
    is_altered = ml.sync_schema()

    helper_path = os.path.join(
        os.environ["KNEXT_ROOT_PATH"],
        os.environ["KNEXT_SCHEMA_DIR"],
        f"{os.environ['KNEXT_PROJECT_DIR']}_schema_helper.py",
    )
    if not Path(helper_path).parent.exists():
        click.secho(f"ERROR: {helper_path} not exists.", fg="bright_red")
        return
    ml.export_schema_python(helper_path)
    if is_altered:
        click.secho("Schema is successfully committed.", fg="bright_green")
        click.secho(
            f"SchemaHelper is created in {os.environ['KNEXT_SCHEMA_DIR']}/{os.environ['KNEXT_PROJECT_DIR']}_schema_helper.py.",
            fg="bright_green",
        )
    else:
        click.secho(
            "There is no diff between local and server-side schema.", fg="bright_yellow"
        )


@click.option("--file", help="Path of DSL file.")
def reg_concept_rule(file):
    """
    Register a concept rule according to DSL file.
    """
    SPGConceptRuleMarkLang(file)
    click.secho(f"Concept rule is successfully registered", fg="bright_green")
