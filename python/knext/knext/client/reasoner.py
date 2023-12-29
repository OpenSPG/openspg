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
from enum import Enum

from knext import rest
from knext.client.base import Client

_DEFAULT_JOB_NAME = "job"


class SchedulerTypeEnum(str, Enum):
    Sync = "SYNC"
    Once = "ONCE"
    Cron = "CRON"


class LocalClusterModeEnum(str, Enum):
    Local = "LOCAL"
    Remote = "REMOTE"


class ReasonerClient(Client):
    """SPG Reasoner Client."""

    _rest_client = rest.ReasonerApi()

    def __init__(self, host_addr: str = None, project_id: int = None):
        super().__init__(host_addr, project_id)

    def submit(self, dsl_content: str):
        """Submit an asynchronous reasoner job to the server by name."""
        content = rest.KgdslReasonerContent(kgdsl=dsl_content)
        request = rest.ReasonerJobSubmitRequest(
            job_name=_DEFAULT_JOB_NAME, project_id=self._project_id, content=content
        )
        return self._rest_client.reasoner_submit_job_info_post(
            reasoner_job_submit_request=request
        )

    def execute(self, dsl_content: str, output_file: str = None):
        """
        --projectId 2 \ --query "MATCH (s:`RiskMining.TaxOfRiskUser`/`赌博App开发者`) RETURN s.id,s.name " \ --output ./reasoner.csv \ --schemaUrl "http://localhost:8887" \ --graphStateClass "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState" \ --graphStoreUrl "tugraph://127.0.0.1:9090?graphName=default&timeout=60000&accessId=admin&accessKey=73@TuGraph" \
        """

        import subprocess
        import datetime
        from knext import lib

        jar_path = os.path.join(lib.__path__[0], lib.LOCAL_REASONER_JAR)
        default_output_file = (
            f"./{datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')}.csv"
        )

        java_cmd = [
            "java",
            "-jar",
            jar_path,
            "--projectId",
            self._project_id,
            "--query",
            dsl_content,
            "--output",
            output_file or default_output_file,
            "--schemaUrl",
            os.environ.get("KNEXT_HOST_ADDR") or lib.LOCAL_SCHEMA_URL,
            "--graphStateClass",
            os.environ.get("KNEXT_GRAPH_STATE_CLASS") or lib.LOCAL_GRAPH_STATE_CLASS,
            "--graphStoreUrl",
            os.environ.get("KNEXT_GRAPH_STORE_URL") or lib.LOCAL_GRAPH_STORE_URL,
        ]

        print_java_cmd = [
            cmd if not cmd.startswith("{") else f"'{cmd}'" for cmd in java_cmd
        ]
        print_java_cmd = [
            cmd if not cmd.count(";") > 0 else f"'{cmd}'" for cmd in print_java_cmd
        ]
        import json

        print(json.dumps(" ".join(print_java_cmd))[1:-1].replace("'", '"'))

        subprocess.call(java_cmd)

    def run_dsl(self, dsl_content: str):
        """Submit a synchronization reasoner job by providing DSL content."""
        content = rest.KgdslReasonerContent(kgdsl=dsl_content)
        request = rest.ReasonerDslRunRequest(
            content=content, project_id=self._project_id
        )
        return self._rest_client.reasoner_run_dsl_post(reasoner_dsl_run_request=request)

    def query(self, job_inst_id: int):
        """Query status of a submitted reasoner job by job inst id."""
        return self._rest_client.reasoner_query_job_inst_get(job_inst_id=job_inst_id)
