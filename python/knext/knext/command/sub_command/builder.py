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

import click
from knext.builder.model.builder_job import BuilderJob


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
