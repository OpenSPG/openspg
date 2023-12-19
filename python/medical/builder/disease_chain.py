# from knext.operator.builtin.auto_prompt import SPOPrompt
#
# spo_prompt = SPOPrompt(
#     spg_type_name="Medical.Disease",
#     property_names=["diseaseSite", "commonSymptom"],custom_prompt="${schema}, ${input}")
#
# print(spo_prompt.to_rest())
from knext.component.builder import CsvSourceReader, UserDefinedExtractor, KGSinkWriter, SPGTypeMapping, \
    LLMBasedExtractor
from knext.operator.base import BaseOp
from nn4k.invoker import LLMInvoker

source = CsvSourceReader(
    local_path="job/data/Disease.csv",
    columns=["id", "content"],
    start_row=2,
)

"""
2. 指定SPG知识映射组件，设置抽取算子，从长文本中抽取多种实体类型
"""


# from operator.disease_extractor import DiseaseExtractor
# extract = UserDefinedExtractor(output_fields=["id", "name"], extract_op=BaseOp.by_name('DiseaseExtractor')({"config": "1"}))
from knext.operator.builtin.auto_prompt import SPOPrompt
extract = LLMBasedExtractor(output_fields=["id", "name"],
                            llm=LLMInvoker.from_config("openai_infer.json"),
                            prompt_ops=[SPOPrompt("Medical1.Disease", ["commonSymptom", "applicableDrug"])])

mapping = SPGTypeMapping(spg_type_name="Medical1.Disease").add_field("id", "id").add_field("name", "name")

"""
3. 定义输出到图谱
"""
sink = KGSinkWriter()

builder_chain = source >> extract >> mapping >> sink

print(builder_chain.invoke())

