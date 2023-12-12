package com.antgroup.openspg.reasoner.lube

import org.slf4j.{Logger, LoggerFactory}

trait Logging {

  /**
   * A [[Logger]] named lube.
   */
  protected val logger: Logger = LoggerFactory.getLogger("lube")

}
