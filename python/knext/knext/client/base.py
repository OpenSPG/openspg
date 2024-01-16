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
from abc import ABC

from knext import rest


class Client(ABC):

    _rest_client: rest.BaseApi

    def __init__(self, host_addr: str = None, project_id: int = None):
        self._host_addr = host_addr or os.environ.get("KNEXT_HOST_ADDR")
        self._project_id = project_id or os.environ.get("KNEXT_PROJECT_ID")

    def serialize(self, obj):
        return self._rest_client.api_client.sanitize_for_serialization(obj)
