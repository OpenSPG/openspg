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


def is_openai_invoker(nn_config: dict) -> bool:
    """
    Check whether `nn_config` specifies OpenAI invoker.

    :type nn_config: dict
    :rtype: bool
    """
    from nn4k.consts import NN_NAME_KEY, NN_NAME_TEXT
    from nn4k.consts import NN_OPENAI_API_KEY_KEY
    from nn4k.consts import NN_OPENAI_API_BASE_KEY
    from nn4k.consts import NN_OPENAI_MAX_TOKENS_KEY
    from nn4k.consts import NN_OPENAI_GPT4_PREFIX
    from nn4k.consts import NN_OPENAI_GPT35_PREFIX
    from nn4k.utils.config_parsing import get_string_field

    nn_name = nn_config.get(NN_NAME_KEY)
    if nn_name is not None:
        nn_name = get_string_field(nn_config, NN_NAME_KEY, NN_NAME_TEXT)
        if nn_name.startswith(NN_OPENAI_GPT4_PREFIX) or nn_name.startswith(
            NN_OPENAI_GPT35_PREFIX
        ):
            return True
    keys = (NN_OPENAI_API_KEY_KEY, NN_OPENAI_API_BASE_KEY, NN_OPENAI_MAX_TOKENS_KEY)
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
    from nn4k.consts import NN_NAME_KEY, NN_NAME_TEXT
    from nn4k.consts import NN_LOCAL_HF_MODEL_CONFIG_FILE
    from nn4k.utils.config_parsing import get_string_field

    nn_name = nn_config.get(NN_NAME_KEY)
    if nn_name is not None:
        nn_name = get_string_field(nn_config, NN_NAME_KEY, NN_NAME_TEXT)
        if os.path.isdir(nn_name):
            file_path = os.path.join(nn_name, NN_LOCAL_HF_MODEL_CONFIG_FILE)
            if os.path.isfile(file_path):
                return True
    return False
