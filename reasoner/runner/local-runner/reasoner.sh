java -jar \
  ./target/builder-runner-local-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
  --projectId 2 \
  --query "MATCH (s:`RiskMining.TaxOfRiskApp`/`赌博应用`) RETURN s.id" \
  --output ./reasoner.csv \
  --schemaUrl "http://localhost:8887" \
  --graphStateClass "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState" \
  --graphStoreUrl "tugraph://127.0.0.1:9090?graphName=default&timeout=60000&accessId=admin&accessKey=73@TuGraph"  \
