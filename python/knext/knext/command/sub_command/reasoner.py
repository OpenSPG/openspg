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
import os
import sys
from pathlib import Path

import click
from knext.common.env import init_kag_config

from knext.reasoner.client import ReasonerClient


@click.option("--file", help="Path of DSL file.")
@click.option("--dsl", help="DSL string enclosed in double quotes.")
@click.option("--output", help="Output file.")
@click.option("--proj_path", help="Path of config.", default="./")
def execute_reasoner_job(file, dsl, output=None, proj_path="./"):
    """
    Submit asynchronous reasoner jobs to server by providing DSL file or string.
    """
    config_path = os.path.join(proj_path, "kag_config.cfg")
    if not Path(config_path).exists():
        # find *.cfg file
        cfg_files = list(Path(proj_path).glob("*.cfg"))
        if len(cfg_files) == 0:
            click.secho("ERROR: No .cfg file found.", fg="bright_red")
            sys.exit()
        config_path = cfg_files[0]
    init_kag_config(config_path)
    host_addr = os.getenv("KAG_PROJECT_ADDR", "http://127.0.0.1:8887")
    project_id = os.getenv("KAG_PROJECT_ID")
    if not project_id:
        click.secho(
            "ERROR: Reasoner must be executed with SPG Server. Need assign proj_path",
            fg="bright_red",
        )
        sys.exit()
    client = ReasonerClient(host_addr=host_addr, project_id=int(project_id))
    if file and not dsl:
        with open(file, "r") as f:
            dsl_content = f.read()
    elif not file and dsl:
        dsl_content = dsl
    else:
        click.secho("ERROR: Please choose either --file or --dsl.", fg="bright_red")
        sys.exit()
    client.execute(dsl_content, output_file=output)