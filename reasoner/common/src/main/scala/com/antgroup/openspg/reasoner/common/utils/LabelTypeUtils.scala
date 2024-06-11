package com.antgroup.openspg.reasoner.common.utils

object LabelTypeUtils {

  /**
   * get label meta type
   * @param sType
   * @return
   */
  def getMetaType(sType: String): String = {
    if (sType.contains("/")) {
      sType.split("/")(0)
    } else {
      sType
    }
  }

}
