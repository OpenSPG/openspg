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

LOCAL_BUILDER_JAR = "builder-runner-local-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

LOCAL_REASONER_JAR = "reasoner-local-runner-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

LOCAL_SCHEMA_URL = "http://localhost:8887"

LOCAL_GRAPH_STORE_URL = "tugraph://127.0.0.1:9090?graphName=default&timeout=50000&accessId=admin&accessKey=73@TuGraph"

LOCAL_SEARCH_ENGINE_URL = "elasticsearch://127.0.0.1:9200?scheme=http"

LOCAL_GRAPH_STATE_CLASS = (
    "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState"
)
