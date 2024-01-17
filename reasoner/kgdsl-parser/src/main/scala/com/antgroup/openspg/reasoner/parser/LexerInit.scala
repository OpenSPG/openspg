/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.parser

import com.antgroup.openspg.reasoner.{KGDSLLexer, KGDSLParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

/**
 * Initialize the lexer class
 */
class LexerInit {

  /**
   * get KGReasoner parser
   * @param s
   * @return
   */
  def initKGReasonerParser(s: String): KGDSLParser = {
    val cs = CharStreams.fromString(s)
    val l = new KGDSLLexer(cs)
    val ts = new CommonTokenStream(l)
    val parser = new KGDSLParser(ts)
    parser.setBuildParseTree(true)
    parser.setErrorHandler(new ErrorHandlerStrategy(s))
    parser
  }

}
