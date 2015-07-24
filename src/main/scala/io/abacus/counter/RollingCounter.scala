package io.abacus.counter

import org.cliffc.high_scale_lib.{Counter, NonBlockingHashMap}

import scala.collection.mutable.{HashMap => MHashMap}

class RollingCounter[T](buckets:Int) {
 private val data = new NonBlockingHashMap[T,Array[Counter]]()
 private val currentBucket = new Counter

  def increment(thing: T): Unit = {
    val index = currentBucket.longValue % buckets
    incrementWithBucket(thing,index.toInt)
  }

  def incrementWithBucket(thing: T, bucket: Int): Unit = {
    val value = getBuckets(thing)
    value(bucket).increment()
  }

  def count(thing: T): Long = {
    Option(data.get(thing)) match {
      case None => 0L
      case Some(array) =>
        var i = 0
        var sum = 0L
        while( i < buckets) {
          sum += array(i).estimate_get
          i = i + 1
        }
        sum
    }
  }

  def counts: Map[T,Long] = {
    val keys = data.keySet
    val output = new MHashMap[T,Long]
    val it = keys.iterator
    while (it.hasNext) {
      val thing = it.next
      output.put(thing,count(thing))
    }

    output.toMap
  }

  def advanceBucket(): Unit = {
    resetAllCountsForBucket(((currentBucket.get + 1L) % buckets).toInt)
    currentBucket.increment()
  }

  def resetAllCountsForBucket(bucket: Int): Unit = {
    val keys = data.keySet
    val it = keys.iterator
    while (it.hasNext) {
      val thing = it.next
      resetCountForBucket(thing,bucket)
    }
  }

  def resetCountForBucket(thing: T, bucket: Int): Unit = {
    val value = getBuckets(thing)
    value(bucket) = new Counter
  }

  private def getBuckets(thing: T): Array[Counter] = {
    Option(data.get(thing)) match {
      case None => initialCountsMaybe(thing)
      case Some(array) => array
    }
  }

  private def initialCountsMaybe(thing: T): Array[Counter] ={
    val array = Array.fill[Counter](buckets)(new Counter)
    Option(data.putIfAbsent(thing,array)) match {
      // This created the array, so return reference to array
      case None => array
      case Some(previous) => previous
    }
  }
}
