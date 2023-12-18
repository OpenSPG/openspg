package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;

/**
 * The specific implementations of BuilderRunner include FlinkRunner, LocalRunner, and SparkRunner.
 * These runners accept pipeline configurations as well as runtime contexts, and then start the
 * builder process. It is significant to note that the runner translates the pipeline into an actual
 * physical execution plan. The physical execution plan within the core module does not encompass
 * source and sink; it solely comprises the processing of records, while the responsibility for
 * generating the source and sink is delegated to the specific runner.
 */
public interface BuilderRunner {
  /**
   * Initialization of the runner. If an initialization error occurs, an exception is thrown
   * directly without proceeding with the builder process.
   */
  void init(Pipeline pipeline, BuilderContext context) throws BuilderException;

  /**
   * Begin executing the runner, which initiates the knowledge builder process. The builder will be
   * carried out on the specific execution engine according to the definitions within the pipeline.
   */
  void execute() throws Exception;

  /** Shut down the runner and perform some resource cleanup tasks. */
  void close() throws Exception;
}
