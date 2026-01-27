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

# Image for building OpenSPG releases. Based on Ubuntu 20.04.
#
# Includes:
# * Java 17

# Base image with conda installed at /opt/conda
FROM registry.cn-hangzhou.aliyuncs.com/lacogito/conda-forge:ubuntu2204

# Replace apt sources with Aliyun mirror for faster downloads
RUN sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list

# For apt to be noninteractive
ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN=true
ENV LANG=C.UTF-8

ARG APT_INSTALL="apt-get install --no-install-recommends -y"

RUN apt-get clean && apt-get update && \
    $APT_INSTALL git less vim wget && \
    $APT_INSTALL openjdk-17-jre && \
    apt-get clean

RUN JAVA_HOME=$(find /usr/lib/jvm/java-17-openjdk-* -maxdepth 0 -type d | head -1) && \
    update-alternatives --set java "$JAVA_HOME/bin/java" && \
    echo "export JAVA_HOME=${JAVA_HOME}" >> /etc/profile

# Create a new environment named 'app_env' with Python 3.10
RUN conda create -n app_env python=3.10 -y && conda clean -afy

# Ensure the environment is activated by default in the shell
RUN echo "conda activate app_env" >> ~/.bashrc
ENV CONDA_DEFAULT_ENV=app_env

# Install pemja using the conda environment with Python 3.10
RUN conda run -n app_env pip install --no-cache-dir -i https://mirrors.aliyun.com/pypi/simple/ pemja==0.4.0 && \
    conda run -n app_env pip install --no-cache-dir -i https://mirrors.aliyun.com/pypi/simple/ openspg-kag>=0.8.0 && \
    conda clean -afy && \
    find $CONDA_DIR -follow -type f -name '*.a' -delete && \
    find $CONDA_DIR -follow -type f -name '*.pyc' -delete

WORKDIR /app
EXPOSE 8887

ADD target/arks-sofaboot-0.0.1-SNAPSHOT-executable.jar /app/
ENTRYPOINT ["java", "-jar", "/app/arks-sofaboot-0.0.1-SNAPSHOT-executable.jar"]
