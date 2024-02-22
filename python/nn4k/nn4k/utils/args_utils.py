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


class ArgsUtils:
    CONFIG_FILE_KEY = "config_file"

    @staticmethod
    def update_args(base_args: dict, new_args: dict) -> dict:
        """
        update an existing args with a new set of args
        :param base_args: args to get updated. Will be copied before get updated.
        :param new_args: args to update the base args.
        :rtype: dict
        """
        import copy

        copy_base_args = copy.deepcopy(base_args)
        new_args = new_args or {}
        copy_base_args.update(new_args)
        return copy_base_args

    @staticmethod
    def handle_dict_config(kwargs: dict) -> dict:
        if "config_file" in kwargs:
            configs = ArgsUtils.load_config_dict_from_file(kwargs.get("config_file"))
        else:
            configs = kwargs

        return configs

    @staticmethod
    def load_config_dict_from_file(file_path: str) -> dict:
        from pathlib import Path

        if file_path.endswith(".json"):
            import json

            with open(Path(file_path), "r", encoding="utf-8") as open_json_file:
                data = json.load(open_json_file)
                nn_config = data
                return nn_config
        if file_path.endswith(".json5"):
            import json5

            with open(Path(file_path), "r", encoding="utf-8") as open_json5_file:
                data = json5.load(open_json5_file)
                nn_config = data
                return nn_config
        from nn4k.utils.io.file_utils import FileUtils

        raise ValueError(
            f"Config file with extension type {FileUtils.get_extension(file_path)} is not supported."
            f"use json or json5 instead."
        )
