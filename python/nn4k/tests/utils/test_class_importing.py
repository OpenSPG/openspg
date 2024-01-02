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

import unittest


class TestClassImporting(unittest.TestCase):
    """
    module nn4k.utils.class_importing unittest
    """

    def testSplitModuleClassName(self):
        from nn4k.utils.class_importing import split_module_class_name

        pair = split_module_class_name("foo.bar.Baz", "test")
        self.assertEqual(pair, ("foo.bar", "Baz"))

    def testSplitModuleClassNameInvalid(self):
        from nn4k.utils.class_importing import split_module_class_name

        with self.assertRaises(RuntimeError):
            pair = split_module_class_name("foo", "test")

    def testDynamicImportClass(self):
        from nn4k.utils.class_importing import dynamic_import_class

        class_ = dynamic_import_class("unittest.TestCase", "test")
        self.assertEqual(class_, unittest.TestCase)

    def testDynamicImportClassModuleNotFound(self):
        from nn4k.utils.class_importing import dynamic_import_class

        with self.assertRaises(ModuleNotFoundError):
            class_ = dynamic_import_class("not_exists.ClassName", "test")

    def testDynamicImportClassClassNotFound(self):
        from nn4k.utils.class_importing import dynamic_import_class

        with self.assertRaises(RuntimeError):
            class_ = dynamic_import_class("unittest.NotExists", "test")

    def testDynamicImportClassNotClass(self):
        from nn4k.utils.class_importing import dynamic_import_class

        with self.assertRaises(RuntimeError):
            class_ = dynamic_import_class("unittest.mock", "test")


if __name__ == "__main__":
    unittest.main()
