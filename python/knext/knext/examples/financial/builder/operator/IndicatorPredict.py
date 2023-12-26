from typing import List

from knext.client.search import SearchClient
from knext.operator.op import PredictOp
from knext.operator.spg_record import SPGRecord


class IndicatorPredict(PredictOp):

    bind_to = ("Financial.State", "derivedFrom", "Financial.Indicator")

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("Financial.Indicator")

    def invoke(self, subject_record: SPGRecord) -> List[SPGRecord]:
        print("##########IndicatorPredict###########")
        query = {"match": {"name": subject_record.get_property("name", "")}}
        recall_records = self.search_client.search(query, start=0, size=10)
        print(recall_records)
        if recall_records is not None and len(recall_records) > 0:
            rerank_record = SPGRecord(
                "Financial.Indicator",
                {
                    "id": recall_records[0].doc_id,
                    "name": recall_records[0].properties.get("name", ""),
                },
            )
            print([rerank_record])
            return [rerank_record]
        return []
