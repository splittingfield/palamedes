package io.abacus.pipeline

import org.scalatest.{ShouldMatchers, WordSpec}

class YearFinderPipelineSpec extends WordSpec with ShouldMatchers {
  "The Year Finder" should {
    "return true when the values are ints and years within range" in {
      val data = Seq(1976,1977, 1901, 1989, 2001)
      val a = new YearFinderPipeline[Int](1900, 2100,1.0)
      data.foreach(a.process(_))
      a.results should be (true)
    }

    "return false when the values are ints and years not within range with threshold of 1" in {
      val data = Seq(1976,1977, 1901, 1989, 2101)
      val a = new YearFinderPipeline[Int](1900, 2100,1.0)
      data.foreach(a.process(_))
      a.results should be (false)
    }
    "return true when the values are ints and years not within range with threshold of < 1" in {
      val data = Seq(1976,1977, 1901, 1989, 2101)
      val a = new YearFinderPipeline[Int](1900, 2100,.75)
      data.foreach(a.process(_))
      a.results should be (true)
    }

    "return true when the values are strings and years within range" in {
      val data = Seq(1976,1977, 1901, 1989, 2001).map(_.toString)
      val a = new YearFinderPipeline[String](1900, 2100,1.0)
      data.foreach(a.process(_))
      a.results should be (true)
    }

    "return false when the values are strings and years not within range with threshold of 1" in {
      val data = Seq(1976,1977, 1901, 1989, 2101).map(_.toString)
      val a = new YearFinderPipeline[String](1900, 2100,1.0)
      data.foreach(a.process(_))
      a.results should be (false)
    }
    "return true when the values are strings and years not within range with threshold of < 1" in {
      val data = Seq(1976,1977, 1901, 1989, 2101).map(_.toString)
      val a = new YearFinderPipeline[String](1900, 2100,.75)
      data.foreach(a.process(_))
      a.results should be (true)
    }

    "return true when the values are strings with commas and years within range" in {
      val data = Seq("1,976","1,977", "1,901", "1,989", "2,001")

      val a = new YearFinderPipeline[String](1900, 2100,1.0)
      data.foreach(a.process(_))
      a.results should be (true)
    }

    "return false when the values are strings with commas  and years not within range with threshold of 1" in {
      val data = Seq("1,976","1,977", "1,901", "1,989", "2,101")
      val a = new YearFinderPipeline[String](1900, 2100,1.0)
      data.foreach(a.process(_))
      a.results should be (false)
    }
    "return true when the values are strings with commans and years not within range with threshold of < 1" in {
      val data = Seq("1,976","1,977", "1,901", "1,989", "2,101")
      val a = new YearFinderPipeline[String](1900, 2100,.75)
      data.foreach(a.process(_))
      a.results should be (true)
    }


  }

}
