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
import sys
from pathlib import Path

import click
from tabulate import tabulate

from knext import rest
from knext.client.reasoner import ReasonerClient


@click.option("--file", help="Path of DSL file.")
@click.option("--dsl", help="DSL string enclosed in double quotes.")
def run_dsl(file, dsl):
    """
    Query dsl by providing a string or file.
    """
    client = ReasonerClient()
    dsl_content = ""
    if file and not dsl:
        with open(file, "r") as f:
            dsl_content = f.read()
    elif not file and dsl:
        dsl_content = dsl
    else:
        click.secho("ERROR: Please choose either --file or --dsl.", fg="bright_red")

    result = client.run_dsl(dsl_content)
    table = tabulate(result.cells, result.columns, tablefmt="github")
    click.echo(table)


@click.option("--file", help="Path of DSL file.")
@click.option("--dsl", help="DSL string enclosed in double quotes.")
def submit_reasoner_job(file, dsl):
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
    inst = client.submit(dsl_content)

    click.secho(
        f"ReasonerJob [\n{dsl_content}] has been successfully submitted."
        f" Use ` knext reasoner get --id {inst.reasoner_job_inst_id} ` to check job status.",
        fg="bright_green",
    )


@click.option("--id", help="Unique id of submitted reasoner job.")
def query_reasoner_job(id):
    """
    Query submitted reasoner job status.
    """
    try:
        job_inst_id = int(id)
    except:
        click.secho("ERROR: Option [--id] must be a integer.", fg="bright_red")
        sys.exit()

    client = ReasonerClient()
    res = client.query(job_inst_id=job_inst_id)
    if res and len(res) > 0:
        data = {k: [v if v else "-"] for k, v in res[0].to_dict().items()}
        table = tabulate(data, headers="keys", tablefmt="github")
        click.echo(table)
    else:
        click.secho(
            f"ERROR: Cannot find ReasonerJob instance. "
            f"Please check if the [--id {id}] is correct.",
            fg="bright_red",
        )
        sys.exit()

    if (
        res[0].status in ["SUCCESS", "FAILURE"]
        and res[0].result
        and res[0].result.table_name
    ):
        confirm = click.style(
            f"ReasonerJob instance with id [{res[0].job_inst_id}] execution completed. "
            f"Do you need to download result_file?",
            fg="bright_green",
        )
        if click.confirm(confirm):
            client = rest.TableStoreApi()
            table_name = res[0].result.table_name
            content = client.table_store_download_get(file_name=table_name)

            file_path = os.path.join(
                os.environ["KNEXT_ROOT_PATH"],
                os.environ["KNEXT_REASONER_RESULT_DIR"],
                table_name.split("/")[-1],
            )
            if not Path(file_path).parent.exists():
                os.mkdir(Path(file_path).parent.resolve())
            with open(file_path, "w") as file:
                file.write(content)
            click.secho(
                f"Download successful. The file path is [{file_path}].",
                fg="bright_green",
            )
        else:
            sys.exit()


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
