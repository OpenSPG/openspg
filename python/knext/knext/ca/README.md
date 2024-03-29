# CA Library

The CA (Controllable Agent) library is designed to streamline the process of computation and scheduling within Python applications. It provides a modular framework that allows for the separation of computation logic from scheduling logic, as well as the ability to interface with remote Large Language Models (LLMs).

## Modules

The library is composed of several modules, each with a distinct purpose:

### Base Module
The Base Module is at the core of the CA library. It provides the essential building blocks for constructing computational workflows. It ensures that the computational logic can operate independently from the scheduling mechanism, thereby allowing developers to focus on the algorithmic aspects without worrying about underlying infrastructure.

### LLM Module
The LLM Module extends the functionality of the CA library by allowing users to invoke Large Language Models that are hosted remotely. This module provides an interface to communicate with LLM services, enabling the integration of sophisticated language processing capabilities into the user's applications.

## Service

The CA library also includes service components that facilitate the deployment and invocation of algorithms comprised of multiple modules.

- **Deployment**: This service allows users to deploy a collection of modules as a cohesive algorithm onto a desired runtime environment. The service ensures that all modules are correctly instantiated and interconnected to function as a single algorithmic unit.
  
- **Invocation**: Once deployed, the invocation service enables users to execute the algorithm by providing an interface to trigger the computational process. It handles the routing of input data to the appropriate modules and the aggregation of results for output.

## TODO

The development roadmap for the CA library includes several enhancements aimed at improving the user experience and expanding the library's capabilities:

- **Develop Automatic Deployment**: To eliminate the need for manual container and service startup, work on an automatic deployment feature will allow users to deploy their algorithms with minimal setup. This advancement would streamline the process, making the deployment of complex algorithms more user-friendly.

- **Develop LLM's Lora Deployment Capability**: To simplify the deployment of models with Lora technology, a specialized service will be created. This service would automate the deployment process, enabling users to focus on the model's functionality without worrying about the intricacies of deployment.

- **Develop Agent Capability**: An agent-based model will be introduced to drive algorithmic workflows using large models. This approach would enable more dynamic and intelligent orchestration of computational processes, potentially leading to improved efficiency and adaptability.


