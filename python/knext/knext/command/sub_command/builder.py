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
from knext.client.builder import BuilderClient
from knext.client.model.builder_job import BuilderJob


@click.argument("job_names", required=True)
def submit_job(job_names):
    """
    Submit asynchronous builder jobs to server by providing job names.
    """
    job_list = [name.strip() for name in job_names.split(",") if name]
    client = BuilderClient()

    for job in job_list:
        inst = client.submit(job)
        click.secho(
            f"BuilderJob [{job}] has been successfully submitted."
            f" Use ` knext builder get --id {inst.building_job_inst_id} ` to check job status.",
            fg="bright_green",
        )


@click.argument("job_names", required=True)
def execute_job(job_names):
    job_list = [name.strip() for name in job_names.split(",") if name]

    for job in job_list:
        builder_job = BuilderJob.by_name(job)()
        builder_chain = builder_job.build()
        params = {
            param: getattr(builder_job, param)
            for param in builder_job.__annotations__
            if hasattr(builder_job, param) and not param.startswith("_")
        }
        builder_chain.invoke(builder_chain, **params)


@click.option("--id", help="Unique id of submitted builder job.")
def get_job(id):
    """
    Query submitted job status.
    """
    try:
        job_inst_id = int(id)
    except:
        click.secho("ERROR: Option [--id] must be a integer.", fg="bright_red")
        sys.exit()

    client = BuilderClient()
    res = client.query(job_inst_id)
    if res and len(res) > 0:
        data = {k: [v if v else "-"] for k, v in res[0].to_dict().items()}
        table = tabulate(data, headers="keys", tablefmt="github")
        click.echo(table)
    else:
        click.secho(
            f"ERROR: Cannot find BuilderJob instance. Please check if the [--id {id}] is correct.",
            fg="bright_red",
        )
        sys.exit()

    if (
        res[0].status in ["SUCCESS", "FAILURE"]
        and res[0].result
        and res[0].result.error_table_file
    ):
        confirm = click.style(
            f"BuilderJob instance with id [{res[0].job_inst_id}] execution completed. "
            f"Do you need to download error_table_file?",
            fg="bright_green",
        )
        if click.confirm(confirm):
            client = rest.TableStoreApi()
            error_table_file = res[0].result.error_table_file
            content = client.table_store_download_get(file_name=error_table_file)

            file_path = os.path.join(
                os.environ["KNEXT_ROOT_PATH"],
                os.environ["KNEXT_BUILDER_RECORD_DIR"],
                error_table_file.split("/")[-1],
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
