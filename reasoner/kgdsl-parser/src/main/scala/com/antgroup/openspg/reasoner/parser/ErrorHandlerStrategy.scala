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

import com.antgroup.openspg.reasoner.common.exception.{
  InternalException,
  KGDSLGrammarException,
  KGDSLInvalidTokenException
}
import org.antlr.v4.runtime._

/**
 * Error handler
 * @param input
 */
class ErrorHandlerStrategy(input: String) extends DefaultErrorStrategy {

  override def reportError(recognizer: Parser, e: RecognitionException): Unit = {
    if (!this.inErrorRecoveryMode(recognizer)) {
      this.beginErrorCondition(recognizer)
      e match {
        case c: NoViableAltException => this.reportNoViableAlternative(recognizer, c)
        case c: InputMismatchException => this.reportInputMismatch(recognizer, c)
        case c: FailedPredicateException => this.reportFailedPredicate(recognizer, c)
        case _ =>
          throw new KGDSLGrammarException("unknown recognition error type: " + e.getClass.getName)
      }
    }
  }

  override def recover(recognizer: Parser, e: RecognitionException): Unit = {}

  override def reportNoViableAlternative(recognizer: Parser, e: NoViableAltException): Unit = {
    val tokens = recognizer.getInputStream
    val exception: InternalException =
      if (tokens != null) {
        if (e.getStartToken.getType == -1) {
          new KGDSLGrammarException("Parse Error at <EOF>")
        } else {
          val line = e.getOffendingToken.getLine
          val errorLine = input.split('\n')(line - 1)
          val col = e.getOffendingToken.getCharPositionInLine
          KGDSLInvalidTokenException(errorLine, line, col)
        }
      } else {
        new KGDSLGrammarException("<unknown input>")
      }
    throw exception
  }

  override def reportInputMismatch(recognizer: Parser, e: InputMismatchException): Unit = {
    val msg = "mismatched input " + this.getTokenErrorDisplay(e.getOffendingToken) +
      " expecting " + e.getExpectedTokens.toString(recognizer.getVocabulary)
    val tokens = recognizer.getInputStream
    val exception: InternalException =
      if (tokens != null) {
        val line = e.getOffendingToken.getLine
        val errorLine = input.split('\n')(line - 1)
        val col = e.getOffendingToken.getCharPositionInLine
        KGDSLInvalidTokenException(errorLine, line, col, msg)
      } else {
        new KGDSLGrammarException(msg)
      }
    throw exception
  }

}
