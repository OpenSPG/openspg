package com.antgroup.openspg.reasoner.thinker

import com.antgroup.openspg.reasoner.thinker.SimplifyDSLLexer._
import com.antgroup.openspg.reasoner.thinker.SimplifyDSLParser.OC_SymbolicNameContext

class SimplifyThinkerParser {

  def parse(simplifyDSL: String): Unit = {
    val parser = new SimplifyThinkerLexerInit().initSimplifyThinkerParser(simplifyDSL)
    val as: OC_SymbolicNameContext = parser.oC_SymbolicName()
  }

}
