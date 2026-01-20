# OpenSPG REST API Usage Guide

This guide provides comprehensive examples for using OpenSPG via REST API, covering tenant/project management, schema definition, data ingestion, querying, and reasoning.

## Table of Contents

- [Base URL and Response Format](#base-url-and-response-format)
- [1. Tenant Management](#1-tenant-management)
- [2. Project Management](#2-project-management)
- [3. Schema Management](#3-schema-management)
- [4. Data Ingestion](#4-data-ingestion)
- [5. Graph Operations](#5-graph-operations)
- [6. Querying](#6-querying)
- [7. Search](#7-search)
- [8. Reasoning](#8-reasoning)
- [9. Builder Jobs](#9-builder-jobs)
- [10. Scheduler](#10-scheduler)
- [Complete Workflow Example](#complete-workflow-example)

---

## Base URL and Response Format

**Base URL**: `http://localhost:8887`

### Standard Response Format

All APIs return responses in one of two formats:

**Format 1 (HttpResult)**:

```json
{
  "success": true,
  "result": { ... },
  "errorCode": null,
  "errorMsg": null,
  "traceId": "xxx"
}
```

**Format 2 (ApiResponse)**:

```json
{
  "success": true,
  "data": { ... },
  "errorMsg": null,
  "traceId": "xxx"
}
```

---

## 1. Tenant Management

### Create Tenant

```bash
curl -X POST "http://localhost:8887/public/v1/tenant" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MyTenant",
    "desc": "My organization tenant"
  }'
```

**Response**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "MyTenant",
    "description": "My organization tenant"
  }
}
```

### Query Tenants

```bash
# Query all tenants
curl -X GET "http://localhost:8887/public/v1/tenant"

# Query specific tenant
curl -X GET "http://localhost:8887/public/v1/tenant?tenantId=1"
```

---

## 2. Project Management

### Create Project

A project is the main workspace for your knowledge graph. When creating a project with `autoSchema=true` (default), a set of default entity types will be created automatically.

```bash
curl -X POST "http://localhost:8887/public/v1/project" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "RiskAnalysis",
    "description": "Risk mining knowledge graph",
    "namespace": "RiskAnalysis",
    "tenantId": 1,
    "userNo": "admin_user",
    "tag": "LOCAL",
    "visibility": "PUBLIC",
    "autoSchema": true,
    "config": {
      "vectorizer": {
        "type": "openai",
        "model": "text-embedding-ada-002",
        "apiKey": "your-api-key"
      }
    }
  }'
```

**Parameters**:
- `name`: Project display name
- `namespace`: Unique namespace for schema types (min 3 characters)
- `userNo`: User account (6-20 chars, letters/numbers/underscores only)
- `tag`: `LOCAL` or `PUBLIC_NET`
- `visibility`: `PUBLIC` or `PRIVATE`
- `autoSchema`: If true, creates default entity types (Chunk, Person, Organization, etc.)
- `config.vectorizer`: Required when tag=LOCAL

**Response**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "RiskAnalysis",
    "namespace": "RiskAnalysis",
    "tenantId": 1,
    "description": "Risk mining knowledge graph",
    "tag": "LOCAL"
  }
}
```

### Query Projects

```bash
# Query all projects
curl -X GET "http://localhost:8887/public/v1/project"

# Query by tenant
curl -X GET "http://localhost:8887/public/v1/project?tenantId=1"

# Query specific project
curl -X GET "http://localhost:8887/public/v1/project?projectId=1"
```

### Update Project

```bash
curl -X POST "http://localhost:8887/public/v1/project/update" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "RiskAnalysis Updated",
    "userNo": "admin_user",
    "config": {
      "vectorizer": {
        "type": "openai",
        "model": "text-embedding-3-small"
      }
    }
  }'
```

---

## 3. Schema Management

### Query Project Schema

Get all schema types defined in a project:

```bash
curl -X GET "http://localhost:8887/public/v1/schema/queryProjectSchema?projectId=1"
```

**Response**:

```json
{
  "success": true,
  "data": {
    "projectId": 1,
    "spgTypes": [
      {
        "spgTypeEnum": "ENTITY_TYPE",
        "basicInfo": {
          "name": {
            "namespace": "RiskAnalysis",
            "name": "Person"
          },
          "nameZh": "人物",
          "desc": ""
        },
        "properties": [...],
        "relations": [...]
      }
    ]
  }
}
```

### Alter Schema (Create/Update Types)

Create custom entity types with properties and relations:

```bash
curl -X POST "http://localhost:8887/public/v1/schema/alterSchema" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "schemaDraft": {
      "alterSpgTypes": [
        {
          "spgTypeEnum": "ENTITY_TYPE",
          "alterOperation": "CREATE",
          "basicInfo": {
            "name": {
              "namespace": "RiskAnalysis",
              "name": "Company"
            },
            "nameZh": "公司",
            "desc": "Company entity type"
          },
          "properties": [
            {
              "alterOperation": "CREATE",
              "basicInfo": {
                "name": {
                  "name": "registeredCapital"
                },
                "nameZh": "注册资本",
                "desc": "Company registered capital"
              },
              "objectTypeRef": {
                "basicInfo": {
                  "name": {
                    "name": "Float"
                  }
                },
                "spgTypeEnum": "BASIC_TYPE"
              }
            },
            {
              "alterOperation": "CREATE",
              "basicInfo": {
                "name": {
                  "name": "industry"
                },
                "nameZh": "行业",
                "desc": "Industry category"
              },
              "objectTypeRef": {
                "basicInfo": {
                  "name": {
                    "name": "Text"
                  }
                },
                "spgTypeEnum": "BASIC_TYPE"
              },
              "advancedConfig": {
                "indexType": "TEXT"
              }
            },
            {
              "alterOperation": "CREATE",
              "basicInfo": {
                "name": {
                  "name": "description"
                },
                "nameZh": "描述",
                "desc": "Company description"
              },
              "objectTypeRef": {
                "basicInfo": {
                  "name": {
                    "name": "Text"
                  }
                },
                "spgTypeEnum": "BASIC_TYPE"
              },
              "advancedConfig": {
                "indexType": "TEXT_AND_VECTOR"
              }
            }
          ],
          "relations": [
            {
              "alterOperation": "CREATE",
              "basicInfo": {
                "name": {
                  "name": "locatedIn"
                },
                "nameZh": "位于",
                "desc": "Company location"
              },
              "objectTypeRef": {
                "basicInfo": {
                  "name": {
                    "namespace": "RiskAnalysis",
                    "name": "GeographicLocation"
                  }
                },
                "spgTypeEnum": "ENTITY_TYPE"
              }
            }
          ]
        }
      ]
    }
  }'
```

**Index Types**:
- `TEXT`: Full-text search index
- `VECTOR`: Vector similarity search index
- `TEXT_AND_VECTOR`: Both text and vector indexing

**Basic Property Types**:
- `Text`: String values
- `Integer`: Integer numbers
- `Float`: Floating point numbers
- `STD.Email`: Email addresses
- `STD.Phone`: Phone numbers

### Query Specific SPG Type

```bash
curl -X GET "http://localhost:8887/public/v1/schema/querySpgType?name=RiskAnalysis.Company"
```

### Query Relation

```bash
curl -X GET "http://localhost:8887/public/v1/schema/queryRelation?sName=RiskAnalysis.Company&relation=locatedIn&oName=RiskAnalysis.GeographicLocation"
```

### Query Built-in Properties

```bash
curl -X GET "http://localhost:8887/public/v1/schema/queryBuiltInProperty?spgTypeEnum=ENTITY_TYPE"
```

---

## 4. Data Ingestion

### Upsert Vertices (Entities)

Insert or update entity instances:

```bash
curl -X POST "http://localhost:8887/public/v1/graph/upsertVertex" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "vertices": [
      {
        "type": "RiskAnalysis.Company",
        "id": "company_001",
        "properties": {
          "name": "Acme Corporation",
          "registeredCapital": 10000000.0,
          "industry": "Technology",
          "description": "A leading technology company specializing in AI solutions"
        }
      },
      {
        "type": "RiskAnalysis.Company",
        "id": "company_002",
        "properties": {
          "name": "Global Trading Inc",
          "registeredCapital": 5000000.0,
          "industry": "Trading",
          "description": "International trading company"
        }
      },
      {
        "type": "RiskAnalysis.Person",
        "id": "person_001",
        "properties": {
          "name": "John Smith",
          "semanticType": "CEO"
        }
      },
      {
        "type": "RiskAnalysis.GeographicLocation",
        "id": "location_001",
        "properties": {
          "name": "New York City"
        }
      }
    ]
  }'
```

**Response**:

```json
{
  "success": true,
  "data": {
    "affectedVerticesCount": 4,
    "affectedEdgesCount": 0
  }
}
```

### Upsert Vertices with Vectors

For entities with vector-indexed properties, you can provide pre-computed vectors:

```bash
curl -X POST "http://localhost:8887/public/v1/graph/upsertVertex" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "vertices": [
      {
        "type": "RiskAnalysis.Chunk",
        "id": "chunk_001",
        "properties": {
          "content": "The company has shown strong growth in Q4 2024..."
        },
        "vectors": {
          "content": [0.1, 0.2, 0.3, ...]
        }
      }
    ]
  }'
```

### Upsert Edges (Relations)

Insert or update relationships between entities:

```bash
curl -X POST "http://localhost:8887/public/v1/graph/upsertEdge" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "upsertAdjacentVertices": false,
    "edges": [
      {
        "srcType": "RiskAnalysis.Person",
        "srcId": "person_001",
        "dstType": "RiskAnalysis.Company",
        "dstId": "company_001",
        "label": "worksAt",
        "properties": {
          "position": "CEO",
          "startDate": "2020-01-01"
        }
      },
      {
        "srcType": "RiskAnalysis.Company",
        "srcId": "company_001",
        "dstType": "RiskAnalysis.GeographicLocation",
        "dstId": "location_001",
        "label": "locatedIn",
        "properties": {}
      },
      {
        "srcType": "RiskAnalysis.Company",
        "srcId": "company_001",
        "dstType": "RiskAnalysis.Company",
        "dstId": "company_002",
        "label": "partnerOf",
        "properties": {
          "partnershipType": "Strategic"
        }
      }
    ]
  }'
```

**Parameters**:
- `upsertAdjacentVertices`: If true, automatically creates source/destination vertices if they don't exist

### Write SubGraph

Write a complete subgraph (nodes and edges) in a single operation:

```bash
curl -X POST "http://localhost:8887/public/v1/graph/writerGraph" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "operation": "UPSERT",
    "enableLeadTo": false,
    "subGraph": {
      "resultNodes": [
        {
          "id": "company_003",
          "bizId": "company_003",
          "name": "Tech Startup",
          "label": "RiskAnalysis.Company",
          "properties": {
            "registeredCapital": 1000000.0,
            "industry": "AI"
          }
        },
        {
          "id": "person_002",
          "bizId": "person_002",
          "name": "Jane Doe",
          "label": "RiskAnalysis.Person",
          "properties": {
            "semanticType": "Founder"
          }
        }
      ],
      "resultEdges": [
        {
          "from": "person_002",
          "fromId": "person_002",
          "fromType": "RiskAnalysis.Person",
          "to": "company_003",
          "toId": "company_003",
          "toType": "RiskAnalysis.Company",
          "label": "foundedBy",
          "properties": {
            "foundedYear": "2023"
          }
        }
      ]
    }
  }'
```

**Operations**:
- `UPSERT`: Insert or update
- `DELETE`: Delete nodes/edges

---

## 5. Graph Operations

### Delete Vertices

```bash
curl -X POST "http://localhost:8887/public/v1/graph/deleteVertex" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "vertices": [
      {
        "type": "RiskAnalysis.Company",
        "id": "company_003"
      }
    ]
  }'
```

### Delete Edges

```bash
curl -X POST "http://localhost:8887/public/v1/graph/deleteEdge" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "edges": [
      {
        "srcType": "RiskAnalysis.Person",
        "srcId": "person_001",
        "dstType": "RiskAnalysis.Company",
        "dstId": "company_001",
        "label": "worksAt"
      }
    ]
  }'
```

### Get All Labels

```bash
curl -X GET "http://localhost:8887/public/v1/graph/allLabels?projectId=1"
```

### Get PageRank Scores

```bash
curl -X POST "http://localhost:8887/public/v1/graph/getPageRankScores" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "spgType": "RiskAnalysis.Company",
    "topK": 10
  }'
```

---

## 6. Querying

### Query SPG Type Instances

Query entities by type and IDs:

```bash
curl -X POST "http://localhost:8887/public/v1/query/spgType" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "spgType": "RiskAnalysis.Company",
    "ids": ["company_001", "company_002"]
  }'
```

**Response**:

```json
{
  "success": true,
  "data": [
    {
      "id": "company_001",
      "type": "RiskAnalysis.Company",
      "properties": {
        "name": "Acme Corporation",
        "registeredCapital": 10000000.0,
        "industry": "Technology"
      }
    },
    {
      "id": "company_002",
      "type": "RiskAnalysis.Company",
      "properties": {
        "name": "Global Trading Inc",
        "registeredCapital": 5000000.0,
        "industry": "Trading"
      }
    }
  ]
}
```

### Sampling SPG Type

Get random samples of a specific entity type:

```bash
curl -X GET "http://localhost:8887/public/v1/sampling/spgType?projectId=1&spgType=RiskAnalysis.Company&limit=10"
```

### Sampling Relations

Get random samples of relationships:

```bash
curl -X GET "http://localhost:8887/public/v1/sampling/relation?projectId=1&sType=RiskAnalysis.Person&relation=worksAt&oType=RiskAnalysis.Company&limit=10"
```

---

## 7. Search

### Text Search

Full-text search across entities:

```bash
curl -X POST "http://localhost:8887/public/v1/search/text" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "queryString": "technology company AI",
    "labelConstraints": ["RiskAnalysis.Company"],
    "topk": 10,
    "page": 0
  }'
```

**Response**:

```json
{
  "success": true,
  "data": [
    {
      "docId": "company_001",
      "label": "RiskAnalysis.Company",
      "score": 0.95,
      "properties": {
        "name": "Acme Corporation",
        "description": "A leading technology company specializing in AI solutions"
      }
    }
  ]
}
```

### Vector Search

Semantic similarity search using vectors:

```bash
curl -X POST "http://localhost:8887/public/v1/search/vector" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "label": "RiskAnalysis.Chunk",
    "propertyKey": "content",
    "queryVector": [0.1, 0.2, 0.3, ...],
    "topk": 5,
    "efSearch": 100
  }'
```

### SPG Type Search

Search entities of a specific type:

```bash
curl -X GET "http://localhost:8887/public/v1/search/spgType?projectId=1&spgTypeName=RiskAnalysis.Company&keyword=technology&pageNo=0&pageSize=10"
```

### Custom Search

Execute custom search queries:

```bash
curl -X POST "http://localhost:8887/public/v1/search/custom" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "indexName": "RiskAnalysis.Company",
    "query": {
      "bool": {
        "must": [
          {"match": {"industry": "Technology"}}
        ],
        "filter": [
          {"range": {"registeredCapital": {"gte": 1000000}}}
        ]
      }
    },
    "topk": 10
  }'
```

---

## 8. Reasoning

### Run Reasoning Query (KGDSL)

Execute KGDSL (Knowledge Graph DSL) queries for graph traversal and reasoning:

```bash
curl -X POST "http://localhost:8887/public/v1/reason/run" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "dsl": "MATCH (p:RiskAnalysis.Person)-[r:worksAt]->(c:RiskAnalysis.Company) WHERE c.industry = $industry RETURN p, r, c",
    "params": {
      "industry": "Technology"
    }
  }'
```

**Response**:

```json
{
  "success": true,
  "data": {
    "projectId": 1,
    "task": {
      "taskId": "task_12345",
      "status": "FINISHED",
      "resultNodes": [
        {
          "id": "person_001",
          "type": "RiskAnalysis.Person",
          "properties": {"name": "John Smith"}
        },
        {
          "id": "company_001",
          "type": "RiskAnalysis.Company",
          "properties": {"name": "Acme Corporation"}
        }
      ],
      "resultEdges": [
        {
          "srcId": "person_001",
          "dstId": "company_001",
          "type": "worksAt"
        }
      ]
    }
  }
}
```

### Common KGDSL Patterns

**Find connected entities**:

```
MATCH (a:RiskAnalysis.Company)-[*1..3]-(b:RiskAnalysis.Company)
WHERE a.id = 'company_001'
RETURN b
```

**Path finding**:

```
MATCH path = (a:RiskAnalysis.Person)-[*..5]->(b:RiskAnalysis.Company)
WHERE a.id = 'person_001' AND b.id = 'company_002'
RETURN path
```

**Aggregation**:

```
MATCH (c:RiskAnalysis.Company)
WHERE c.industry = 'Technology'
RETURN count(c) as total, avg(c.registeredCapital) as avgCapital
```

### Thinker Query

Execute intelligent reasoning with the Thinker engine:

```bash
curl -X POST "http://localhost:8887/public/v1/reason/thinker" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "subject": "RiskAnalysis.Person/person_001",
    "predicate": "potentialRisk",
    "object": null,
    "mode": "INFER",
    "params": "{}"
  }'
```

**Modes**:
- `INFER`: Inference mode - deduce new relationships
- `VERIFY`: Verification mode - validate existing relationships

### Get Reasoning Schema

Get schema information for reasoning:

```bash
curl -X GET "http://localhost:8887/public/v1/reason/schema?projectId=1"
```

---

## 9. Builder Jobs

### Submit KAG Builder Job

Submit a knowledge graph building job:

```bash
curl -X POST "http://localhost:8887/public/v1/builder/kag/submit" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "userNumber": "admin_user",
    "command": "python build_kg.py --input /data/documents/ --output /data/graph/",
    "workerNum": 2,
    "workerCpu": 2.0,
    "workerMemory": 4096,
    "image": "openspg/builder:latest",
    "envs": {
      "OPENAI_API_KEY": "your-api-key"
    }
  }'
```

**Response**:

```json
{
  "success": true,
  "result": {
    "id": 1,
    "jobName": "KAG_COMMAND_admin_user_20240115_120000",
    "status": "RUNNING",
    "projectId": 1,
    "taskId": 100
  }
}
```

### Get Builder Job Status

```bash
curl -X GET "http://localhost:8887/public/v1/builder/getById?id=1"
```

### Search Builder Jobs

```bash
curl -X POST "http://localhost:8887/public/v1/builder/search" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "status": "RUNNING",
    "pageNo": 0,
    "pageSize": 20
  }'
```

### Delete Builder Job

```bash
curl -X GET "http://localhost:8887/public/v1/builder/delete?id=1"
```

---

## 10. Scheduler

### Submit Scheduler Job

```bash
curl -X POST "http://localhost:8887/public/v1/scheduler/job/submit" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "name": "Daily Data Sync",
    "lifeCycle": "PERIOD",
    "cronExpression": "0 0 2 * * ?",
    "translateType": "KAG_COMMAND_BUILDER"
  }'
```

### Execute Job Immediately

```bash
curl -X GET "http://localhost:8887/public/v1/scheduler/job/execute?id=1"
```

### Enable/Disable Job

```bash
# Enable
curl -X GET "http://localhost:8887/public/v1/scheduler/job/enable?id=1"

# Disable
curl -X GET "http://localhost:8887/public/v1/scheduler/job/disable?id=1"
```

### Get Job Details

```bash
curl -X GET "http://localhost:8887/public/v1/scheduler/job/getById?id=1"
```

### Search Jobs

```bash
curl -X POST "http://localhost:8887/public/v1/scheduler/job/search" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "status": "ENABLE",
    "pageNo": 0,
    "pageSize": 20
  }'
```

### Instance Management

```bash
# Get instance
curl -X GET "http://localhost:8887/public/v1/scheduler/instance/getById?id=1"

# Stop instance
curl -X GET "http://localhost:8887/public/v1/scheduler/instance/stop?id=1"

# Restart instance
curl -X GET "http://localhost:8887/public/v1/scheduler/instance/restart?id=1"

# Search instances
curl -X POST "http://localhost:8887/public/v1/scheduler/instance/search" \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": 1,
    "pageNo": 0,
    "pageSize": 20
  }'
```

---

## Complete Workflow Example

Here's a complete example workflow for building a risk analysis knowledge graph:

### Step 1: Create Tenant and Project

```bash
# Create tenant
curl -X POST "http://localhost:8887/public/v1/tenant" \
  -H "Content-Type: application/json" \
  -d '{"name": "RiskOrg", "desc": "Risk Analysis Organization"}'

# Create project (note: tenant ID from previous response)
curl -X POST "http://localhost:8887/public/v1/project" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "RiskMining",
    "namespace": "RiskMining",
    "tenantId": 1,
    "userNo": "risk_admin",
    "tag": "LOCAL",
    "visibility": "PUBLIC",
    "autoSchema": false,
    "config": {
      "vectorizer": {"type": "local", "model": "bge-base"}
    }
  }'
```

### Step 2: Define Custom Schema

```bash
curl -X POST "http://localhost:8887/public/v1/schema/alterSchema" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "schemaDraft": {
      "alterSpgTypes": [
        {
          "spgTypeEnum": "ENTITY_TYPE",
          "alterOperation": "CREATE",
          "basicInfo": {
            "name": {"namespace": "RiskMining", "name": "Account"},
            "nameZh": "账户"
          },
          "properties": [
            {
              "alterOperation": "CREATE",
              "basicInfo": {"name": {"name": "accountNumber"}, "nameZh": "账号"},
              "objectTypeRef": {"basicInfo": {"name": {"name": "Text"}}, "spgTypeEnum": "BASIC_TYPE"}
            },
            {
              "alterOperation": "CREATE",
              "basicInfo": {"name": {"name": "riskScore"}, "nameZh": "风险分数"},
              "objectTypeRef": {"basicInfo": {"name": {"name": "Float"}}, "spgTypeEnum": "BASIC_TYPE"}
            }
          ]
        },
        {
          "spgTypeEnum": "ENTITY_TYPE",
          "alterOperation": "CREATE",
          "basicInfo": {
            "name": {"namespace": "RiskMining", "name": "Transaction"},
            "nameZh": "交易"
          },
          "properties": [
            {
              "alterOperation": "CREATE",
              "basicInfo": {"name": {"name": "amount"}, "nameZh": "金额"},
              "objectTypeRef": {"basicInfo": {"name": {"name": "Float"}}, "spgTypeEnum": "BASIC_TYPE"}
            },
            {
              "alterOperation": "CREATE",
              "basicInfo": {"name": {"name": "timestamp"}, "nameZh": "时间"},
              "objectTypeRef": {"basicInfo": {"name": {"name": "Text"}}, "spgTypeEnum": "BASIC_TYPE"}
            }
          ],
          "relations": [
            {
              "alterOperation": "CREATE",
              "basicInfo": {"name": {"name": "fromAccount"}, "nameZh": "来自账户"},
              "objectTypeRef": {"basicInfo": {"name": {"namespace": "RiskMining", "name": "Account"}}, "spgTypeEnum": "ENTITY_TYPE"}
            },
            {
              "alterOperation": "CREATE",
              "basicInfo": {"name": {"name": "toAccount"}, "nameZh": "到账户"},
              "objectTypeRef": {"basicInfo": {"name": {"namespace": "RiskMining", "name": "Account"}}, "spgTypeEnum": "ENTITY_TYPE"}
            }
          ]
        }
      ]
    }
  }'
```

### Step 3: Ingest Data

```bash
# Insert accounts
curl -X POST "http://localhost:8887/public/v1/graph/upsertVertex" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "vertices": [
      {"type": "RiskMining.Account", "id": "acc_001", "properties": {"accountNumber": "1234567890", "riskScore": 0.2}},
      {"type": "RiskMining.Account", "id": "acc_002", "properties": {"accountNumber": "0987654321", "riskScore": 0.8}},
      {"type": "RiskMining.Account", "id": "acc_003", "properties": {"accountNumber": "1122334455", "riskScore": 0.5}}
    ]
  }'

# Insert transactions with relationships
curl -X POST "http://localhost:8887/public/v1/graph/upsertVertex" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "vertices": [
      {"type": "RiskMining.Transaction", "id": "txn_001", "properties": {"amount": 10000.0, "timestamp": "2024-01-15T10:30:00Z"}},
      {"type": "RiskMining.Transaction", "id": "txn_002", "properties": {"amount": 50000.0, "timestamp": "2024-01-15T11:45:00Z"}}
    ]
  }'

# Insert edges
curl -X POST "http://localhost:8887/public/v1/graph/upsertEdge" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "edges": [
      {"srcType": "RiskMining.Transaction", "srcId": "txn_001", "dstType": "RiskMining.Account", "dstId": "acc_001", "label": "fromAccount"},
      {"srcType": "RiskMining.Transaction", "srcId": "txn_001", "dstType": "RiskMining.Account", "dstId": "acc_002", "label": "toAccount"},
      {"srcType": "RiskMining.Transaction", "srcId": "txn_002", "dstType": "RiskMining.Account", "dstId": "acc_002", "label": "fromAccount"},
      {"srcType": "RiskMining.Transaction", "srcId": "txn_002", "dstType": "RiskMining.Account", "dstId": "acc_003", "label": "toAccount"}
    ]
  }'
```

### Step 4: Query and Analyze

```bash
# Find high-risk accounts
curl -X POST "http://localhost:8887/public/v1/reason/run" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "dsl": "MATCH (a:RiskMining.Account) WHERE a.riskScore > 0.7 RETURN a",
    "params": {}
  }'

# Find transactions involving high-risk accounts
curl -X POST "http://localhost:8887/public/v1/reason/run" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "dsl": "MATCH (t:RiskMining.Transaction)-[:fromAccount|toAccount]->(a:RiskMining.Account) WHERE a.riskScore > 0.7 RETURN t, a",
    "params": {}
  }'

# Find transaction paths between accounts
curl -X POST "http://localhost:8887/public/v1/reason/run" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "dsl": "MATCH path = (a1:RiskMining.Account)-[*..4]-(a2:RiskMining.Account) WHERE a1.id = $startAccount AND a2.id = $endAccount RETURN path",
    "params": {
      "startAccount": "acc_001",
      "endAccount": "acc_003"
    }
  }'
```

---

## Data Source Management

### Register Data Source

```bash
curl -X POST "http://localhost:8887/public/v1/datasource/insert" \
  -H "Content-Type: application/json" \
  -d '{
    "dbName": "risk_data",
    "dbUrl": "jdbc:mysql://localhost:3306/risk_data",
    "dbUser": "root",
    "dbPassword": "password",
    "type": "MYSQL",
    "createUser": "admin"
  }'
```

### Test Connection

```bash
curl -X POST "http://localhost:8887/public/v1/datasource/testConnect" \
  -H "Content-Type: application/json" \
  -d '{
    "dbUrl": "jdbc:mysql://localhost:3306/risk_data",
    "dbUser": "root",
    "dbPassword": "password",
    "type": "MYSQL"
  }'
```

### List Tables

```bash
curl -X GET "http://localhost:8887/public/v1/datasource/getAllTable?id=1&dbName=risk_data&keyword="
```

### Get Table Schema

```bash
curl -X GET "http://localhost:8887/public/v1/datasource/getTableDetail?id=1&dbName=risk_data&tableName=transactions"
```

---

## Concept Management

### Query Concepts

```bash
curl -X GET "http://localhost:8887/public/v1/concept/queryConcept?conceptTypeName=RiskMining.RiskLevel&conceptName=High"
```

### Define Dynamic Taxonomy

```bash
curl -X POST "http://localhost:8887/public/v1/concept/defineDynamicTaxonomy" \
  -H "Content-Type: application/json" \
  -d '{
    "conceptTypeName": "RiskMining.RiskLevel",
    "conceptName": "High",
    "dsl": "MATCH (a:RiskMining.Account) WHERE a.riskScore > 0.7 RETURN a"
  }'
```

### Define Logical Causation

```bash
curl -X POST "http://localhost:8887/public/v1/concept/defineLogicalCausation" \
  -H "Content-Type: application/json" \
  -d '{
    "subjectConceptTypeName": "RiskMining.Account",
    "subjectConceptName": "HighRiskAccount",
    "objectConceptTypeName": "RiskMining.Alert",
    "objectConceptName": "RiskAlert",
    "predicateName": "triggers",
    "dsl": "MATCH (a:RiskMining.Account)-[:triggers]->(alert:RiskMining.Alert) WHERE a.riskScore > 0.7 RETURN alert"
  }'
```

---

## Retrieval Management

### Get Retrievals by Project

```bash
curl -X GET "http://localhost:8887/public/v1/retrieval/getByProjectId?projectId=1"
```

### Search Retrievals

```bash
curl -X POST "http://localhost:8887/public/v1/retrieval/search" \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": 1,
    "pageNo": 0,
    "pageSize": 20
  }'
```

### Update Retrieval

```bash
curl -X POST "http://localhost:8887/public/v1/retrieval/update" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Updated Retrieval",
    "config": {}
  }'
```

---

## Error Handling

All API errors follow this format:

```json
{
  "success": false,
  "errorCode": "INVALID_PARAM",
  "errorMsg": "projectId cannot be null",
  "traceId": "trace_12345"
}
```

Common error codes:
- `INVALID_PARAM`: Invalid request parameters
- `NOT_FOUND`: Resource not found
- `UNAUTHORIZED`: Authentication required
- `PERMISSION_DENIED`: Insufficient permissions
- `INTERNAL_ERROR`: Server-side error

---

## Best Practices

1. **Batch Operations**: Use batch upsert APIs for bulk data ingestion instead of individual inserts
2. **Indexing**: Configure appropriate index types (TEXT, VECTOR, TEXT_AND_VECTOR) based on query patterns
3. **Schema Design**: Design your schema with clear entity types and relationships before data ingestion
4. **Error Handling**: Always check the `success` field in responses and handle errors appropriately
5. **Pagination**: Use pagination parameters (`page`, `pageSize`) for large result sets
6. **Namespacing**: Use consistent namespace prefixes for all entity types in a project

