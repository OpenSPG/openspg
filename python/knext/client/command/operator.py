# -*- coding: utf-8 -*-
#
#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

import click

from knext.core.builder.operator import Operator


@click.argument("op_names", required=True)
def publish_operator(op_names):
    """
    Publish operators to server.
    """
    client = Operator()

    op_list = [name.strip() for name in op_names.split(",") if name]
    for op_name in op_list:
        op = client.publish(op_name)
        click.secho(
            f"Operator [{op_name}] has been successfully published. The latest version is {op._version}",
            fg="bright_green",
        )


def list_operator():
    """
    List all server-side operators.
    """
    click.secho("Not support yet.", fg="bright_yellow")
