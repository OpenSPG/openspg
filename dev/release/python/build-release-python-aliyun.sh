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

IMAGE="spg-registry.cn-hangzhou.cr.aliyuncs.com/spg/openspg-python"
VERSION="0.5.1"
cd ../../../../
docker build -f openspg/dev/release/python/Dockerfile --platform linux/arm64/v8 --push \
  -t  ${IMAGE}:${VERSION}-arm64 \
  .
docker build -f openspg/dev/release/python/Dockerfile --platform linux/amd64 --push \
  -t  ${IMAGE}:${VERSION}-amd64 \
  .

if docker manifest inspect ${IMAGE}:${VERSION} &> /dev/null; then
  echo "Manifest already exists, removing it..."
  docker manifest rm ${IMAGE}:${VERSION}
else
  echo "Manifest does not exist, proceeding with creation and push."
fi

docker manifest create \
   ${IMAGE}:${VERSION} \
   ${IMAGE}:${VERSION}-amd64 \
   ${IMAGE}:${VERSION}-arm64

docker manifest push  ${IMAGE}:${VERSION}

if docker manifest inspect ${IMAGE}:${LATEST} &> /dev/null; then
  echo "Manifest already exists, removing it..."
  docker manifest rm ${IMAGE}:${LATEST}
else
  echo "Manifest does not exist, proceeding with creation and push."
fi

docker manifest create \
  ${IMAGE}:${LATEST} \
  ${IMAGE}:${VERSION}-amd64 \
  ${IMAGE}:${VERSION}-arm64

docker manifest push ${IMAGE}:${LATEST}