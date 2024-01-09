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

FROM ubuntu:20.04

# For apt to be noninteractive
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV LANG C.UTF-8

ARG TARGETPLATFORM
ARG APT_INSTALL="apt-get install --no-install-recommends -y"

ARG PIP_PKGS="openspg-knext openspg-nn4k pemja==0.4.0"

RUN apt-get clean && apt-get update && \
    # Install openjdk 8.
    $APT_INSTALL openjdk-8-jdk && \
    update-alternatives --set java /usr/lib/jvm/java-8-openjdk-`echo $TARGETPLATFORM | cut -d'/' -f2`/jre/bin/java && \
    # Install build / source control tools
    $APT_INSTALL wget git maven less vim && \
    # Install needed python packages. Use pip for installing packages (for consistency).
    $APT_INSTALL python3.8 python3-pip python3.8-venv

RUN python3 -m venv /openspg_venv && \
    . /openspg_venv/bin/activate && \
    pip3 install $PIP_PKGS && \
    echo "if (tty -s); then \n   . /openspg_venv/bin/activate \nfi" >> ~/.bashrc

ADD lib/*.jar /openspg_venv/lib/python3.8/site-packages/knext/lib

RUN git clone --depth=1 https://github.com/OpenSPG/openspg.git