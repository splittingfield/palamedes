package io.abacus.soroban.elements

import java.nio.ByteBuffer
import java.text.NumberFormat
import java.util.Locale

trait Element[@specialized(Int) T] {
  def toBytes(t: T): Array[Byte]
  def toInt(t: T): Option[Int]
}

object Element {
  implicit object StringElement extends Element[String] {
    def toBytes(underlying: String): Array[Byte] = underlying.getBytes
    def toInt(underlying:String): Option[Int] = {
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
    private val bufLen = 4
    def toBytes(underlying: Int): Array[Byte] = {
      val buf = new Array[Byte](bufLen)
      ByteBuffer
        .wrap(buf)
        .putInt(underlying)
      buf
    }

    def toInt(t: Int): Option[Int] = Some(t)
  }
}
