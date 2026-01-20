# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

OpenSPG is a knowledge graph engine developed by Ant Group in collaboration with OpenKG, based on the SPG (Semantic-enhanced Programmable Graph) framework. It combines LPG (Labeled Property Graph) structural simplicity with RDF semantic expressiveness for industrial knowledge graph construction and reasoning.

OpenSPG Core Capabilities:
- **SPG-Schema**: Semantic modeling with schema framework for property graphs
- **SPG-Builder**: Knowledge construction from structured/unstructured data
- **SPG-Reasoner**: Logical rule reasoning with KGDSL (Knowledge Graph Domain Specific Language)
- **KNext**: Programmable framework for knowledge graph solutions
- **Cloudext**: Cloud adaptation layer for pluggable graph storage and computing engines

## Project Information

- **License**: Apache License 2.0
- **Java Version**: 17
- **Scala Version**: 2.11.12
- **Main Repository**: https://github.com/OpenSPG/OpenSPG
- **Documentation**: https://openspg.github.io/v2

## Build and Development Commands

### Building the Project

```bash
# Full build (compiles Java, Scala, and runs tests)
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Format code (Spotless for Java, Scalastyle for Scala)
mvn spotless:apply

# Check code formatting
mvn spotless:check
```

### Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=ClassName

# Run tests in a specific module
mvn test -pl module-name
```

### Local Development with Docker Compose

```bash
# Start all services (MySQL, Neo4j, MinIO, OpenSPG Server)
cd dev/release
./docker-compose.sh up

# Stop services
./docker-compose.sh down

# Alternative: Use docker-compose directly
docker-compose -f docker-compose.yml up -d
docker-compose -f docker-compose.yml down
```

**Service Ports:**
- OpenSPG Server: 8887
- Neo4j HTTP: 7474
- Neo4j Bolt: 7687
- MySQL: 3306
- MinIO API: 9000
- MinIO Console: 9001

### Running Standalone Components

```bash
# Local Builder Runner
java -jar builder/runner/local/target/builder-runner-local-*.jar

# Local Reasoner Runner
java -jar reasoner/runner/local-runner/target/reasoner-local-runner-*.jar
```

## Architecture

### Module Structure

**server/** - Main HTTP server built with SOFABoot (Alibaba's Spring Boot extension)
- Entry point: `server/arks/sofaboot/src/main/java/com/antgroup/openspg/server/arks/sofaboot/Application.java`
- `api/` - REST API layer (facade, http-client, http-server)
- `biz/` - Business logic (common, schema, service)
- `core/` - Core services (schema, scheduler, reasoner models and services)
- `infra/` - Data access layer (MyBatis DAOs)
- Configuration: `server/arks/sofaboot/src/main/resources/config/application-*.properties`

**reasoner/** - Logical rule reasoning engine (Scala-based)
- `lube-api/` - Core abstractions (block, catalog, parser, utils)
- `lube-logical/` - Logical plan layer
- `lube-physical/` - Physical plan layer
- `kgdsl-parser/` - ANTLR4-based KGDSL parser
- `runner/` - Execution infrastructure (local-runner, runner-common)
- `catalog/openspg-catalog/` - SPG catalog implementation
- `udf/` - User-defined functions
- `warehouse/` - Data warehouse implementations (cloudext-warehouse, warehouse-common)

**builder/** - Knowledge construction pipeline (Java-based)
- `core/logical/` - Logical plan nodes (LLMBasedExtractNode, RelationMappingNode, SPGTypeMappingNode)
- `core/physical/` - Physical plan and operators
- `core/reason/` - Reasoning processors
- `core/runtime/` - Runtime execution
- `core/strategy/` - Entity fusing and property linking strategies
- `model/` - Data models and interfaces
- `runner/local/` - Local execution runner
- `testdata/` - Test data utilities

**cloudext/** - Cloud adaptation layer with pluggable interfaces
- `interface/` - Abstract interfaces for graph-store, search-engine, cache, object-storage, computing-engine
- `impl/` - Implementations:
- `graph-store/` - TuGraph, Neo4j
- `search-engine/` - Elasticsearch, Neo4j
- `cache/` - Redis
- `object-storage/` - MinIO, OSS

**common/** - Shared utilities
- `util/` - Common utility classes

### Key Architectural Patterns

**Logical/Physical Plan Pattern**: Both builder and reasoner use a two-layer optimization approach. Logical plans define what to compute, physical plans define how to compute it (with optimizations).

**Cloudext Plugin System**: External dependencies are abstracted behind interfaces. To add a new graph store, implement `cloudext-interface-graph-store` and add to `impl/graph-store/`.

**Catalog Pattern**: Schema and metadata management is centralized in catalog implementations (`openspg-catalog` for reasoner).

### Technology Stack

- **Languages**: Java 17, Scala 2.11.12
- **Build**: Maven multi-module
- **Web Framework**: SOFABoot 3.17.0 (extends Spring Boot 2.7.8)
- **Data Access**: MyBatis 3.5.2, MySQL 5.1.30
- **Graph Storage**: TuGraph 1.4.1, Neo4j 4.4.7
- **Search**: Elasticsearch (via cloudext)
- **Scala Libraries**: Cats 2.0.0 (functional), Json4s 4.0.6
- **Language Processing**: ANTLR4 4.8 (KGDSL), QLExpress 3.3.2 (expressions)
- **Other**: Lombok, Guava, Jackson, Quartz (scheduling), OpenAPI/Swagger
- **Testing**: JUnit, Spock (Groovy), ScalaTest
- **Code Quality**: Spotless (Java), Scalastyle (Scala), JaCoCo (coverage)

### Entry Points

1. **Main Server**: `com.antgroup.openspg.server.arks.sofaboot.Application`
2. **Local Builder**: `com.antgroup.openspg.builder.runner.local.LocalBuilderMain`
3. **Local Reasoner**: `com.antgroup.openspg.reasoner.runner.local.LocalReasonerMain`

## Development Workflow

### Working with Modules

OpenSPG uses a multi-module Maven structure. You can work on specific modules:

```bash
# Build only the server module
mvn clean install -pl server

# Build server and its dependencies
mvn clean install -pl server -am

# Test only the reasoner module
mvn test -pl reasoner
```

### Code Style and Quality

```bash
# Check code formatting before committing
mvn spotless:check

# Auto-format code
mvn spotless:apply

# Run tests with coverage
mvn clean test jacoco:report
```

### IDE Configuration

For IntelliJ IDEA:
1. Enable annotation processing (for Lombok)
2. Install Scala plugin for Scala modules
3. Set Java 17 as project SDK
4. Import as Maven project

### Common Issues

- **Scala Version**: The project uses Scala 2.11.12 - ensure compatibility
- **Java Version**: Requires Java 17 - verify JAVA_HOME setting
- **Memory**: Builds may require increased heap space: `export MAVEN_OPTS="-Xmx4g"`

