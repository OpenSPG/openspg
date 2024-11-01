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

from knext.common.base.client import Client


class ThinkerClient(Client):
    """SPG Thinker Client."""

    def __init__(self, host_addr: str = None, project_id: int = None):
        super().__init__(host_addr, project_id)

    def execute(self, subject="", predicate="", object="", mode="spo", params=""):
        """
        Execute a synchronous builder job in local runner.
        """

        import subprocess
        from knext.reasoner import lib
        from knext.common import env

        jar_path = os.path.join(lib.__path__[0], lib.LOCAL_REASONER_JAR)

        java_cmd = [
            "java",
            "-cp",
            jar_path,
            "com.antgroup.openspg.reasoner.runner.local.thinker.LocalThinkerMain",
            "--projectId",
            self._project_id,
            "--subject",
            subject or "",
            "--predicate",
            predicate or "",
            "--object",
            object or "",
            "--mode",
            mode,
            "--params" or "",
            params,
            "--schemaUrl",
            os.environ.get("KNEXT_HOST_ADDR") or env.LOCAL_SCHEMA_URL,
            "--graphStateClass",
            os.environ.get("KNEXT_GRAPH_STATE_CLASS") or lib.LOCAL_GRAPH_STATE_CLASS,
            "--graphStoreUrl",
            os.environ.get("KNEXT_GRAPH_STORE_URL") or lib.LOCAL_GRAPH_STORE_URL,
            ]

        subprocess.call(java_cmd)