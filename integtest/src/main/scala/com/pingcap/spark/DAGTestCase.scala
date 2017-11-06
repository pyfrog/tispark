package com.pingcap.spark

import scala.collection.mutable.ArrayBuffer

class DAGTestCase(colList: List[String]) {
  val compareOpList = List("=", "<", ">", "<=", ">=", "!=", "<>")
  val LEFT_TB_NAME = "A"
  val RIGHT_TB_NAME = "B"
  val basicSelfJoin: String = s"select * from " +
    s"full_data_type_table $LEFT_TB_NAME join full_data_type_table $RIGHT_TB_NAME " +
    s"on $LEFT_TB_NAME.id_dt = $RIGHT_TB_NAME.id_dt"

  def getDAGTestCases: List[String] = {
    List(
      "select * from full_data_type_table A join full_data_type_table B on A.id_dt = B.id_dt"
    )
  }

  /**
    * We create test for each type, each operator
    *
    * @return
    */
  def createTypeTestCases: List[String] = {
    var res = ArrayBuffer.empty[String]
    for (op <- compareOpList) {
      for (tp <- colList) {
        res += basicSelfJoin + where(binaryOpWithName(tp, tp, op))
      }
    }

    res.toList
  }

  def where(condition: String): String = {
    " where " + condition
  }

  def binaryOpWithName(leftCol: String, rightCol: String, op: String): String = {
    tableColDot(LEFT_TB_NAME, leftCol) + " " + op + " " + tableColDot(RIGHT_TB_NAME, rightCol)
  }

  def tableColDot(table: String, col: String): String = {
    table + dot + col
  }

  def dot() = "."

  def limit(num: Int): String = {
    "limit " + num
  }
}

object DAGTestCase {
  def main(args: Array[String]): Unit = {
    for (str <- new DAGTestCase(List("tp1", "tp2", "tp3")).createTypeTestCases) {
      println(str)
    }
  }
}