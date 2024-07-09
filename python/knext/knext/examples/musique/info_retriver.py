import json
import os
import numpy as np
import pandas as pd
from concurrent.futures import ThreadPoolExecutor, as_completed

from knext.ca.logic.modules.reasoner import ExtractTriplesFromTextModule
from knext.ca.tools.retriver import RagInfoFetchTool
from knext.ca.common.utils import logger


class TextInfoRetriver(RagInfoFetchTool):
    def __init__(self, musique_dataset, llm, embedding_fn, top_k=3):
        super().__init__(llm, embedding_fn)
        self.dataset = musique_dataset
        self.top_k = top_k

    def prepare_for_question(self, question):
        # get list of paragraph
        all_paragraphs = []
        para_texts = []
        for para_idx_dict in self.dataset.get_para_idx_list_by_question(question):
            idx = para_idx_dict['idx']
            title = para_idx_dict['title']
            text = para_idx_dict['paragraph_text']
            para_text = f'{title}\n{text}'
            para_texts.append({'idx': idx, 'text': para_text})
            all_paragraphs.append(para_text)

        entity_embeddings = self.get_embeddings(all_paragraphs)
        self.index = self.create_embedding_index(entity_embeddings)
        self.para_texts = para_texts

        self.reset_question_supported_idx_dict(question)

    def fetch_info(self, query):
        query_embedding = self.get_embeddings([query]).reshape(1, -1)
        distances, indices = self.index.search(query_embedding, self.top_k)
        results = [(self.para_texts[i]['idx'], self.para_texts[i]['text']) for i in indices[0]]
        return results


class SPOInfoRetriver(RagInfoFetchTool):
    def __init__(self, musique_dataset, llm, embedding_fn):
        super().__init__(llm, embedding_fn)
        self.dataset = musique_dataset

    def get_spo_entities(self, question_dataframe):
        df = question_dataframe
        # 使用's'列进行分组，并将其他列转换为元组，然后聚合到list中
        s_grouped_df = df.groupby('s')[['s', 'p', 'o', 'text', 'context_idx']].apply(
            lambda x: x.to_dict(orient='records')).reset_index(
            name='po_pairs')
        s_grouped_df.rename(columns={"s": "entity"}, inplace=True)

        # 使用'o'列进行分组，并将其他列转换为元组，然后聚合到list中
        o_grouped_df = df.groupby('o')[['s', 'p', 'o', 'text', 'context_idx']].apply(
            lambda x: x.to_dict(orient='records')).reset_index(
            name='sp_pairs')
        o_grouped_df.rename(columns={"o": "entity"}, inplace=True)

        merged_df = pd.merge(s_grouped_df, o_grouped_df, on='entity', how='outer')
        return merged_df

    def get_question_dataframe(self, question):
        raise NotImplementedError

    def prepare_for_question(self, question):
        question_dataframe = self.get_question_dataframe(question)

        # spos of question
        self.entities_df = self.get_spo_entities(question_dataframe)

        # entities to embedding
        self.entity_embeddings = self.get_embeddings(self.entities_df['entity'].values.tolist())

        # embedding to faiss
        self.embedding_index = self.create_embedding_index(self.entity_embeddings)

        # prepare self.extra_output_dict
        self.reset_question_supported_idx_dict(question)

    def fetch_info(self, query):
        entity_embedding = np.array(self.embedding_fn.generate(query))
        # fetch or create related spos
        distances, indexs = self.embedding_index.search(entity_embedding, k=5)
        sp_or_op_df = self.entities_df.iloc[indexs[0]]

        # fetch sp or po
        def _combine_non_nan_values(row):
            row_po_pairs = row['po_pairs']
            row_sp_pairs = row['sp_pairs']
            combined_list = []
            if isinstance(row_po_pairs, list):
                combined_list.extend(row_po_pairs)
            if isinstance(row_sp_pairs, list):
                combined_list.extend(row_sp_pairs)

            filter_list = []
            for context in combined_list:
                filter_result = {}
                filter_result['spo'] = (context['s'], context['p'], context['o'])
                filter_result['para_idx'] = context['context_idx']
                filter_list.append(filter_result)
            return filter_list

        contexts_list = sp_or_op_df.apply(_combine_non_nan_values, axis=1).values

        merged_context = ""
        for contexts in contexts_list:
            for context in contexts:
                spo_str = ', '.join(context['spo'])
                para_idx = context['para_idx']
                merged_context += f"spo: {spo_str}; para_idx: {para_idx}\n"

        return merged_context


class SPOInfoRetriverWithTripleExtractor(SPOInfoRetriver):
    def __init__(self, musique_dataset, llm, embedding_fn, prompt_template_dir, intermediate_dir, save_triples=True):
        super().__init__(musique_dataset, llm, embedding_fn)
        self.tripel_extractor = ExtractTriplesFromTextModule(llm, prompt_template_dir)
        self.prompt_template_dir = prompt_template_dir
        self.intermediate_dir = intermediate_dir
        self.save_triples = save_triples
        self.para_title_triples_dict = {}
        self.intermediate_dir = intermediate_dir
        os.makedirs(self.intermediate_dir, exist_ok=True)

    def extract_triple_impl(self, idx_title_para_dict):
        triple_list = []
        text = idx_title_para_dict['paragraph_text']
        idx = idx_title_para_dict['idx']
        title = idx_title_para_dict['title']
        triples = self.tripel_extractor.forward(f'{title}\n{text}')
        for triple in triples:
            tmp_dict = {
                's': triple[0],
                'p': triple[1],
                'o': triple[2],
                'context_idx': idx,
                'text': text
            }
            triple_list.append(tmp_dict)
        return triple_list

    def get_question_dataframe(self, question):
        # check file exists
        q_hash = self.dataset.convert_question_to_hash(question)
        triple_path = os.path.join(self.intermediate_dir, f'{q_hash}.json')

        # if not, extract it
        if not os.path.exists(triple_path):
            triple_list = []
            para_list_dict = self.dataset.get_para_idx_list_by_question(question)
            with ThreadPoolExecutor() as executor:
                futures = [executor.submit(self.extract_triple_impl, idx_title_para_dict) for idx_title_para_dict in
                           para_list_dict]

            for future in as_completed(futures):
                triple_list.extend(future.result())

            # if save_triples, save it
            if self.save_triples:
                with open(triple_path, 'w') as f:
                    json.dump(triple_list, f, ensure_ascii=False, indent=4)
                    # else, load it
        else:
            with open(triple_path, 'r') as f:
                triple_list = json.load(f)
        return pd.DataFrame(triple_list)

