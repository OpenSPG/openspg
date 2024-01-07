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

# for amd64
docker build -f Dockerfile --platform linux/amd64 --push \
  -t openspg/openspg-server-amd64:0.0.2-beta1 \
  -t openspg/openspg-server-amd64:latest \
  .

# for arm64-v8
docker build -f Dockerfile --platform linux/arm64/v8 --push \
  -t openspg/openspg-server-arm64v8:0.0.2-beta1 \
  -t openspg/openspg-server-arm64v8:latest \
  .
