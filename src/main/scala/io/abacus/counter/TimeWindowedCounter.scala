package io.abacus.counter
import java.util.concurrent.{Executors, TimeUnit}

// window is size of the window in milliseconds and
// granularity is the size of each interval in milliseconds
// window/granularity should be even, if not, we round up
class TimeWindowedCounter[T](window: Long, granularity: Long) {
  private val buckets = window/granularity
  private val counter = new RollingCounter[T](buckets.toInt)
  private val scheduledThreadPool = Executors.newScheduledThreadPool(1)

  scheduledThreadPool.scheduleWithFixedDelay(HeartBeat(counter), granularity, granularity, TimeUnit.MILLISECONDS)

  def increment(thing: T): Unit = counter.increment(thing)
  def counts: Map[T,Long] = counter.counts

  def stop: Map[T,Long] = {
    scheduledThreadPool.shutdown()
    scheduledThreadPool.awaitTermination(granularity, TimeUnit.MILLISECONDS)
    counts
  }

  case class HeartBeat(counter: RollingCounter[T]) extends Runnable {
    def run(): Unit = {
      counter.advanceBucket()
    }
  }
}
