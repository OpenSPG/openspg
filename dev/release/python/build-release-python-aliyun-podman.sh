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
VERSION="0.7"
LATEST="latest"

# 构建 ARM64 镜像
podman build -f Dockerfile --platform linux/arm64/v8 --build-arg MINICONDA_FILE=Miniconda3-py310_25.1.1-2-Linux-aarch64.sh \
    -t ${IMAGE}:${VERSION}-arm64 .
podman push ${IMAGE}:${VERSION}-arm64

# 构建 AMD64 镜像
podman build -f Dockerfile --platform linux/amd64 --build-arg MINICONDA_FILE=Miniconda3-py310_25.1.1-2-Linux-x86_64.sh \
    -t ${IMAGE}:${VERSION}-amd64 .
podman push ${IMAGE}:${VERSION}-amd64

# 检查并移除已存在的 manifest（如果存在）
if podman manifest inspect ${IMAGE}:${VERSION} &>/dev/null; then
    echo "Manifest already exists, removing it..."
    podman manifest rm ${IMAGE}:${VERSION}
else
    echo "Manifest does not exist, proceeding with creation and push."
fi

# 创建并推送多架构 manifest（VERSION 标签）
podman manifest create ${IMAGE}:${VERSION}
podman manifest add ${IMAGE}:${VERSION} docker://${IMAGE}:${VERSION}-amd64
podman manifest add ${IMAGE}:${VERSION} docker://${IMAGE}:${VERSION}-arm64
podman manifest push ${IMAGE}:${VERSION}

# 检查并移除已存在的 latest manifest（如果存在）
if podman manifest inspect ${IMAGE}:${LATEST} &>/dev/null; then
    echo "Manifest already exists, removing it..."
    podman manifest rm ${IMAGE}:${LATEST}
else
    echo "Manifest does not exist, proceeding with creation and push."
fi

# 创建并推送多架构 manifest（LATEST 标签）
podman manifest create ${IMAGE}:${LATEST}
podman manifest add ${IMAGE}:${LATEST} docker://${IMAGE}:${VERSION}-amd64
podman manifest add ${IMAGE}:${LATEST} docker://${IMAGE}:${VERSION}-arm64
podman manifest push ${IMAGE}:${LATEST}
