package com.antgroup.reasoner.session

import com.antgroup.openspg.reasoner.lube.logical.Var
import com.antgroup.openspg.reasoner.lube.physical.rdg.Row

class EmptyRow(orderedFields: List[Var], rdg: EmptyRDG)
    extends Row[EmptyRDG](orderedFields, rdg) {

  /**
   * Print the result, usually used for debug.
   *
   * @param rows number of rows to print
   */
  override def show(rows: Int): Unit = {}
}
