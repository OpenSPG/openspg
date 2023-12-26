from typing import List

from knext.client.search import SearchClient
from knext.operator.op import FuseOp
from knext.operator.spg_record import SPGRecord


class StateFuse(FuseOp):

    bind_to = "Financial.State"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("Financial.State")

    def invoke(self, subject_records: List[SPGRecord]) -> List[SPGRecord]:
        print("##########StateFuse###########")
        fused_records = []
        for record in subject_records:
            query = {"match": {"name": record.get_property("name", "")}}
            recall_records = self.search_client.search(query, start=0, size=10)
            if recall_records is not None and len(recall_records) > 0:
                rerank_record = SPGRecord(
                    "Financial.State",
                    {
                        "id": recall_records[0].doc_id,
                        "name": recall_records[0].properties.get("name", ""),
                    },
                )
                fused_records.append(rerank_record)
            fused_records.append(record)
        return fused_records
