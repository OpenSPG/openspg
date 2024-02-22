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

import unittest
from dataclasses import dataclass, field
from typing import List, Optional


@dataclass
class TestArgs:
    input_columns: Optional[List[str]] = field(
        default=None,
        metadata={"help": ""},
    )
    is_bool: Optional[bool] = field(
        default=None,
        metadata={"help": ""},
    )
    max_input_length: int = field(
        default=1024,
        metadata={"help": ""},
    )
    lora_config: Optional[dict] = field(default=None)


class TestConfigParsing(unittest.TestCase):
    """
    module nn4k.utils.config_parsing unittest
    """

    def testPreprocessConfigFile(self):
        import os
        from nn4k.utils.config_parsing import preprocess_config

        dir_path = os.path.dirname(os.path.abspath(__file__))
        file_path = os.path.join(dir_path, "test_config.json")
        nn_config = preprocess_config(file_path)
        self.assertEqual(nn_config, {"foo": "bar"})

    def testPreprocessConfigFileNotExists(self):
        import os
        from nn4k.utils.config_parsing import preprocess_config

        dir_path = os.path.dirname(os.path.abspath(__file__))
        file_path = os.path.join(dir_path, "not_exists.json")
        with self.assertRaises(ValueError):
            nn_config = preprocess_config(file_path)

    def testPreprocessConfigDict(self):
        from nn4k.utils.config_parsing import preprocess_config

        conf = {"foo": "bar"}
        nn_config = preprocess_config(conf)
        self.assertEqual(nn_config, conf)

    def testGetField(self):
        from nn4k.utils.config_parsing import get_field

        nn_config = {"foo": "bar"}
        value = get_field(nn_config, "foo", "Foo")
        self.assertEqual(value, "bar")

    def testGetFieldNotExists(self):
        from nn4k.utils.config_parsing import get_field

        nn_config = {"foo": "bar"}
        with self.assertRaises(ValueError):
            value = get_field(nn_config, "not_exists", "not exists")

    def testGetStringField(self):
        from nn4k.utils.config_parsing import get_string_field

        nn_config = {"foo": "bar"}
        value = get_string_field(nn_config, "foo", "Foo")
        self.assertEqual(value, "bar")

    def testGetStringFieldNotString(self):
        from nn4k.utils.config_parsing import get_string_field

        nn_config = {"foo": "bar", "baz": True}
        with self.assertRaises(TypeError):
            value = get_string_field(nn_config, "baz", "Baz")

    def testGetIntField(self):
        from nn4k.utils.config_parsing import get_int_field

        nn_config = {"foo": "bar", "baz": 1000}
        value = get_int_field(nn_config, "baz", "Baz")
        self.assertEqual(value, 1000)

    def testGetIntFieldNotInteger(self):
        from nn4k.utils.config_parsing import get_int_field

        nn_config = {"foo": "bar", "baz": "quux"}
        with self.assertRaises(TypeError):
            value = get_int_field(nn_config, "baz", "Baz")

    def testGetPositiveIntField(self):
        from nn4k.utils.config_parsing import get_positive_int_field

        nn_config = {"foo": "bar", "baz": 1000}
        value = get_positive_int_field(nn_config, "baz", "Baz")
        self.assertEqual(value, 1000)

    def testGetPositiveIntFieldNotPositive(self):
        from nn4k.utils.config_parsing import get_positive_int_field

        nn_config = {"foo": "bar", "baz": 0}
        with self.assertRaises(ValueError):
            value = get_positive_int_field(nn_config, "baz", "Baz")

    def testTransformerArgsParseDict(self):
        from transformers import HfArgumentParser

        args = {
            "input_columns": ["column1", "column2"],
            "is_bool": False,
            "max_input_length": 256,
            "lora_config": {"r": 1, "type": "lora"},
            "is_bool_int": 1,
            "extra_arg": "extra_configs",
        }

        parser = HfArgumentParser(TestArgs)
        parsed_args: TestArgs
        parsed_args, *rest = parser.parse_dict(args, allow_extra_keys=True)

        self.assertEqual(parsed_args.input_columns, ["column1", "column2"])
        self.assertEqual(parsed_args.is_bool, False)
        self.assertEqual(parsed_args.lora_config, {"type": "lora", "r": 1})
        self.assertEqual(parsed_args.max_input_length, 256)


if __name__ == "__main__":
    unittest.main()
