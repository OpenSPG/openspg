from typing import List

from knext.client.search import SearchClient
from knext.operator.op import FuseOp
from knext.operator.spg_record import SPGRecord


class IndicatorFuse(FuseOp):

    bind_to = "Financial.Indicator"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("Financial.Indicator")

    def link(self, subject_records: List[SPGRecord]) -> List[SPGRecord]:
        print("####################IndicatorFuse#####################")
        print("IndicatorFuse(Input): ")
        print("----------------------")
        [print(r) for r in subject_records]
        linked_records = []
        for record in subject_records:
            query = {"match": {"name": record.get_property("name", "")}}
            recall_records = self.search_client.search(query, start=0, size=10)
            if recall_records is not None and len(recall_records) > 0:
                linked_records.append(SPGRecord(
                    "Financial.Indicator",
                    {
                        "id": recall_records[0].doc_id,
                        "name": recall_records[0].properties.get("name", ""),
                    },
                ))
        return linked_records

    def merge(self, subject_records: List[SPGRecord], target_records: List[SPGRecord]) -> List[SPGRecord]:
        merged_records = []
        for s in subject_records:
            if s in target_records:
                continue
            merged_records.append(s)
        print("IndicatorFuse(Output): ")
        print("----------------------")
        [print(r) for r in merged_records]
        return merged_records
