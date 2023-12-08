import networkx as nx
import matplotlib.pyplot as plt

from knext.api.component import SPGTypeMapping
from knext.api.component import KGSinkWriter
from knext.api.component import CsvSourceReader

if __name__ == '__main__':
    source = CsvSourceReader(
        local_path="./builder/job/data/BodyPart.csv", columns=["id"], start_row=1
    )

    mapping1 = SPGTypeMapping(spg_type_name="Medical.BodyPart").add_field(
        "id", "Medical.BodyPart.id"
    )

    mapping2 = SPGTypeMapping(spg_type_name="Medical.BodyPart").add_field(
        "id", "Medical.BodyPart.id1"
    )

    sink = KGSinkWriter()
    sink2 = KGSinkWriter()

    builder_chain = source >> [mapping1, None] >> sink2

    print(builder_chain.dag.edges)

    G = builder_chain.dag
    # 绘制图形
    nx.draw(G, with_labels=True, arrows=True)

    # 显示图形
    plt.show()
