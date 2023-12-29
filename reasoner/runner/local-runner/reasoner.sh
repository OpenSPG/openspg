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
  ./target/builder-runner-local-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
  --projectId 2 \
  --query "MATCH (s:`RiskMining.TaxOfRiskApp`/`赌博应用`) RETURN s.id" \
  --output ./reasoner.csv \
  --schemaUrl "http://localhost:8887" \
  --graphStateClass "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState" \
  --graphStoreUrl "tugraph://127.0.0.1:9090?graphName=default&timeout=60000&accessId=admin&accessKey=73@TuGraph"  \
