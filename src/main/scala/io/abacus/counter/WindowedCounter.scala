package io.abacus.counter

import org.cliffc.high_scale_lib.NonBlockingHashMap
import org.cliffc.high_scale_lib.Counter

import scala.collection.mutable.HashMap

class WindowedCounter[T](buckets:Int) {
  val data = new NonBlockingHashMap[T,Array[Counter]]()
  val currentBucket = new Counter


  def count(thing:T) = {
    val index = currentBucket.longValue % buckets
    countInBucket(thing,index.toInt)
  }

  def advanceBucket {
    currentBucket.increment
    resetAllCountsForBucket((currentBucket.get % buckets).toInt)

  }


  def countInBucket(thing:T, bucket:Int) = {
    val value = getBuckets(thing)
    value(bucket).increment

  }


  def computeCount(thing:T):Long = {
    val array = data.get(thing)
    if(array == null) 0L
    else {
      var i = 0;
      var sum = 0L;
      while( i < buckets) {
        sum += array(i).estimate_get
        i = i+1
      }
      sum
    }

  }

  def resetAllCountsForBucket(bucket:Int) {
    val keys = data.keySet
    val it = keys.iterator
    while(it.hasNext) {
      val thing = it.next
      resetCountForBucket(thing,bucket)
    }

  }

  def resetCountForBucket(thing:T,bucket:Int) = {
    val value = getBuckets(thing)
    value(bucket) = new Counter
  }


  def aggregate:Map[T,Long] = {
     val keys = data.keySet
     val output = new HashMap[T,Long]()
     val it = keys.iterator
     while(it.hasNext) {
      val thing = it.next
      output.put(thing,computeCount(thing))
     }

     output.toMap

  }

  private def getBuckets(thing:T) = {
    val array = data.get(thing)
    if(array == null) initialCountsMaybe(thing) else array
  }

  private def initialCountsMaybe(thing:T):Array[Counter] ={
    val array = Array.fill[Counter](buckets)(new Counter)
    val previous = data.putIfAbsent(thing,array)
    if(previous == null)
      // This created the array, so return reference to array
      array
     else
      previous

  }

}
