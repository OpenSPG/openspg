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

import json

from typing import Any
from typing import Union


def preprocess_config(nn_config: Union[str, dict]) -> dict:
    try:
        if isinstance(nn_config, str):
            with open(nn_config, "r") as f:
                nn_config = json.load(f)
    except:
        raise ValueError("cannot decode config file")
    return nn_config

def get_field(nn_config: dict, name: str, text: str) -> Any:
    value = nn_config.get(name)
    if value is None:
        message = "%s %r not found" % (text, name)
        raise ValueError(message)
    return value

def get_string_field(nn_config: dict, name: str, text: str) -> str:
    value = get_field(nn_config, name, text)
    if not isinstance(value, str):
        message = "%s %r must be string; " % (text, name)
        message += "%r is invalid" % (value,)
        raise TypeError(message)
    return value

def get_int_field(nn_config: dict, name: str, text: str) -> int:
    value = get_field(nn_config, name, text)
    if not isinstance(value, int):
        message = "%s %r must be integer; " % (text, name)
        message += "%r is invalid" % (value,)
        raise TypeError(message)
    return value

def get_positive_int_field(nn_config: dict, name: str, text: str) -> int:
    value = get_int_field(nn_config, name, text)
    if value <= 0:
        message = "%s %r must be positive integer; " % (text, name)
        message += "%r is invalid" % (value,)
        raise ValueError(message)
    return value
