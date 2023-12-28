from typing import List

from knext.client.search import SearchClient
from knext.operator.op import FuseOp
from knext.operator.spg_record import SPGRecord


class IndicatorFuse(FuseOp):

    bind_to = "Financial.Indicator"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("Financial.Indicator")

    def link(self, subject_record: SPGRecord) -> List[SPGRecord]:
        print("####################IndicatorFuse(指标融合)#####################")
        print("IndicatorFuse(Input): ")
        print("----------------------")
        print(subject_record)
        linked_records = []
        query = {"match": {"name": subject_record.get_property("name", "")}}
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

    def merge(self, subject_record: SPGRecord, linked_records: List[SPGRecord]) -> List[SPGRecord]:
        merged_records = []
        if not linked_records:
            merged_records.append(subject_record)
        print("IndicatorFuse(Output): ")
        print("----------------------")
        [print(r) for r in merged_records]
        return merged_records
