# README: Controllable Generation Agent

This README provides an overview of a Controllable Generation Agent, highlighting its components, operational flow, and how training and inference are integrated to enable a large language model (LLM) to understand domain-specific questions and utilize a knowledge graph (KG) for appropriate responses.

## Components

The agent comprises two main modules:
1. **LLM Module**: This module serves as the agent's core, leveraging a large language model to process natural language input and generate responses.
2. **KG Query Module**: This module interacts with a Knowledge Graph, enabling the agent to fetch specific information or validate facts.

## Operational Flow

The agent operates in a loop to ensure continuous interaction and feedback. Here's a simplified outline of the flow:
1. The LLM Module receives user input and determines if it is a domain-specific query.
2. If the question is domain-specific, the KG Query Module checks if the Knowledge Graph can provide an answer.
3. If the KG contains relevant information, the response is generated using this data. Otherwise, the LLM generates a response based on its training data.
4. The cycle repeats with each new input.

## Training and Inference Collaboration

Training and inference processes work together to make the agent more effective:
1. **Training**: The LLM is trained to understand the context and determine whether a question falls within a specific domain. This training helps the agent decide if the question requires querying the Knowledge Graph.
2. **Inference**: The trained LLM is used to make real-time decisions on whether to retrieve information from the Knowledge Graph or generate responses based on its own data.
