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
