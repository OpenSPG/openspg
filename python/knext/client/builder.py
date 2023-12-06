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

from knext import rest
from knext.client.base import Client
from knext.common.class_register import register_from_package
from knext.core.builder.job.builder_job import BuilderJob


class BuilderClient(Client):
    """SPG Builder Client."""

    def __init__(self):
        self._client = rest.BuilderApi()
        self._project_id = os.environ.get("KNEXT_PROJECT_ID")
        self._builder_job_path = os.path.join(
            os.environ["KNEXT_ROOT_PATH"], os.environ["KNEXT_BUILDER_JOB_DIR"]
        )

        register_from_package(self._builder_job_path, BuilderJob)

    def submit(self, job_name: str):
        """Submit an asynchronous builder job to the server by name."""
        job = BuilderJob.by_name(job_name)()
        start_node = job.build()
        config = self._generate_dag_config(start_node)

        params = {
            param: getattr(job, param)
            for param in job.__annotations__
            if hasattr(job, param) and not param.startswith("_")
        }
        request = rest.BuilderJobSubmitRequest(
            job_name=job.name,
            project_id=self._project_id,
            pipeline=config,
            params=params,
        )
        return self._client.builder_submit_job_info_post(
            builder_job_submit_request=request
        )

    def query(self, job_inst_id: int):
        """Query status of a submitted builder job by job inst id."""
        return self._client.builder_query_job_inst_get(job_inst_id=job_inst_id)

    def _generate_dag_config(self, node):
        """Transforms a list of components to REST model `Pipeline`."""
        nodes, edges = [node._to_rest()], []
        while node.next:
            next_nodes = node.next
            nodes.extend([n._to_rest() for n in next_nodes])
            edges.extend(
                [rest.Edge(_from=pre.id, to=n.id) for n in next_nodes for pre in n.pre]
            )
            node = node.next[0]
        dag_config = rest.Pipeline(nodes=nodes, edges=edges)
        return dag_config
