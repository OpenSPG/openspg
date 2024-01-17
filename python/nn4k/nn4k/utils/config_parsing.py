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

import json

from typing import Any
from typing import Union


def preprocess_config(nn_config: Union[str, dict]) -> dict:
    """
    Preprocess config `nn_config` into a dictionary.

    * If `nn_config` is already a dictionary, return it as is.

    * If `nn_config` is a string, decode it as a JSON file.

    :param nn_config: config to be preprocessed
    :type nn_config: str or dict
    :return: `nn_config` or `nn_config` decoded as JSON
    :rtype: dict
    :raises ValueError: if cannot decode config file specified by
                        `nn_config` as JSON
    """
    try:
        if isinstance(nn_config, str):
            with open(nn_config, "r") as f:
                nn_config = json.load(f)
    except:
        raise ValueError("cannot decode config file")
    return nn_config


def get_field(nn_config: dict, name: str, text: str) -> Any:
    """
    Get the value of the field specified by `name` from the configuration
    dictionary `nn_config`.

    :param str name: name of the field
    :param str name: descriptive text of the name of the field
    :return: value of the field
    :rtype: Any
    :raises ValueError: if the field is not specified in `nn_config`
    """
    value = nn_config.get(name)
    if value is None:
        message = "%s %r not found" % (text, name)
        raise ValueError(message)
    return value


def get_string_field(nn_config: dict, name: str, text: str) -> str:
    """
    Get the value of the string field specified by `name` from the
    configuration dictionary `nn_config`.

    :param str name: name of the field
    :param str name: descriptive text of the name of the field
    :return: value of the field
    :rtype: str
    :raises ValueError: if the field is not specified in `nn_config`
    :raises TypeError: if the value of the field is not a string
    """
    value = get_field(nn_config, name, text)
    if not isinstance(value, str):
        message = "%s %r must be string; " % (text, name)
        message += "%r is invalid" % (value,)
        raise TypeError(message)
    return value


def get_int_field(nn_config: dict, name: str, text: str) -> int:
    """
    Get the value of the integer field specified by `name` from the
    configuration dictionary `nn_config`.

    :param str name: name of the field
    :param str name: descriptive text of the name of the field
    :return: value of the field
    :rtype: int
    :raises ValueError: if the field is not specified in `nn_config`
    :raises TypeError: if the value of the field is not an integer
    """
    value = get_field(nn_config, name, text)
    if not isinstance(value, int):
        message = "%s %r must be integer; " % (text, name)
        message += "%r is invalid" % (value,)
        raise TypeError(message)
    return value


def get_positive_int_field(nn_config: dict, name: str, text: str) -> int:
    """
    Get the value of the positive integer field specified by `name`
    from the configuration dictionary `nn_config`.

    :param str name: name of the field
    :param str name: descriptive text of the name of the field
    :return: value of the field
    :rtype: int
    :raises ValueError: if the field is not specified in `nn_config`, or the
                        value of the field is not a positive integer
    :raises TypeError: if the value of the field is not an integer
    """
    value = get_int_field(nn_config, name, text)
    if value <= 0:
        message = "%s %r must be positive integer; " % (text, name)
        message += "%r is invalid" % (value,)
        raise ValueError(message)
    return value
