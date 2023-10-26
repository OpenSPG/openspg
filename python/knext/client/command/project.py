# -*- coding: utf-8 -*-
#
#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

import os
import re
import string
import sys
from configparser import ConfigParser
from pathlib import Path
from shutil import copy2, copystat
from stat import S_IWUSR as OWNER_WRITE_PERMISSION
from typing import Optional

import click
from tabulate import tabulate

from knext import rest
from knext.common.template import render_templatefile

TEMPLATES_TO_RENDER = (
    (".knext.cfg.tmpl",),
    ("README.md.tmpl",),
    ("schema", "${project}.schema.tmpl"),
    ("reasoner", "demo.dsl.tmpl"),
    ("builder", "operator", "demo_extract_op.py"),
    ("builder", "job", "data", "Demo.csv"),
    ("builder", "job", "demo.py.tmpl"),
)


def list_project():
    """
    List all project information.
    """
    client = rest.ProjectApi()
    projects = client.project_get()

    table_data = []
    for project in projects:
        item = project.to_dict()
        table_data.append(
            [item["id"], item["name"], item["namespace"], item["description"]]
        )

    table_headers = ["ID", "Name", "Namespace", "Description"]

    table = tabulate(table_data, table_headers, tablefmt="github")
    click.echo(table)


def _recover_project(prj_path: str):
    """
    Recover project by a project dir path.
    """
    if not Path(prj_path).exists():
        click.secho(f"ERROR: No such directory: {prj_path}", fg="bright_red")
        sys.exit()

    if prj_path not in sys.path:
        sys.path.append(prj_path)
    prj = Path(prj_path).resolve()

    cfg_file = prj / ".knext.cfg"
    cfg = ConfigParser()
    cfg.read(cfg_file)
    project_name = cfg.get("local", "project_name")
    namespace = cfg.get("local", "namespace")
    desc = cfg.get("local", "description")

    client = rest.ProjectApi()
    project_create_request = rest.ProjectCreateRequest(
        name=project_name, desc=desc, namespace=namespace
    )
    project = client.project_create_post(project_create_request=project_create_request)

    cfg.set("local", "project_id", str(project.id))
    with open(cfg_file, "w") as config_file:
        cfg.write(config_file)

    click.secho(
        f"Project [{project_name}] with namespace [{namespace}] was successfully created from [{prj_path}].",
        fg="bright_green",
    )


@click.option("--name", help="Name of project.")
@click.option("--namespace", help="Prefix of project schema.")
@click.option("--desc", help="Description of project.")
@click.option(
    "--prj_path",
    help="If set, project will be created according to config file of this path.",
)
def create_project(
    name: str, namespace: str, desc: Optional[str], prj_path: Optional[str]
):
    """
    Create new project with a demo case.
    """

    if prj_path:
        _recover_project(prj_path)
        sys.exit()

    if not name:
        click.secho("ERROR: Option [--name] is required.")
        sys.exit()
    if not namespace:
        click.secho("ERROR: Option [--namespace] is required.")
        sys.exit()

    if not re.match(r"^[A-Z][A-Za-z0-9]{0,15}$", namespace):
        raise click.BadParameter(
            f"Invalid namespace: {namespace}."
            f" Must start with an uppercase letter, only contain letters and numbers, and have a maximum length of 16."
        )

    project_dir = Path(namespace.lower())
    if project_dir.exists():
        raise click.ClickException(
            f"Project directory [{namespace.lower()}] already exists."
        )
    client = rest.ProjectApi()
    project_create_request = rest.ProjectCreateRequest(
        name=name, desc=desc, namespace=namespace
    )
    project = client.project_create_post(project_create_request=project_create_request)

    import knext

    templates_dir = Path(knext.__path__[0]) / "templates/project"
    _copytree(Path(templates_dir), project_dir.resolve(), namespace.lower())
    for paths in TEMPLATES_TO_RENDER:
        tplfile = Path(
            project_dir,
            *(string.Template(s).substitute(project=namespace.lower()) for s in paths),
        )
        render_templatefile(
            tplfile,
            project_name=name,
            namespace=namespace,
            project_dir=namespace.lower(),
            project_id=project.id,
            description=desc,
            helper=f"{namespace.lower()}_schema_helper",
        )

    click.secho(
        f"Project [{name}] with namespace [{namespace}] was successfully created in {project_dir.resolve()} \n"
        + "You can checkout your project with: \n"
        + f"  cd {project_dir}",
        fg="bright_green",
    )


def _copytree(src: Path, dst: Path, project_name: str):
    names = [x.name for x in src.iterdir()]

    if not dst.exists():
        dst.mkdir(parents=True)

    for name in names:
        _name = name.replace("${project}", project_name)
        src_name = src / name
        dst_name = dst / _name
        if src_name.is_dir():
            _copytree(src_name, dst_name, project_name)
        else:
            copy2(src_name, dst_name)
            _make_writable(dst_name)

    copystat(src, dst)
    _make_writable(dst)


def _make_writable(path):
    current_permissions = os.stat(path).st_mode
    os.chmod(path, current_permissions | OWNER_WRITE_PERMISSION)
