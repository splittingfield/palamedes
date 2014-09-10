package io.abacus.pipeline

import java.util.concurrent.atomic.AtomicLong

import scala.{specialized => spec}


class CountingPipeline[@spec (Int) T]() extends Pipeline[T,Long,Long] {
  val count = new AtomicLong(0)
  override def results: Long = {
    count.get
  }
  override def process(elem: T): Long =  count.incrementAndGet()

}
