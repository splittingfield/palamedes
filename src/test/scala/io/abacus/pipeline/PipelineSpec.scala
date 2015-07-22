package io.abacus.pipeline

import org.scalatest.{ShouldMatchers, WordSpec}

import scala.collection.mutable

class PipelineSpec extends WordSpec with ShouldMatchers {
  class StringToLength() extends Pipeline[String,Int,Int] {
    var count = 0
    def process(elem: String): Int = { count += elem.length; elem.length }
    def results: Int = count
  }
  class WordCounter() extends Pipeline[String,String,mutable.Map[String,Int]] {
    val words = mutable.HashMap.empty[String,Int].withDefaultValue(0)
    def process(elem: String): String = {
      words.put(elem,words(elem) + 1)
      elem
    }
    def results: mutable.Map[String,Int] = words
  }

  class Summer() extends Pipeline[Int,Int, Int] {
    var sum = 0
    def process(elem: Int): Int = { sum += elem; elem }
    def results: Int = sum
  }

  class WordReduce() extends Pipeline[mutable.Map[String,Int], Int,Int] {
    var acc = 0
    def process(elem: mutable.Map[String,Int]): Int = { val a = elem.map{ case (k,v) => v}.sum; acc += a; a}
    def results: Int = acc
  }

  "A simple smoke test for the Pipeline framework" should {
    "count words" in {
      val a = new WordCounter()
      a.process("a")
      a.process("b")
      a.process("b")

      a.results should be (mutable.Map("a"->1, "b"->2))
    }
    "count words and their total length" in {
      val a = new WordCounter()
      val b = a.alongWith(new StringToLength())
      b.process("a")
      b.process("b")
      b.process("b")

      b.results should be ((mutable.Map("a"->1, "b"->2),3))
    }

    "count words length and then pipe into sum with andThen" in {
      val a = new StringToLength()
      val b = a.andThen(new Summer())
      b.process("a")
      b.process("bc")
      b.results should be (3)
    }

    "pipe results with |" in {
      val a = new WordCounter()
      val b = a pipeResults new WordReduce()
      b.process("a") should be (1)
      b.process("a") should be (2)
      b.process("bc") should be (3)
      b.results should be (6)
    }
  }
}
