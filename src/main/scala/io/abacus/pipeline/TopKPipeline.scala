package io.abacus.pipeline

import java.util.concurrent.atomic.AtomicBoolean

import com.twitter.algebird.{SpaceSaver, SpaceSaverSemigroup}
import io.abacus.soroban.elements.Element

// NOT THREADSAFE BEFORE INITIAL SETUP
class TopKPipeline[@specialized(Int) T](k: Int)(implicit ev: Element[T]) extends Pipeline[T,T,Seq[(T,Long)]] {
  private val sumaryCap = 32
  val sssm = new SpaceSaverSemigroup[T]
  val processedFirst = new AtomicBoolean(false)
  var summary: SpaceSaver[T] = _
  def process(elem: T): T = {
    if (processedFirst.get()) {
      summary = sssm.plus(summary,SpaceSaver(sumaryCap,elem))
    }
    else {
      summary = SpaceSaver(sumaryCap, elem)
      processedFirst.set(true)
    }

    elem
  }

  def results: Seq[(T, Long)] = summary.topK(k).map(v => (v._1, v._2.estimate))
}
