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

# for amd64
docker build -f Dockerfile --platform linux/amd64 --push \
  -t openspg-registry.cn-hangzhou.cr.aliyuncs.com/openspg/openspg-python:0.0.3 \
  -t openspg-registry.cn-hangzhou.cr.aliyuncs.com/openspg/openspg-python:latest \
  -t openspg/openspg-python:0.0.3 \
  -t openspg/openspg-python:latest \
  .
