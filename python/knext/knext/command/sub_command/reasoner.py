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

import sys
import click

from knext.reasoner.client import ReasonerClient


@click.option("--file", help="Path of DSL file.")
@click.option("--dsl", help="DSL string enclosed in double quotes.")
@click.option("--output", help="Output file.")
def execute_reasoner_job(file, dsl, output=None):
    """
    Submit asynchronous reasoner jobs to server by providing DSL file or string.
    """
    client = ReasonerClient()
    if file and not dsl:
        with open(file, "r") as f:
            dsl_content = f.read()
    elif not file and dsl:
        dsl_content = dsl
    else:
        click.secho("ERROR: Please choose either --file or --dsl.", fg="bright_red")
        sys.exit()
    client.execute(dsl_content, output)
