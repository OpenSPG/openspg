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


def is_openai_invoker(nn_config: dict) -> bool:
    """
    Check whether `nn_config` specifies OpenAI invoker.

    :type nn_config: dict
    :rtype: bool
    """
    from nn4k.utils.config_parsing import get_string_field

    nn_name = nn_config.get("nn_name")
    if nn_name is not None:
        nn_name = get_string_field(nn_config, "nn_name", "NN name")
        if nn_name.startswith("gpt-4") or nn_name.startswith("gpt-3.5"):
            return True
    keys = ("openai_api_key", "openai_api_base", "openai_max_tokens")
    for key in keys:
        if key in nn_config:
            return True
    return False


def is_local_invoker(nn_config: dict) -> bool:
    """
    Check whether `nn_config` specifies local invoker.

    :type nn_config: dict
    :rtype: bool
    """
    from nn4k.utils.config_parsing import get_string_field

    nn_name = nn_config.get("nn_name")
    if nn_name is not None:
        nn_name = get_string_field(nn_config, "nn_name", "NN name")
        if os.path.isdir(nn_name):
            file_path = os.path.join(nn_name, "config.json")
            if os.path.isfile(file_path):
                return True
    return False
