from typing import List

from knext.client.search import SearchClient
from knext.operator.op import FuseOp
from knext.operator.spg_record import SPGRecord


class IndicatorFuse(FuseOp):

    bind_to = ("Financial.State", "derivedFrom", "Financial.Indicator")

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("Financial.Indicator")

    def invoke(self, subject_record: SPGRecord) -> List[SPGRecord]:
        query = {"match": {"name": subject_record.get_property("name", "")}}
        recall_records = self.search_client.search(query, start=0, size=10)
        if recall_records is not None and len(recall_records) > 0:
            rerank_record = SPGRecord("Financial.Indicator", {"id": recall_records[0].doc_id, "name": recall_records[0].properties.get("name", "")})
            return [rerank_record]
        return []
