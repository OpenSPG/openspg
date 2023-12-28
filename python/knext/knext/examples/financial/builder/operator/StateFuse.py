from typing import List

from knext.client.search import SearchClient
from knext.operator.op import FuseOp
from knext.operator.spg_record import SPGRecord


class StateFuse(FuseOp):

    bind_to = "Financial.State"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("Financial.State")

    def link(self, subject_records: List[SPGRecord]) -> List[SPGRecord]:
        print("####################StateFuse(状态融合)#####################")
        print("StateFuse(Input): ")
        print("----------------------")
        [print(r) for r in subject_records]
        linked_records = []
        for record in subject_records:
            query = {"match": {"name": record.get_property("name", "")}}
            recall_records = self.search_client.search(query, start=0, size=10)
            if recall_records is not None and len(recall_records) > 0:
                linked_records.append(SPGRecord(
                    "Financial.State",
                    {
                        "id": recall_records[0].doc_id,
                        "name": recall_records[0].properties.get("name", ""),
                    },
                )
                )
        return linked_records

    def merge(self, subject_records: List[SPGRecord], target_records: List[SPGRecord]) -> List[SPGRecord]:
        merged_records = []
        for s in subject_records:
            # for t in target_records:
            merged_records.append(SPGRecord(
                "Financial.State",
                {
                    "id": s.get_property("id"),
                    "name": s.get_property("name", ""),
                })
            )
        print("StateFuse(Output): ")
        print("----------------------")
        [print(r) for r in merged_records]
        return merged_records
