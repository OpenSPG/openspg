# -*- coding: utf-8 -*-
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
from pathlib import Path
from shutil import copystat, copy2
from typing import Any, Union
from jinja2 import Environment, FileSystemLoader
from stat import S_IWUSR as OWNER_WRITE_PERMISSION


def render_template(
    root: Union[str, os.PathLike], file: Union[str, os.PathLike], **kwargs: Any
) -> None:
    env = Environment(loader=FileSystemLoader(root))
    template = env.get_template(str(file))
    content = template.render(kwargs)

    path_obj = Path(root) / file
    render_path = path_obj.with_suffix("") if path_obj.suffix == ".tmpl" else path_obj

    if path_obj.suffix == ".tmpl":
        path_obj.rename(render_path)

    render_path.write_text(content, "utf8")


def copytree(src: Path, dst: Path, project_name: str):
    import knext

    template_dir = os.path.join(knext.__path__[0], "templates")
    src = Path(template_dir) / src
    names = [x.name for x in src.iterdir()]

    if not dst.exists():
        dst.mkdir(parents=True)

    for name in names:
        _name = name.replace("${project}", project_name)
        src_name = src / name
        dst_name = dst / _name
        if src_name.is_dir():
            copytree(src_name, dst_name, project_name)
        else:
            copy2(src_name, dst_name)
            _make_writable(dst_name)

    copystat(src, dst)
    _make_writable(dst)


def _make_writable(path):
    current_permissions = os.stat(path).st_mode
    os.chmod(path, current_permissions | OWNER_WRITE_PERMISSION)
