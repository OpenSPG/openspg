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
import sys
from configparser import ConfigParser
from pathlib import Path
from typing import Union, Optional


GLOBAL_CONFIG = ["host_addr", "graph_store_url", "search_engine_url"]
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


def init_env():
    """Initialize environment to use command-line tool from inside a project
    dir. This sets the Scrapy settings module and modifies the Python path to
    be able to locate the project module.
    """
    project_cfg, root_path = get_config()

    if project_cfg.has_section("global"):
        for cfg in GLOBAL_CONFIG:
            if project_cfg.has_option("global", cfg):
                os.environ[CFG_PREFIX + cfg.upper()] = project_cfg.get("global", cfg)

    if project_cfg.has_section("local"):
        for cfg in LOCAL_CONFIG:
            if project_cfg.has_option("local", cfg):
                os.environ[CFG_PREFIX + cfg.upper()] = project_cfg.get("local", cfg)

        os.environ[CFG_PREFIX + "ROOT_PATH"] = str(root_path.resolve())

    load_operator()
    load_builder_job()


def get_config():
    """
    Get knext config file as a ConfigParser.
    """
    global_cfg, _, local_cfg, local_cfg_path = get_cfg_files()

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


def get_cfg_files():
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


def load_operator():
    """
    Load all operators in [builder_operator_dir].
    """
    from knext.operator.base import BaseOp
    from knext.operator import builtin

    if not BaseOp._has_registered and (
        "KNEXT_ROOT_PATH" in os.environ and "KNEXT_BUILDER_OPERATOR_DIR" in os.environ
    ):

        from knext.common.class_register import register_from_package

        builder_operator_path = os.path.join(
            os.environ["KNEXT_ROOT_PATH"], os.environ["KNEXT_BUILDER_OPERATOR_DIR"]
        )

        register_from_package(builder_operator_path, BaseOp)
        sys.path.append(builtin.__path__[0])


def load_builder_job():
    """
    Load all builder jobs in [builder_job_dir].
    """
    from knext.client.model.builder_job import BuilderJob

    if not BuilderJob._has_registered and (
        "KNEXT_ROOT_PATH" in os.environ and "KNEXT_BUILDER_JOB_DIR" in os.environ
    ):

        from knext.common.class_register import register_from_package

        builder_operator_path = os.path.join(
            os.environ["KNEXT_ROOT_PATH"], os.environ["KNEXT_BUILDER_JOB_DIR"]
        )

        register_from_package(builder_operator_path, BuilderJob)
