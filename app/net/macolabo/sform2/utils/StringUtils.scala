package net.macolabo.sform2.utils

object StringUtils {
  implicit class StringImprovements(val s: String) {
    import scala.util.control.Exception._
    def toIntOpt: Option[Int] = catching(classOf[NumberFormatException]) opt s.toInt
  }
}