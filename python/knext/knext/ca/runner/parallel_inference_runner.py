import concurrent
import concurrent.futures
import json
import os
import sys
import random
from tqdm import tqdm
from knext.ca.common.base import Question
from knext.ca.common.utils import logger


class AgentRunner(object):
    def __init__(self, create_agent_fn, max_retry_times):
        self.create_agent_fn = create_agent_fn
        self.max_retry_times = max_retry_times
        self.agent = None  # self.create_agent_fn()

    def run(self, question):
        if self.agent is None:
            logger.info(f'call create_agent_fn')
            self.agent = self.create_agent_fn()

        for i in range(self.max_retry_times):
            try:
                agent_result = self.agent.solve_problem(Question(question))
                return agent_result
            except Exception as err:
                logger.warning(f'Agent solve_problem {question} fail {i}th time: {err}')
        return None


class ParallelInferenceRunner(object):
    """
    The ParallelInferenceRunner class is designed to facilitate concurrent execution of inference tasks, 
    typically useful for scenarios requiring parallel processing of multiple tasks simultaneously. 
    Upon initialization, the class allows configuration of parameters such as the number of 
    parallel processes (parallel_num) and the maximum number of retry attempts (max_retry_times) for each task.
    """
    def __init__(
            self,
            parallel_num=8,
            max_retry_times=3,
            continue_run=True,
            workspace_dir=None
    ):
        self.parallel_num = parallel_num
        self.max_retry_times = max_retry_times
        self.continue_run = continue_run
        if self.continue_run:
            self.flush_num = 12
            if workspace_dir:
                temp_result_dir = os.path.join(workspace_dir, 'temp_result')
            else:
                temp_result_dir = os.path.join(os.getcwd(), 'temp_result')
                
            os.makedirs(temp_result_dir, exist_ok=True)
            self.temp_result_file = os.path.join(temp_result_dir, f'temp_result_from_inference.jsonl')
            self.prev_temp_result_dict = {}
            if os.path.exists(self.temp_result_file):
                with open(self.temp_result_file, 'r') as f:
                    for line_data in tqdm(f, desc='loading previous result'):
                        agent_result = json.loads(line_data)
                        question = agent_result['question']
                        self.prev_temp_result_dict[question] = agent_result

    def inference_impl(self, agent_runner, question, global_context):
        return agent_runner.run(question)

    def run(self, question_list, create_agent_fn, debug_mode=False, global_context=None):
        if debug_mode:
            agent = create_agent_fn()
            question = question_list[0]
            logger.info(f'\n********Debug Start**********\nquestion: {question}')
            question_obj = Question(question, global_context=global_context)
            agent_result = agent.solve_problem(question_obj)
            logger.info(f'\n********Debug Finish**********\nagent_result: {agent_result}\n')
            return agent_result
        else:
            agent_runners = [AgentRunner(create_agent_fn, self.max_retry_times) for _ in range(self.parallel_num)]

            with concurrent.futures.ProcessPoolExecutor(max_workers=self.parallel_num) as executor:
                logger.info(f'Submitting data to agents')
                futures = []
                for q_ind, question in enumerate(question_list):
                    if self.continue_run and question in self.prev_temp_result_dict:
                        continue
                    a_ind = q_ind % self.parallel_num
                    futures.append(executor.submit(self.inference_impl, agent_runners[a_ind], question))

                logger.info(f'Agents processing data......')
                agent_results = []
                if self.continue_run:
                    temp_agent_results = []
                try:
                    for future in tqdm(concurrent.futures.as_completed(futures), total=len(futures)):
                        agent_result = future.result()
                        if agent_result:
                            agent_results.append(agent_result)
                            if self.continue_run:
                                temp_agent_results.append(agent_result)
                                if len(temp_agent_results) == self.flush_num:
                                    with open(self.temp_result_file, 'a') as f:
                                        for temp_agent_result in temp_agent_results:
                                            line_str = json.dumps(temp_agent_result)
                                            f.write(line_str + '\n')
                                    temp_agent_results = []

                except KeyboardInterrupt:
                    logger.info("Interrupted by user")
                    executor.shutdown(wait=False)
                    sys.exit(1)
                except Exception as e:
                    logger.warning(f"Error occurred: {e}")
                    executor.shutdown(wait=False)
                    sys.exit(1)

            if self.continue_run:
                for _, prev_agent_result in self.prev_temp_result_dict.items():
                    agent_results.append(prev_agent_result)
            return agent_results
