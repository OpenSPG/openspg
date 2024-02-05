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


if __name__ == "__main__":
    unittest.main()
