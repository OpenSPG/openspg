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

import os
import sys
from configparser import ConfigParser
from pathlib import Path
from typing import Union, Optional

import click

GLOBAL_CONFIG = ["host_addr"]
LOCAL_CONFIG = [
    "project_name",
    "project_id",
    "namespace",
    "description",
    "project_dir",
    "schema_dir",
    "schema_file",
    "builder_dir",
    "builder_record_dir",
    "builder_operator_dir",
    "builder_job_dir",
    "builder_model_dir",
    "reasoner_dir",
    "reasoner_result_dir",
]
CFG_PREFIX = "KNEXT_"


@click.option("--global", multiple=True)
@click.option("--local", multiple=True)
def edit_config(**kwargs):
    """
    Edit global or local configs.
    """
    global_cfg, global_cfg_path, local_cfg, local_cfg_path = _get_cfg_files()
    for option, args in kwargs.items():
        if option == "global":
            if not global_cfg.has_section("global"):
                global_cfg.add_section("global")
            for arg in args:
                key, value = arg.split("=")
                global_cfg.set("global", key, value)
                with open(global_cfg_path, "w") as config_file:
                    global_cfg.write(config_file)
        if option == "local":
            for arg in args:
                key, value = arg.split("=")
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


def get_config():
    """
    Get knext config file as a ConfigParser.
    """
    global_cfg, _, local_cfg, local_cfg_path = _get_cfg_files()

    for section in global_cfg.sections():
        local_cfg.add_section(section)
        for key, value in global_cfg.items(section):
            local_cfg.set(section, key, value)

    return local_cfg, Path(local_cfg_path).parent


def _closest_cfg(
    path: Union[str, os.PathLike] = ".",
    prev_path: Optional[Union[str, os.PathLike]] = None,
) -> str:
    """
    Return the path to the closest .knext.cfg file by traversing the current
    directory and its parents
    """
    if prev_path is not None and str(path) == str(prev_path):
        return ""
    path = Path(path).resolve()
    cfg_file = path / ".knext.cfg"
    if cfg_file.exists():
        return str(cfg_file)
    return _closest_cfg(path.parent, path)


def _get_cfg_files():
    """
    Get global and local knext config files and paths.
    """
    global_cfg_path = (
                          Path(os.environ.get("XDG_CONFIG_HOME") or "~/.config").expanduser()
                      ) / ".knext.cfg"
    if not global_cfg_path.parent.exists():
        Path.mkdir(global_cfg_path.parent)
    global_cfg = ConfigParser()
    global_cfg.read(global_cfg_path)
    local_cfg_path = _closest_cfg()
    local_cfg = ConfigParser()
    local_cfg.read(local_cfg_path)

    if local_cfg_path:
        projdir = str(Path(local_cfg_path).parent)
        if projdir not in sys.path:
            sys.path.append(projdir)

    return global_cfg, global_cfg_path, local_cfg, local_cfg_path
