package com.antgroup.openspg.reasoner.parser.utils

import com.antgroup.openspg.reasoner.common.Utils
import com.antgroup.openspg.reasoner.common.table.Field
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.block.{Block, TableResultBlock}

object ParserUtils {

  def getResultTableColumns(block: Block, param: Map[String, Object]): List[Field] = {
    block match {
      case trb: TableResultBlock =>
        Utils.getResultTableColumns(
          trb.asList,
          if (Utils.getForceOutputString(param)) {
            trb.asList.map(_ => KTString)
          } else {
            List.empty
          })
      case _ => null
    }
  }

}
