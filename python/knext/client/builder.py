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
import json
import os

from knext import rest
from knext.chain.builder_chain import BuilderChain
from knext.client.base import Client
from knext.client.model.builder_job import BuilderJob, AlterOperationEnum
from knext.common.class_register import register_from_package


class BuilderClient(Client):
    """SPG Builder Client."""

    _rest_client = rest.BuilderApi()

    def __init__(self, host_addr: str = None, project_id: int = None):
        super().__init__(host_addr, project_id)

        if "KNEXT_ROOT_PATH" in os.environ and "KNEXT_BUILDER_JOB_DIR" in os.environ:
            self._builder_job_path = os.path.join(
                os.environ["KNEXT_ROOT_PATH"], os.environ["KNEXT_BUILDER_JOB_DIR"]
            )
            register_from_package(self._builder_job_path, BuilderJob)

    def submit(self, job_name: str):
        """Submit an asynchronous builder job to the server by name."""
        job = BuilderJob.by_name(job_name)()
        builder_chain = job.build()
        dag_config = builder_chain.to_rest()

        params = {
            param: getattr(job, param)
            for param in job.__annotations__
            if hasattr(job, param) and not param.startswith("_")
        }
        request = rest.BuilderJobSubmitRequest(
            job_name=job.name,
            project_id=self._project_id,
            pipeline=dag_config,
            params=params,
        )
        return self._rest_client.builder_submit_job_info_post(
            builder_job_submit_request=request
        )

    def execute(self, builder_chain: BuilderChain, **kwargs):
        """
          --projectId 2 \
  --jobName "TaxOfRiskApp" \
  --pipeline "{\"nodes\":[{\"id\":\"1\",\"name\":\"csv\",\"nodeConfig\":{\"@type\":\"CSV_SOURCE\",\"startRow\":2,\"url\":\"./src/test/resources/TaxOfRiskApp.csv\",\"columns\":[\"id\"],\"type\":\"CSV_SOURCE\"}},{\"id\":\"2\",\"name\":\"mapping\",\"nodeConfig\":{\"@type\":\"SPG_TYPE_MAPPING\",\"spgType\":\"RiskMining.TaxOfRiskUser\",\"mappingFilters\":[],\"mappingConfigs\":[],\"type\":\"SPG_TYPE_MAPPING\"}},{\"id\":\"3\",\"name\":\"sink\",\"nodeConfig\":{\"@type\":\"GRAPH_SINK\",\"type\":\"GRAPH_SINK\"}}],\"edges\":[{\"from\":\"1\",\"to\":\"2\"},{\"from\":\"2\",\"to\":\"3\"}]}" \
  --pythonExec "/usr/local/bin/python3.9" \
  --pythonPaths "/usr/local/lib/python3.9/site-packages;./python" \
  --schemaUrl "http://localhost:8887" \
  --parallelism "1" \
  --alterOperation "UPSERT" \
  --logFile TaxOfRiskApp.log
        """

        dag_config = builder_chain.to_rest()
        import os
        import sys
        import knext
        python_exec = sys.executable
        python_paths = sys.path
        sys.path.append(os.path.join(knext.__path__[0], "/operator/builtin"))

        import subprocess
        import datetime

        jar_path = os.path.join(knext.__path__[0], f"engine/builder-runner-local-0.0.1-SNAPSHOT-jar-with-dependencies.jar")
        api_client = BuilderClient()._rest_client.api_client
        pipeline = api_client.sanitize_for_serialization(dag_config)
        log_file_name = f"{datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')}.log"

        java_cmd = ['java', '-jar',
                    "-Dcloudext.graphstore.drivers=com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphStoreClientDriver",
                    "-Dcloudext.searchengine.drivers=com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchEngineClientDriver",
                    jar_path,
                    "--projectId", os.environ.get("KNEXT_PROJECT_ID"),
                    "--jobName", kwargs.get("job_name", "default_job"),
                    "--pipeline", json.dumps(pipeline),
                    "--pythonExec", python_exec,
                    "--pythonPaths", ';'.join(python_paths),
                    "--schemaUrl", os.environ.get("KNEXT_HOST_ADDR"),
                    "--parallelism", str(kwargs.get("parallelism", "1")),
                    "--alterOperation", kwargs.get("alter_operation", AlterOperationEnum.Upsert),
                    "--logFile", log_file_name
                    ]

        print_java_cmd = [cmd if not cmd.startswith('{') else f"'{cmd}'" for cmd in java_cmd]
        print_java_cmd = [cmd if not cmd.count(';') > 0 else f"'{cmd}'" for cmd in print_java_cmd]
        print(json.dumps(' '.join(print_java_cmd))[1:-1].replace("'", '"'))

        subprocess.call(java_cmd)

    def query(self, job_inst_id: int):
        """Query status of a submitted builder job by job inst id."""
        return self._rest_client.builder_query_job_inst_get(job_inst_id=job_inst_id)
