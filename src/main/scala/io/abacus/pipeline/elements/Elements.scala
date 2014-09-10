package io.abacus.soroban.elements

import java.nio.ByteBuffer
import java.text.NumberFormat
import java.util.Locale
import scala.{specialized => spec}

trait Element[@spec(Int) T] {
  def toBytes(t: T):Array[Byte]
  def toInt(t: T):Option[Int]
}

object Element {

  implicit object StringElement extends Element[String] {
    def toBytes(underlying: String) = underlying.getBytes
    def toInt(underlying:String) = {
      val nh = NumberFormat.getInstance(Locale.US)
      try {
        val int = nh.parse(underlying)
        Some(int.intValue())
      } catch {
        case _:Throwable => None
      }

    }
  }

  implicit object IntElement extends Element[Int] {
    def toBytes(underlying: Int) = {
      val buf = new Array[Byte](4)
      ByteBuffer
        .wrap(buf)
        .putInt(underlying)
      buf
    }

    def toInt(t:Int) = Some(t)
  }

}
