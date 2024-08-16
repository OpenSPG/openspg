# Copyright 2023 OpenSPG Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Any, Optional, Union, Iterable, Dict, Tuple


Metadata = Dict[str, Any]
EmbeddingVector = Iterable[float]


@dataclass
class Document:
    document_id: Optional[str] = None
    text: Optional[str] = None
    metadata: Optional[Metadata] = None


class EmbeddingExtractor(ABC):
    """
    Embedding extractor extracts embedding vectors from documents and queries.
    """

    @classmethod
    @abstractmethod
    def from_config(cls, config: Dict[str, Any]) -> "EmbeddingExtractor":
        """
        Create embedding extractor from `config`.

        :param config: embedding extractor config
        :type config: Dict[str, Any]
        :return: embedding extractor instance
        :rtype: EmbeddingExtractor
        """
        message = "abstract method from_config is not implemented"
        raise NotImplementedError(message)

    @abstractmethod
    def embed_document_text(self, text: str) -> EmbeddingVector:
        """
        Embed document text as vector.

        :param text: document text to embed
        :type text: str
        :return: embedding vector of the document text
        :rtype: EmbeddingVector
        """
        message = "abstract method embed_document_text is not implemented"
        raise NotImplementedError(message)

    def embed_document_texts(self, texts: Iterable[str]) -> Iterable[EmbeddingVector]:
        """
        Embed a series of document texts as vectors.

        :param texts: document texts to embed
        :type texts: Iterable[str]
        :return: embedding vectors of the document texts
        :rtype: Iterable[EmbeddingVector]
        """
        vectors = [self.embed_document_text(text) for text in texts]
        return vectors

    def embed_document(self, document: Document) -> EmbeddingVector:
        """
        Embed document as vector.

        :param document: document to embed
        :type document: Document
        :return: embedding vector of the document
        :rtype: EmbeddingVector
        """
        if document.text is None:
            message = "document text is not initialized"
            raise ValueError(message)
        return self.embed_document_text(document.text)

    def embed_documents(
        self, documents: Iterable[Document]
    ) -> Iterable[EmbeddingVector]:
        """
        Embed a series of documents as vectors.

        :param documents: documents to embed
        :type documents: Iterable[Document]
        :return: embedding vectors of the documents
        :rtype: Iterable[EmbeddingVector]
        """
        vectors = [self.embed_document(document) for document in documents]
        return vectors

    @abstractmethod
    def embed_query_text(self, text: str) -> EmbeddingVector:
        """
        Embed query text as vector.

        :param text: query text to embed
        :type text: str
        :return: embedding vector of the query text
        :rtype: EmbeddingVector
        """
        message = "abstract method embed_query_text is not implemented"
        raise NotImplementedError(message)

    def embed_query_texts(self, texts: Iterable[str]) -> Iterable[EmbeddingVector]:
        """
        Embed a series of query texts as vectors.

        :param texts: query texts to embed
        :type texts: Iterable[str]
        :return: embedding vectors of the query texts
        :rtype: Iterable[EmbeddingVector]
        """
        vectors = [self.embed_query_text(text) for text in texts]
        return vectors


class VectorStore(ABC):
    """
    VectorStore stores a bunch of documents and supports efficient vector-based searching.
    """

    @classmethod
    @abstractmethod
    def from_config(
        cls,
        config: Dict[str, Any],
        embedding_extractor: Optional[EmbeddingExtractor] = None,
    ) -> "VectorStore":
        """
        Create vector store from `config` and optional `embedding_extractor`.

        :param config: vector store config
        :type config: Dict[str, Any]
        :param embedding_extractor: optional embedding extractor
        :type embedding_extractor: EmbeddingExtractor or None
        :return: vector store instance
        :rtype: VectorStore
        """
        message = "abstract method from_config is not implemented"
        raise NotImplementedError(message)

    @property
    @abstractmethod
    def embedding_extractor(self):
        """
        Embedding extractor of the vector store, if any.

        :return: the embedding extractor or None
        :rtype: EmbeddingExtractor or None
        """
        message = "abstract property embedding_extractor is not implemented"
        raise NotImplementedError(message)

    @abstractmethod
    def add_document_embeddings(
        self,
        documents: Iterable[Document],
        embeddings: Iterable[EmbeddingVector],
        **kwargs: Any
    ) -> Iterable[str]:
        """
        Add a series of documents and embeddings to the store.

        :param documents: documents to be added to the store
        :type documents: Iterable[Document]
        :param embeddings: document embeddings
        :type embeddings: Iterable[EmbeddingVector]
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: corresponding ids of the documents
        :rtype: Iterable[str]
        """
        message = "abstract method add_document_embeddings is not implemented"
        raise NotImplementedError(message)

    def add_documents(
        self, documents: Iterable[Document], **kwargs: Any
    ) -> Iterable[str]:
        """
        Add a series of documents to the store.

        :param documents: documents to be added to the store
        :type documents: Iterable[Document]
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: corresponding ids of the documents
        :rtype: Iterable[str]
        """
        embeddings = self.embedding_extractor.embed_documents(documents)
        document_ids = self.add_document_embeddings(documents, embeddings, **kwargs)
        return document_ids

    def add_document_texts(
        self, texts: Iterable[str], metadatas: Iterable[Metadata] = None, **kwargs: Any
    ) -> Iterable[str]:
        """
        Add a series of documents to the store given texts and metadatas of the documents.

        This is a convenience wrapper of method `add_documents`.
        Ids of the documents will be auto-generated.

        :param texts: document texts
        :type texts: Iterable[str]
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :param metadatas: document metadatas
        :type metadatas: Iterable[Metadata] or None
        """
        if metadatas is None:
            documents = [self._create_document(text) for text in texts]
        else:
            documents = [
                self._create_document(text, metadata)
                for text, metadata in zip(texts, metadatas)
            ]
        document_ids = self.add_documents(documents, **kwargs)
        return document_ids

    def _create_document(
        self, text: Optional[str] = None, metadata: Optional[Metadata] = None
    ) -> Document:
        """
        Create a document instance given text and metadata of the document.

        Id of the document will be auto-generated.

        :param text: document text
        :type text: str or None
        :param metadata: document metadata
        :type metadata: Metadata or None
        """
        import uuid

        document_id = uuid.uuid4().hex
        document = Document(document_id=document_id, text=text, metadata=metadata)
        return document

    def delete_documents(self, document_ids: Iterable[str], **kwargs: Any) -> bool:
        """
        Delete a series of documents from the store given ids of the documents.

        :param document_ids: document ids
        :type document_ids: Iterable[str]
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: True if deletion is successful, False otherwise
        :rtype: bool
        :raises NotImplementedError: if deletion is not supported
        """
        message = "deletion is not supported"
        raise NotImplementedError(message)

    @abstractmethod
    def similarity_search_by_vector(
        self, vector: EmbeddingVector, k: int = 10, **kwargs: Any
    ) -> Iterable[Tuple[Document, float]]:
        """
        Perform vector-based similarity search on the vector store,
        return top-k similar documents and scores.

        :param vector: vector to search
        :type vector: EmbeddingVector
        :param k: maximum number of similar documents to return
        :type k: int
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: documents most similar to the query and scores
        :rtype: Iterable[Tuple[Document, float]]
        """
        message = "abstract method similarity_search_by_vector is not implemented"
        raise NotImplementedError(message)

    def batch_similarity_search_by_vectors(
        self, vectors: Iterable[EmbeddingVector], k: int = 10, **kwargs: Any
    ) -> Iterable[Iterable[Tuple[Document, float]]]:
        """
        Perform vector-based batch similarity search on the vector store,
        return top-k similar documents and scores for each query.

        :param vectors: vectors to search
        :type vectors: Iterable[EmbeddingVector]
        :param k: maximum number of similar documents to return
        :type k: int
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: documents most similar to the queries and scores
        :rtype: Iterable[Iterable[Tuple[Document, float]]]
        """
        documents_and_scores = [
            self.similarity_search_by_vector(vector, k=k, **kwargs)
            for vector in vectors
        ]
        return documents_and_scores

    def similarity_search(
        self, query: str, k: int = 10, **kwargs: Any
    ) -> Iterable[Tuple[Document, float]]:
        """
        Perform vector-based similarity search on the vector store,
        return top-k similar documents and scores.

        :param query: query text to search
        :type query: str
        :param k: maximum number of similar documents to return
        :type k: int
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: documents most similar to the query and scores
        :rtype: Iterable[Tuple[Document, float]]
        """
        vector = self.embedding_extractor.embed_query_text(query)
        documents_and_scores = self.similarity_search_by_vector(vector, k=k, **kwargs)
        return documents_and_scores

    def batch_similarity_search(
        self, queries: Iterable[str], k: int = 10, **kwargs: Any
    ) -> Iterable[Iterable[Tuple[Document, float]]]:
        """
        Perform vector-based batch similarity search on the vector store,
        return top-k similar documents and scores for each query.

        :param queries: query texts to search
        :type queries: Iterable[str]
        :param k: maximum number of similar documents to return
        :type k: int
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: documents most similar to the queries and scores
        :rtype: Iterable[Iterable[Tuple[Document, float]]]
        """
        vectors = self.embedding_extractor.embed_query_texts(queries)
        documents_and_scores = self.batch_similarity_search_by_vectors(
            vectors, k=k, **kwargs
        )
        return documents_and_scores

    def similarity_search_documents(
        self, query: str, k: int = 10, **kwargs: Any
    ) -> Iterable[Document]:
        """
        Perform vector-based similarity search on the vector store,
        return top-k similar documents.

        :param query: query text to search
        :type query: str
        :param k: maximum number of similar documents to return
        :type k: int
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: documents most similar to the query
        :rtype: Iterable[Document]
        """
        documents = [
            document for document, score in self.similarity_search(query, k=k, **kwargs)
        ]
        return documents

    def batch_similarity_search_documents(
        self, queries: Iterable[str], k: int = 10, **kwargs: Any
    ) -> Iterable[Iterable[Document]]:
        """
        Perform vector-based batch similarity search on the vector store,
        return top-k similar documents for each query.

        :param queries: query texts to search
        :type queries: Iterable[str]
        :param k: maximum number of similar documents to return
        :type k: int
        :param kwargs: vector store specific keyword arguments
        :type kwargs: Any
        :return: documents most similar to the queries
        :rtype: Iterable[Iterable[Document]]
        """
        documents = [
            self.similarity_search_documents(query, k=k, **kwargs) for query in queries
        ]
        return documents
