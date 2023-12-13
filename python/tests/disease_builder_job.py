from knext.client.model.builder_job import BuilderJob
from knext.component.builder import (
    CsvSourceReader,
    SPGTypeMapping,
    LLMBasedExtractor,
    KGSinkWriter,
)


class Disease(BuilderJob):
    @property
    def build(self):
        """
        1. 定义输入源，CSV文件，其中CSV文件每一行为一段文本
        """
        source = CsvSourceReader(
            local_path="Disease.csv",
            columns=["content"],
            start_row=2,
        )

        """
        2. 指定SPG知识映射组件，设置抽取算子，从长文本中抽取多种实体类型
        """
        mapping = SPGTypeMapping(spg_type_name=Medical.Disease).set_operator(
            "DiseaseExtractor"
        )

        """
        3. 定义输出到图谱
        """
        sink = SinkToKgComponent()

        """
        4. 完整Pipeline定义
        """

        return source >> mapping >> sink
