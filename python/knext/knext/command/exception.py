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
from json import JSONDecodeError
from typing import Any

import json

import click
from click import Context, Group

from knext.rest import ApiException


class _ApiExceptionHandler(Group):
    """Echo exceptions."""

    def invoke(self, ctx: Context) -> Any:
        if os.getenv("KNEXT_DEBUG_MODE", "False") == "True":
            return super().invoke(ctx)
        try:
            return super().invoke(ctx)
        except ApiException as api:
            try:
                body = json.loads(api.body)
            except JSONDecodeError:
                raise api
            click.secho("ERROR: " + body, fg="bright_red")
        except Exception as e:
            click.secho("ERROR: " + e.__str__(), fg="bright_red")
