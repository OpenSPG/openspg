from typing import List, Dict

from knext.common.base.client import Client
from knext.graph_algo import (
    GetPageRankScoresRequest,
    GetPageRankScoresRequestStartNodes,
    WriterGraphRequest,
)
from knext.graph_algo import rest


class GraphAlgoClient(Client):
    """ """

    _rest_client = rest.GraphApi()

    def __init__(self, host_addr: str = None, project_id: int = None):
        super().__init__(host_addr, project_id)

    def calculate_pagerank_scores(self, target_vertex_type, start_nodes: List[Dict]):
        """
        Calculate and retrieve PageRank scores for the given starting nodes.

        Parameters:
        target_vertex_type (str): Return target vectex type ppr score
        start_nodes (list): A list containing document fragment IDs to be used as starting nodes for the PageRank algorithm.

        Returns:
        ppr_doc_scores (dict): A dictionary containing each document fragment ID and its corresponding PageRank score.

        This method uses the PageRank algorithm in the graph store to compute scores for document fragments. If `start_nodes` is empty,
        it returns an empty dictionary. Otherwise, it attempts to retrieve PageRank scores from the graph store and converts the result
        into a dictionary format where keys are document fragment IDs and values are their respective PageRank scores. Any exceptions,
        such as failures in running `run_pagerank_igraph_chunk`, are logged.
        """
        ppr_start_nodes = [
            GetPageRankScoresRequestStartNodes(id=node["name"], type=node["type"])
            for node in start_nodes
        ]
        req = GetPageRankScoresRequest(
            self._project_id, target_vertex_type, ppr_start_nodes
        )
        resp = self._rest_client.graph_get_page_rank_scores_post(
            get_page_rank_scores_request=req
        )
        return {item.id: item.score for item in resp}

    def write_graph(self, sub_graph: dict, operation: str, lead_to_builder: bool):
        request = WriterGraphRequest(
            project_id=self._project_id,
            sub_graph=sub_graph,
            operation=operation,
            enable_lead_to=lead_to_builder,
        )
        self._rest_client.graph_writer_graph_post(writer_graph_request=request)


if __name__ == "__main__":
    sc = GraphAlgoClient("http://127.0.0.1:8887", 4)
    out = sc.calculate_pagerank_scores(
        "Entity", [{"name": "Anxiety_and_nervousness", "type": "Entity"}]
    )
    for o in out:
        print(o)
