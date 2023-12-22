from typing import Dict, List

from knext.client.model.builder_job import BuilderJob
from knext.component.builder import CSVReader, LLMBasedExtractor, KGWriter
from knext.component.builder.mapping import SubGraphMapping, SPGTypeMapping
from knext.examples.medical.schema.medical_schema_helper import Medical
from knext.operator.builtin.auto_prompt import SPOPrompt
from knext.operator.op import PromptOp
from knext.operator.spg_record import SPGRecord
from nn4k.invoker import NNInvoker, LLMInvoker
from nn4k.invoker.openai_invoker import OpenAIInvoker


class DiseaseREPromptOp(PromptOp):

    template = """
    假设你是一个专业的医学专家，请从文本中抽取关系。我们会首先提供文本，然后会提供知识图谱schema，再提供回答的具体要求，最后是一个举例。
    ----文本----
    {re_input}
    ----知识图谱schema----
    ${schema}
    ----回答要求----
    1. 答案格式为json格式：[{"subject":,"predicate":,"object":},]
    2. object要求简洁，必须是中文，如果object包含多个值请用英文逗号分隔；
    3. 每一条关系必须属于知识图谱schema。
    ----举例----
    文本为：急性扁桃体炎通常伴有咽痛，声嘶，发热等症状。回答为：{"subject":"急性扁桃体炎","predicate":"症状","object":"咽痛,声嘶,发热"}
    """

    def build_prompt(self, record: Dict[str, str]) -> str:
        """
        record: {"input": "甲状腺结节是指在甲状腺内的肿块，可随吞咽动作随甲状腺而上下移动，是临床常见的病症......."}
        """
        return self.template.format(input=record.get("input"))

    def parse_response(self, response: str) -> List[SPGRecord]:
        """
        默认解析逻辑：
        response: [{"subject":"甲状腺结节","predicate":"发病位置","object":"甲状腺"},
                    {"subject":"急性扁桃体炎","predicate":"症状","object":"咽痛,声嘶,发热"}
                    ]
        ->

        [{"id": "甲状腺结节", "name": "甲状腺结节", "bodyPart": "甲状腺"},
        {"id": "急性扁桃体炎", "name": "急性扁桃体炎", "commonSymptom": "咽痛,声嘶,发热"}
        ]
        """

        pass


class DiseaseNERPromptOp:

    template = """
    已知实体类型(entity_type)包括:${schema}。
    假设你是一个专业的医学专家，请从下列文本中抽取所有实体(entity)。
    ----文本----
    {input}
    ----回答要求----
    1. 答案格式为：[{"entity": ,"entity_type": },]
    """

    def build_prompt(self, record: Dict[str, str]) -> str:
        """
        record: {"id": "急性扁桃体炎", "name": "急性扁桃体炎", "commonSymptom": "咽痛,声嘶,发热"， "ner_input": "咽痛,声嘶,发热", "input": "..."}
        """
        return self.template.format(input=record.get("ner_input"))

    def parse_response(self, response: str) -> List[Dict[str, str]]:
        """
        response: [{"entity": "咽痛", "entity_type": "症状"},
                {"entity": "声嘶", "entity_type": "症状"},
                {"entity": "发热", "entity_type": "症状"}
        ]
        ->

        [{"id": "咽痛", "name": "咽痛", "bodyPart": "甲状腺", "ner_input": "甲状腺"}),
        SPGRecord("spg_type_name": "Medical.Disease", "properties": {"id": "急性扁桃体炎", "name": "急性扁桃体炎", "commonSymptom": "咽痛,声嘶,发热"， "ner_input": "咽痛,声嘶,发热"})
        ]
        """

        pass


class BodyPartLinkOp:
    pass


class Disease(BuilderJob):
    def build(self):
        """
        1. 定义输入源，CSV文件，其中CSV文件每一行为一段文本
        """
        source = CSVReader(
            local_path="Disease.csv",
            columns=["content"],
            start_row=2,
        )

        spo_prompt = SPOPrompt(
            spg_type_name=Medical.Disease,
            property_names=[Medical.Disease.bodyPart, Medical.Disease.commonSymptom])
        extract = LLMBasedExtractor(llm=OpenAIInvoker.from_config("./config.json"), prompt_ops=[])

        """
        2. 指定SPG知识映射组件，设置抽取算子，从长文本中抽取多种实体类型
        """


        # mapping_schema = [
        #     {
        #         "identifier": "Medical.Disease",
        #         "property_name": "bodyPart",
        #         "link_strategy": "id_equal",
        #     },
        #     {
        #         "identifier": "Medical.Disease",
        #         "property_name": "description",
        #     }
        # ]
        #
        # mapping_config = [
        #     {
        #         "identifier": "Medical.Disease",
        #         "source": "bodyPart",
        #         "target": "bodyPart"
        #     }
        # ]


        """
        3. 定义输出到图谱
        """
        sink = KGWriter()

        """
        4. 完整Pipeline定义
        """

        return source >> mapping >> sink


d = Disease()
chain = d.build()

print(chain)

chain.invoke()
