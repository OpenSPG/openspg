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
import json
import os
import sys

from knext.builder import rest
from knext.builder.chain import BuilderChain
from knext.common.base.client import Client
from knext.builder.model.builder_job import BuilderJob, AlterOperationEnum
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

    def execute(self, builder_chain: BuilderChain, **kwargs):
        import subprocess
        import datetime
        from knext.builder import lib
        from knext.common import env

        jar_path = os.path.join(lib.__path__[0], lib.LOCAL_BUILDER_JAR)
        dag_config = builder_chain.to_rest()
        pipeline = self.serialize(dag_config)
        log_file_name = f"{datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')}.log"

        java_cmd = [
            "java",
            "-jar",
            jar_path,
            "--projectId",
            self._project_id,
            "--jobName",
            kwargs.get("job_name", "default_job"),
            "--pipeline",
            json.dumps(pipeline),
            "--pythonExec",
            sys.executable,
            "--pythonPaths",
            ";".join(sys.path),
            "--schemaUrl",
            os.environ.get("KNEXT_HOST_ADDR") or env.LOCAL_SCHEMA_URL,
            "--parallelism",
            str(kwargs.get("parallelism", "1")),
            "--alterOperation",
            kwargs.get("alter_operation", AlterOperationEnum.Upsert),
            "--logFile",
            log_file_name,
            "--graphStoreUrl",
            os.environ.get("KNEXT_GRAPH_STORE_URL") or lib.LOCAL_GRAPH_STORE_URL,
            "--searchEngineUrl",
            os.environ.get("KNEXT_SEARCH_ENGINE_URL") or lib.LOCAL_SEARCH_ENGINE_URL,
        ]

        if kwargs.get("lead_to"):
            java_cmd.append("--leadTo")

        if os.getenv("KNEXT_DEBUG_MODE", "False") == "True":
            print_java_cmd = [
                cmd if not cmd.startswith("{") else f"'{cmd}'" for cmd in java_cmd
            ]
            print_java_cmd = [
                cmd if not cmd.count(";") > 0 else f"'{cmd}'" for cmd in print_java_cmd
            ]
            print(json.dumps(" ".join(print_java_cmd))[1:-1].replace("'", '"'))

        subprocess.call(java_cmd)
