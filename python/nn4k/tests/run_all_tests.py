#!/usr/bin/env python
# -*- encoding: utf-8 -*-
# -*- mode: python -*-

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

from __future__ import print_function
from __future__ import absolute_import
from __future__ import division


class NN4KTestsRunner(object):
    def _run_all_tests(self):
        import os
        import sys
        import subprocess

        dir_path = os.path.dirname(os.path.abspath(__file__))

        restore_script_path = os.path.join(dir_path, "python-env", ".env.restore.sh")
        args = [restore_script_path]
        subprocess.check_call(args)

        nn4k_dir_path = os.path.dirname(dir_path)
        python_executable_path = os.path.join(
            dir_path, "python-env", ".env", "bin", "python"
        )
        saved_dir_path = os.getcwd()
        os.chdir(dir_path)

        args = ["env", "PYTHONPATH=%s" % nn4k_dir_path]
        args += [python_executable_path]
        args += ["-m", "unittest"]
        try:
            subprocess.check_call(args)
        except subprocess.CalledProcessError:
            raise SystemExit(1)
        finally:
            os.chdir(saved_dir_path)

    def run(self):
        self._run_all_tests()


def main():
    runner = NN4KTestsRunner()
    runner.run()


if __name__ == "__main__":
    main()
