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
# * Java 8

FROM ubuntu:20.04

# For apt to be noninteractive
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV LANG C.UTF-8

ARG TARGETPLATFORM
ARG APT_INSTALL="apt-get install --no-install-recommends -y"

RUN apt-get clean && apt-get update && \
    $APT_INSTALL git less vim && \
    # Install openjdk 8.
    $APT_INSTALL openjdk-8-jdk && \
    update-alternatives --set java /usr/lib/jvm/java-8-openjdk-`echo $TARGETPLATFORM | cut -d'/' -f2`/jre/bin/java

EXPOSE 8887

ADD target/arks-sofaboot-0.0.1-SNAPSHOT-executable.jar /
ENTRYPOINT ["java", "-jar", "arks-sofaboot-0.0.1-SNAPSHOT-executable.jar"]
