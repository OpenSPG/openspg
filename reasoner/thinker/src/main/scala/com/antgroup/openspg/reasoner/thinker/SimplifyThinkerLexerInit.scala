package com.antgroup.openspg.reasoner.thinker

import com.antgroup.openspg.reasoner.parser.ErrorHandlerStrategy
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

class SimplifyThinkerLexerInit {

  def initSimplifyThinkerParser(simplifyDSL: String): SimplifyDSLParser = {
    val cs = CharStreams.fromString(simplifyDSL)
    val lexer = new SimplifyDSLLexer(cs)
    val ts = new CommonTokenStream(lexer)
    val parser = new SimplifyDSLParser(ts)
    parser.setBuildParseTree(true)
    parser.setErrorHandler(new ErrorHandlerStrategy(simplifyDSL))
    parser
  }

}
