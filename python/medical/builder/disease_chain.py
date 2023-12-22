
from knext.component.builder import CSVReader, KGWriter, LLMBasedExtractor, SubGraphMapping, SPGTypeMapping
from nn4k.invoker import LLMInvoker
from knext.api.operator import SPOPrompt

"""
1. 定义输入源，CSV文件
"""
source = CSVReader(
    local_path="job/data/Disease.csv",
    columns=["id", "input"],
    start_row=2,
)

"""
2. 定义大模型抽取组件，从长文本中抽取Medical.Disease类型实体
"""

extract = LLMBasedExtractor(llm=LLMInvoker.from_config("openai_infer.json"),
                            prompt_ops=[SPOPrompt("Medical1.Disease", ["commonSymptom", "applicableDrug"])])


mapping = SubGraphMapping(spg_type_name="Medical1.Disease")\
    .add_mapping_field("id", "id")\
    .add_mapping_field("name", "name")\
    .add_mapping_field("commonSymptom", "commonSymptom")\
    .add_mapping_field("applicableDrug", "applicableDrug")


"""
4. 定义输出到图谱
"""
sink = KGWriter()

"""
5. 定义builder_chain
"""
builder_chain = source >> extract >> mapping >> sink

"""
5. 执行builder_chain，或发布成平台任务
"""
builder_chain.invoke()
# builder_chain.submit()
