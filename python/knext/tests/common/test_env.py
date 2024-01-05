# -*- coding: utf-8 -*-
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
import shutil
from configparser import ConfigParser
from knext.common.env import get_cfg_files, get_config

PWD = os.path.dirname(__file__)


def prepare():
    src = os.path.join(PWD, "resource/cfg")

    dst1 = os.path.abspath(os.path.join(PWD, "../.knext.cfg"))
    shutil.copyfile(src, dst1)
    global_conf_path = os.path.expanduser("~/.config")
    if os.path.exists(global_conf_path):
        dst2 = os.path.join(global_conf_path, ".knext.cfg")
        # empty global conf
        open(dst2, "w").close()
        return [dst1, dst2]
    return [dst1]


def clean(files):
    for file_ in files:
        os.remove(file_)


def diff_conf(conf1, conf2):
    l1_keys = [list(conf1.keys()), list(conf2.keys())]
    if l1_keys[0] != l1_keys[0]:
        raise ValueError("conf contains different keys: {l1_keys}")
    for k in l1_keys[0]:
        if dict(conf1[k]) != dict(conf2[k]):
            raise ValueError(f"key {k} mismatch")


def _test_get_cfg_files(dsts):
    global_conf, global_conf_path, local_conf, local_conf_path = get_cfg_files()
    print(f"global_conf = {global_conf}")
    print(f"global_conf_path = {global_conf_path}")
    print(f"local_conf = {local_conf}")
    print(f"local_conf_path = {local_conf_path}")
    print(list(global_conf.keys()))
    assert isinstance(global_conf, ConfigParser)
    assert isinstance(local_conf, ConfigParser)
    assert (
        str(local_conf_path) == dsts[0]
    ), f"local config path should be {dsts[0]}, got {local_conf_path}"
    if len(dsts) == 2:
        assert (
            str(global_conf_path) == dsts[1]
        ), f"global config path should be {dsts[1]}, got {global_conf_path}"


def _test_get_cfg(dsts):
    conf, _ = get_config()
    conf_correct = ConfigParser()
    conf_correct.read(dsts[0])
    diff_conf(conf_correct, conf)


def test_get_cfg_files():
    dsts = prepare()
    print(f"dsts = {dsts}")
    try:
        _test_get_cfg_files(dsts)
    except Exception as e:
        clean(dsts)
        raise e
    clean(dsts)


def test_get_config():
    dsts = prepare()
    print(f"dsts = {dsts}")
    try:
        _test_get_cfg(dsts)
    except Exception as e:
        clean(dsts)
        raise e
    clean(dsts)
