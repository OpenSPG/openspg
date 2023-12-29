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

java -jar \
  -Dcloudext.graphstore.drivers=com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphStoreClientDriver \
  -Dcloudext.searchengine.drivers=com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.ElasticSearchEngineClientDriver \
  ./target/builder-runner-local-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
  --projectId 2 \
  --jobName "TaxOfRiskApp" \
  --pipeline "{\"nodes\":[{\"id\":\"1\",\"name\":\"csv\",\"nodeConfig\":{\"@type\":\"CSV_SOURCE\",\"startRow\":2,\"url\":\"./src/test/resources/TaxOfRiskApp.csv\",\"columns\":[\"id\"],\"type\":\"CSV_SOURCE\"}},{\"id\":\"2\",\"name\":\"mapping\",\"nodeConfig\":{\"@type\":\"SPG_TYPE_MAPPING\",\"spgType\":\"RiskMining.TaxOfRiskUser\",\"mappingFilters\":[],\"mappingConfigs\":[],\"type\":\"SPG_TYPE_MAPPING\"}},{\"id\":\"3\",\"name\":\"sink\",\"nodeConfig\":{\"@type\":\"GRAPH_SINK\",\"type\":\"GRAPH_SINK\"}}],\"edges\":[{\"from\":\"1\",\"to\":\"2\"},{\"from\":\"2\",\"to\":\"3\"}]}" \
  --pythonExec "/usr/local/bin/python3.9" \
  --pythonPaths "/usr/local/lib/python3.9/site-packages;./python" \
  --schemaUrl "http://localhost:8887" \
  --parallelism "1" \
  --alterOperation "UPSERT" \
  --logFile TaxOfRiskApp.log
