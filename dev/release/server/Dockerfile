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

FROM registry.cn-hangzhou.aliyuncs.com/lacogito/ubuntu:22.04

# For apt to be noninteractive
ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN=true
ENV LANG=C.UTF-8
ENV CONDA_DIR=/home/admin/miniconda3
ENV PATH=${CONDA_DIR}/bin:${PATH}

ARG TARGETPLATFORM
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

# Install Miniconda and Pemja
RUN mkdir -p /home/admin && \
    wget --quiet --no-check-certificate https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -O miniconda.sh && \
    /bin/bash miniconda.sh -b -p /home/admin/miniconda3 && \
    rm miniconda.sh && \
    # Ensure we use the new python to install pemja
    python -m pip install --no-cache-dir pemja==0.4.0 && \
    # Clean up to reduce layer size
    conda clean -afy && \
    find /home/admin/miniconda3/ -follow -type f -name '*.a' -delete && \
    find /home/admin/miniconda3/ -follow -type f -name '*.pyc' -delete

EXPOSE 8887

ADD target/arks-sofaboot-0.0.1-SNAPSHOT-executable.jar /
ENTRYPOINT ["java", "-jar", "arks-sofaboot-0.0.1-SNAPSHOT-executable.jar"]
