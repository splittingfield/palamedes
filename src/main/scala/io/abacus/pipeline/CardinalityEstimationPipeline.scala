package io.abacus.pipeline

import com.twitter.algebird.{HLL, HyperLogLogMonoid}
import io.abacus.soroban.elements.Element

import scala.{specialized => spec}




class CardinalityEstimationPipeline[@spec (Int) T]()(implicit ev: Element[T]) extends Pipeline[T,T,Long] {
  val hll = new HyperLogLogMonoid(12)
  var sumHll:HLL = hll.zero
  override def results: Long = {
    val approxSize = hll.sizeOf(sumHll)
    approxSize.estimate
  }
  override def process(elem: T): T = {
    val item = hll(ev.toBytes(elem))
    sumHll = hll.plus(sumHll,item)
    elem
  }
}
