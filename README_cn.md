# OpenSPG

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](./LICENSE)

[OpenSPG](https://openspg.github.io/v2) 是蚂蚁集团结合多年金融领域多元场景知识图谱构建与应用业务经验的总结，并与OpenKG联合推出的基于SPG(Semantic-enhanced Programmable Graph)框架研发的知识图谱引擎。

![OpenSPG Architecture](https://mdn.alipayobjects.com/huamei_xgb3qj/afts/img/A*DsIHS7Fe78AAAAAAAAAAAAAADtmcAQ/original)

# SPG背景介绍

SPG(Semantic-enhanced Programmable Graph)：语义增强可编程框架，是蚂蚁知识图谱平台经过多年金融领域业务的支撑，沉淀的一套基于属性图的语义表示框架。它创造性地融合了LPG结构性与RDF语义性，既克服了RDF/OWL语义复杂无法工业落地的问题，又充分继承了LPG结构简单与大数据体系兼容的优势。该框架从三个方面来定义和表示知识语义。首先，SPG明确定义了"知识"的形式化表示和可编程框架，使其可定义、可编程，机器可理解和处理。其次，SPG实现了知识层级间的兼容递进，支持工业级场景下非完备数据状态的图谱构建和持续迭代演化。最后，SPG有效衔接大数据与AI技术体系，支持对海量数据进行高效的知识化转换，帮助提高数据价值和应用价值。通过SPG框架，我们可以更加高效地构建和管理图谱数据，同时可以更好地支持业务需求和应用场景。由于SPG框架具有良好的可扩展性和灵活性，新的业务场景可以通过扩展领域知识模型及开发新算子，快速构建其领域模型和解决方案。

SPG的详细介绍请参考**蚂蚁集团和OpenKG联合发布**的[《SPG白皮书》](https://openspg.github.io/v2/blog/design_philosophy/white_paper/openspg)。

# OpenSPG介绍

OpenSPG是以SPG框架为基础设计和实现的知识图谱开放引擎，它为领域图谱构建提供了明确的语义表示、逻辑规则定义、算子框架(
构建、推理)等能力，支持各厂商可插拔的适配基础引擎、算法服务，构建自定义的解决方案。

OpenSPG核心能力模型包括：

* SPG-Schema语义建模
  * 负责属性图语义增强的Schema框架设计，如主体模型、演化模型、谓词模型等。
* SPG-Builder知识构建
  * 支持结构化和非结构化知识导入。
  * 与大数据架构兼容衔接，提供了知识构建算子框架，实现从数据到知识的转换。
  * 抽象了知识加工SDK框架，提供实体链指、概念标化和实体归一等算子能力，结合自然语言处理(Natural Language Processing, NLP)
    和深度学习算法，提高单个类型(Class)中不同实例(Instance)的唯一性水平，支持领域图谱的持续迭代演化。
* SPG-Reasoner逻辑规则推理
  * 抽象了KGDSL(Knowledge Graph Domain Specific Language)，为逻辑规则提供可编程的符号化表示。
  * 以机器可理解的符号表示支持下游规则推理、神经/符号融合学习、KG2Prompt联动LLM知识抽取/知识推理等。
  * 通过谓词语义和逻辑规则来定义知识之间的依赖和传递，并且支持对复杂的业务场景的建模和分析。
* 可编程框架KNext
  * KNext作为图谱可编程框架，提供了一套可扩展，流程化，对用户友好的组件化能力；
  * 抽象了图谱核心能力，沉淀为组件化、框架化、引擎内置的能力；
  * 实现引擎与业务逻辑、领域模型的隔离，方便业务快速定义图谱解决方案；
  * 构建以OpenSPG引擎为基础，知识驱动的可控AI技术栈，链接LLM、GraphLearning等深度学习能力。
* 云适配层Cloudext
  * 业务系统通过SDK对接开放引擎，构建自身特色的业务前端
  * 可扩展/适配自定义的图存储/图计算引擎
  * 可扩展/适配适合自身业务特点的机器学习框架

# 如何使用

## Get Started

* [安装说明](https://openspg.yuque.com/ndx6g9/docs/zxh5a5dr03945l1x)
* 通过案例快速上手:
  * [企业供应链图谱](https://openspg.yuque.com/ndx6g9/docs/dq9ptys5u5dvh0ia)
  * [黑产挖掘图谱](https://openspg.yuque.com/ndx6g9/docs/xq9fwzuqr272smno)
  * [医疗知识图谱](https://openspg.yuque.com/ndx6g9/docs/bo7b9kwhkl6zynsh)

## 进阶教程

* [OpenSPG用户手册](https://openspg.yuque.com/ndx6g9/docs)

# 如何贡献代码

* [Contribution Guidelines](https://openspg.yuque.com/ndx6g9/docs/yv8rahc3dewb1gy8#Pqcd)

# Cite

如果您使用此软件，请引用引用:
* [KAG: Boosting LLMs in Professional Domains via Knowledge Augmented Generation](https://arxiv.org/abs/2409.13731)
* KGFabric: A Scalable Knowledge Graph Warehouse for Enterprise Data Interconnection

```bibtex
@article{liang2024kag,
  title={KAG: Boosting LLMs in Professional Domains via Knowledge Augmented Generation},
  author={Liang, Lei and Sun, Mengshu and Gui, Zhengke and Zhu, Zhongshu and Jiang, Zhouyu and Zhong, Ling and Zhao, Peilong and Bo, Zhongpu and Yang, Jin and others},
  journal={arXiv preprint arXiv:2409.13731},
  year={2024}
}

@article{yikgfabric,
  title={KGFabric: A Scalable Knowledge Graph Warehouse for Enterprise Data Interconnection},
  author={Yi, Peng and Liang, Lei and Da Zhang, Yong Chen and Zhu, Jinye and Liu, Xiangyu and Tang, Kun and Chen, Jialin and Lin, Hao and Qiu, Leijie and Zhou, Jun}
}
```

# License

[Apache License 2.0](LICENSE)

# OpenSPG 核心团队

梁磊，孙梦姝，桂正科，朱仲书，江洲钰，钟玲，赵培龙，伯仲璞，阳进，熊怀东，袁琳，徐军，汪兆洋，张志强，张文，陈华钧，陈文光，周俊，王昊奋

