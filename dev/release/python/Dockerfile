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

FROM m.daocloud.io/ubuntu:20.04

# For apt to be noninteractive
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV LANG C.UTF-8

ARG TARGETPLATFORM
ARG APT_INSTALL="apt-get install --no-install-recommends -y"

RUN apt-get clean && apt-get update && \
    $APT_INSTALL ca-certificates && \
    $APT_INSTALL ca-certificates-java && \
    $APT_INSTALL git less vim && \
    # Install openjdk 8.
    $APT_INSTALL openjdk-8-jdk && \
    update-alternatives --set java /usr/lib/jvm/java-8-openjdk-$(dpkg --print-architecture)/jre/bin/java && \
    # Install build / source control tools
    $APT_INSTALL wget git maven less vim && \
    # Install needed python packages. Use pip for installing packages (for consistency).
    $APT_INSTALL python3.8 python3-pip python3.8-venv

RUN if [ "${TARGETPLATFORM}" = "linux/amd64" ]; then \
      export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64; \
    else \
      export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64; \
      $APT_INSTALL gcc-aarch64-linux-gnu; \
      $APT_INSTALL build-essential libpython3.8-dev; \
    fi && \
    echo "export JAVA_HOME=${JAVA_HOME}" >> /etc/profile

RUN python3 -m venv /openspg_venv && \
    . /openspg_venv/bin/activate && \
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-$(dpkg --print-architecture) && \
    pip3 install openspg-kag==0.5.1 && \
    pip3 install pemja==0.4.0 && \
    pip3 install -U "http://alps-common.oss-cn-hangzhou-zmf.aliyuncs.com/nscommon/shiji/nscommon-0.0.1.tar.gz" &&\
    echo "if (tty -s); then \n   . /openspg_venv/bin/activate \nfi" >> ~/.bashrc

COPY openspg/ /openspg
RUN git clone --depth=1 https://github.com/OpenSPG/KAG.git