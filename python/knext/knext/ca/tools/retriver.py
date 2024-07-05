import numpy as np


class ExtraInfoRetriver(object):
    """
    Base class for retrieving and managing extra information during retrieval processes.

    Attributes:
        extra_output_dict (dict or None): Dictionary to store additional output information.
    """

    def __init__(self):
        self.extra_output_dict = None

    def connect_extra_output_dict(self, extra_output_dict):
        self.extra_output_dict = extra_output_dict

    def fetch_info(self, query):
        pass

    def update_extra_output_dict_before_forward(self, fetch_result, question):
        pass

    def update_extra_output_dict_after_forward(self, fetch_result, question, answer):
        pass


class RagRetriver(ExtraInfoRetriver):
    """
    Subclass implementing a retrieval system with similarity search

    """
    def __init__(self, llm, embedding_fn):
        super().__init__()
        self.llm = llm
        self.embedding_fn = embedding_fn
        self.question_supported_idx_dict = {}

        
    def get_embeddings(self, entities, batch_size=32):
        num_iter = int((len(entities) + batch_size - 1) / batch_size)
        embeddings = []
        for i in range(num_iter):
            s_idx = i * batch_size
            e_idx = (i + 1) * batch_size
            entity_batch = entities[s_idx: e_idx]
            embedding_batch = self.embedding_fn.generate(entity_batch)
            embeddings.extend(embedding_batch)
        return np.array(embeddings)
    
    def create_embedding_index(self, entity_embeddings):
        import faiss
        dim = entity_embeddings.shape[1]
        index = faiss.IndexFlatL2(dim)
        index.add(entity_embeddings)
        return index

    def reset_question_supported_idx_dict(self, question):
        self.current_root_question = question
        self.question_supported_idx_dict[self.current_root_question] = []

    def store_supported_idx(self, predicted_support_idxs):
        self.question_supported_idx_dict[self.current_root_question].extend(predicted_support_idxs)

    def fetch_supported_idx_by_question(self, current_root_question):
        return list(set(self.question_supported_idx_dict[current_root_question]))
