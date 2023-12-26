java -jar \
  ./target/builder-runner-local-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
  --query "MATCH (s:\`RiskMining.TaxOfRiskApp\`/\`赌博应用\`) RETURN s.id" \
  --output reasoner.csv \
  --schema_uri "http://localhost:8887" \
  --graph_state_class "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState" \
  --graph_state_url "tugraph://127.0.0.1:9090/default?timeout=60000&accessId=admin&accessKey=73@TuGraph"  \
  --param_map_json_str "{\"projId\": 1}"