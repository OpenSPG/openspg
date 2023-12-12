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
