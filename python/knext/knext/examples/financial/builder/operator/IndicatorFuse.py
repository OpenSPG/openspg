from typing import List

from knext.client.search import SearchClient
from knext.operator.op import FuseOp
from knext.operator.spg_record import SPGRecord


class IndicatorFuse(FuseOp):

    bind_to = "Financial.Indicator"

    def __init__(self):
        super().__init__()
        self.search_client = SearchClient("Financial.Indicator")

    def invoke(self, subject_records: List[SPGRecord]) -> List[SPGRecord]:
        print("####################IndicatorFuse#####################")
        print("IndicatorFuse(Input): ")
        print("----------------------")
        [print(r) for r in subject_records]
        fused_records = []
        for record in subject_records:
            query = {"match": {"name": record.get_property("name", "")}}
            recall_records = self.search_client.search(query, start=0, size=10)
            if recall_records is not None and len(recall_records) > 0:
                continue
            fused_records.append(record)
        print("IndicatorFuse(Output): ")
        print("----------------------")
        [print(r) for r in fused_records]
        return fused_records
