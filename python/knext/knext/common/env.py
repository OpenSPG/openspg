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
import logging
import os
import sys
from configparser import ConfigParser as CP
import yaml
from pathlib import Path
from typing import Union, Optional

import knext.common as common


class ConfigParser(CP):
    def __init__(self, defaults=None):
        CP.__init__(self, defaults=defaults)

    def optionxform(self, optionstr):
        return optionstr


LOCAL_SCHEMA_URL = "http://localhost:8887"
DEFAULT_KAG_CONFIG_FILE_NAME = "default_config.cfg"
DEFAULT_KAG_CONFIG_PATH = os.path.join(common.__path__[0], DEFAULT_KAG_CONFIG_FILE_NAME)
KAG_CFG_PREFIX = "KAG"


def init_env():
    """Initialize environment to use command-line tool from inside a project
    dir. This sets the Scrapy settings module and modifies the Python path to
    be able to locate the project module.
    """
    project_cfg, root_path = get_config()

    config = yaml.safe_load(
        Path(os.path.join(root_path, "kag_config.yaml")).read_text()
    )
    project_config = config.get("project", {})
    project_id = project_config.get("project_id", None)
    os.environ["KAG_PROJECT_ID"] = project_id
    log_config = config.get("log", {})
    value = log_config.get("level", "INFO")
    logging.basicConfig(level=logging.getLevelName(value))


def get_config():
    """
    Get knext config file as a ConfigParser.
    """
    local_cfg_path = _closest_cfg()
    local_cfg = yaml.safe_load(Path(local_cfg_path).read_text())

    projdir = ""
    if local_cfg_path:
        projdir = str(Path(local_cfg_path).parent)
        if projdir not in sys.path:
            sys.path.append(projdir)

    return local_cfg, projdir


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
    cfg_file = path / "kag_config.yaml"
    if cfg_file.exists():
        return str(cfg_file)
    return _closest_cfg(path.parent, path)
