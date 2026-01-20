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
    $APT_INSTALL openjdk-17-jdk && \
    apt-get clean

RUN JAVA_HOME=$(find /usr/lib/jvm/java-17-openjdk-* -maxdepth 0 -type d | head -1) && \
    if [ -f "$JAVA_HOME/jre/bin/java" ]; then \
        update-alternatives --set java "$JAVA_HOME/jre/bin/java"; \
    elif [ -f "$JAVA_HOME/bin/java" ]; then \
        update-alternatives --set java "$JAVA_HOME/bin/java"; \
    fi && \
    echo "export JAVA_HOME=${JAVA_HOME}" >> /etc/profile

# Install pemja using the installed Miniconda
RUN python -m pip install --no-cache-dir pemja==0.4.0 && \
    # Clean up to reduce layer size
    conda clean -afy && \
    find $CONDA_DIR -follow -type f -name '*.a' -delete && \
    find $CONDA_DIR -follow -type f -name '*.pyc' -delete

EXPOSE 8887

ADD target/arks-sofaboot-0.0.1-SNAPSHOT-executable.jar /
ENTRYPOINT ["java", "-jar", "arks-sofaboot-0.0.1-SNAPSHOT-executable.jar"]
