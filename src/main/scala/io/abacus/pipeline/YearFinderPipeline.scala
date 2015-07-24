package io.abacus.pipeline

import java.util.concurrent.atomic.AtomicLong

import io.abacus.soroban.elements.Element

import scala.{specialized => spec}

class YearFinderPipeline[@spec(Int) T](minYear:Int, maxYear:Int, threshold: Double)(implicit ev:Element[T])
  extends Pipeline[T,Option[Int],Boolean] {
  val possibleYearCount = new AtomicLong(0)
  val totalElements = new AtomicLong()

  def process(elem:T): Option[Int] = {
    totalElements.incrementAndGet()
    val possibleInt = ev.toInt(elem)
    possibleInt match {
      case Some(pi) => if (minYear <= pi && pi <= maxYear) possibleYearCount.incrementAndGet()
      case None =>
    }
    possibleInt
  }

  def results: Boolean = {
    possibleYearCount.get().toDouble/totalElements.get >= threshold
  }
}
