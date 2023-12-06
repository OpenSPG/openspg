package com.antgroup.openspg.reasoner.common.utils

object ResourceLoader {
  def loadResourceFile(fileName: String): String = {
    val stream = getClass.getClassLoader.getResourceAsStream(fileName)
    val content = scala.io.Source.fromInputStream(stream).mkString
    content
  }
}
