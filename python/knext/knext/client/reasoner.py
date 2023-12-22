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
