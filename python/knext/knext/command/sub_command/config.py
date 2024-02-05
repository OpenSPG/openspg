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

from knext.common.env import get_cfg_files, get_config


@click.option("--global", multiple=True)
@click.option("--local", multiple=True)
def edit_config(**kwargs):
    """
    Edit global or local configs.
    """
    global_cfg, global_cfg_path, local_cfg, local_cfg_path = get_cfg_files()
    for option, args in kwargs.items():
        if option == "global":
            if not global_cfg.has_section("global"):
                global_cfg.add_section("global")
            for arg in args:
                key, value = arg.split("=", 1)
                global_cfg.set("global", key, value)
                with open(global_cfg_path, "w") as config_file:
                    global_cfg.write(config_file)
        if option == "local":
            for arg in args:
                key, value = arg.split("=", 1)
                local_cfg.set("local", key, value)
                with open(local_cfg_path, "w") as config_file:
                    local_cfg.write(config_file)


def list_config():
    """
    List global and local knext configs.
    """
    config, _ = get_config()
    click.echo("[global]")
    if config.has_section("global"):
        for key, value in config.items("global"):
            click.echo(f"{key} = {value}")
    click.echo("[local]")
    if config.has_section("local"):
        for key, value in config.items("local"):
            click.echo(f"{key} = {value}")
