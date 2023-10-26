# OpenSPG

[中文版文档](./README_cn.md)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](./LICENSE)


OpenSPG is a knowledge graph engine developed by Ant Group in collaboration with OpenKG, based on the SPG (Semantic-enhanced Programmable Graph) framework, which is a summary of Ant Group's years of experience in constructing and applying diverse domain knowledge graphs in the financial sector.


![OpenSPG Architecture](https://mdn.alipayobjects.com/huamei_xgb3qj/afts/img/A*DmdvRKo5pyYAAAAAAAAAAAAADtmcAQ/original)

# SPG Background

SPG (Semantic-enhanced Programmable Graph): semantic-enhanced programmable framework is a set of semantic representation framework based on attribute graph precipitated by Ant Knowledge Graph platform after years of supporting business in the financial field. It creatively integrates LPG structural and RDF semantic, which overcomes the problem that RDF/OWL semantic complexity cannot be industrially landed, and fully inherits the advantages of LPG structural simplicity and compatibility with big data system. The framework defines and represents knowledge semantics from three aspects. First, SPG explicitly defines the formal representation and programmable framework of "knowledge", so that it can be defined, programmed, understood and processed by machines. Secondly, SPG achieves compatibility and progressive advancement between knowledge levels, supporting the construction of knowledge graphs and the continuous iterative evolution of incomplete data states in industrial-level scenarios. Finally, SPG serves as an effective bridge between big data and AI technology systems, facilitating the efficient transformation of massive data into knowledge-based insights. By doing so, it enhances the value and application potential of the data. With the SPG framework, we can construct and manage graph data more efficiently, and at the same time, we can better support business requirements and application scenarios. Since SPG framework has good scalability and flexibility, new business scenarios can quickly build their domain models and solutions by extending the domain knowledge model and developing new
operators.

For a detailed introduction to SPG, please refer to **the [《SPG White Paper》](https://spg.openkg.cn/ "SPG White Paper") jointly released by Ant Group and OpenKG**.


# OpenSPG

OpenSPG is an open engine for knowledge graph designed and implemented on the basis of SPG framework, which provides explicit semantic representations, logical rule definitions, operator frameworks (construction, inference) and other capabilities for the domain knowledge graphs, and supports pluggable adaptation of basic engines and algorithmic services by various vendors to build customized solutions.

OpenSPG Core Capabilities:

* SPG-Schema semantic modeling
    * Schema framework responsible for semantic enhancement of attribute graphs, such as subject models, evolutionary models, predicate models, etc.
* SPG-Builder knowledge construction
    * Supports the construction of both structured and unstructured knowledge.
    * Compatible and articulated with big data architecture, provides a knowledge construction operator framework to realize the conversion from data to knowledge.
    * Abstracts the knowledge processing SDK framework, provides the ability of entity linking, concept standardization and entity normalization operators, combines Natural Language Processing (NLP) and deep learning algorithms, improves the uniqueness level of different instances within a single type. Furthermore, it supports the continuous iterative evolution of the domain knowledge graphs.
* SPG-Reasoner logical rule reasoning
    * Abstracts KGDSL (Knowledge Graph Domain Specific Language) to provide programmable symbolic representation of logic rules.
    * Supports downstream tasks, such as rule inference, neural/symbolic fusion learning, KG2Prompt linked LLM knowledge extraction/knowledge reasoning, represented in machine-understandable symbolic form.
    * Define dependency and transfer between knowledge through predicate semantics and logic rules, and support modeling and analysis of complex business scenarios.
* Extensible SDK framework
    * Business systems build their own characteristic business front-end by interfacing with open SDKs
    * Extensible/adaptable customized graph storage/graph calculation engine
    * Extensible/adaptable machine learning framework suitable for their own business characteristics

# How to use

## Get Started

* [Install OpenSPG](https://spg.openkg.cn/en-US/quick-start/install)
* Quick start with examples:
    * [Enterprise Supply Chain Knowledge Graph](https://spg.openkg.cn/en-US/example/enterprise-supply-chain)
    * [Risk Mining Knowledge Graph](https://spg.openkg.cn/en-US/example/risk-mining)
    * [Medical Knowledge Graph](https://spg.openkg.cn/en-US/example/medical)

## Advanced tutorials

* [KNext command tool and SDK Tutorial](https://spg.openkg.cn/en-US/tutorial/knext)
* [SPG-Schema tutorial](https://spg.openkg.cn/en-US/tutorial/spgschema)
* [SPG-Reasoner Tutorial](https://spg.openkg.cn/en-US/tutorial/spgreasoner)
* [Adapting a new graph database tutorial](https://spg.openkg.cn/en-US/tutorial/spg2lpg)

# How to contribute

* [Contribution Guidelines](https://spg.openkg.cn/en-US/quick-start/contribution)

# Cite

# License

[Apache License 2.0](LICENSE)
