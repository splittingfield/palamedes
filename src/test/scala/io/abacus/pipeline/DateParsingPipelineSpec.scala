package io.abacus.pipeline

import org.joda.time.{DateTimeZone, DateTime}
import org.scalatest.{Failed, ShouldMatchers, WordSpec}

class DateParsingPipelineSpec extends WordSpec with ShouldMatchers {
  "The DateParsing Pipeline" should {
    "parse a valid ISO date time to a Some(datetime)" in {
      val datetime = "2014-08-27T14:20:03Z"
      val a = new DateParsingPipeline[String]()
      val opt = a.process(datetime)
      opt match {
        case Some(d) => assert(d.isEqual(new DateTime(2014,8,27,14,20,3, DateTimeZone.UTC)))
        case None => Failed
      }
    }
    "parse a valid ISO date time in a different time zone" in {
      val datetime = "2014-08-27T14:20:03+3:00"
      val a = new DateParsingPipeline[String]()
      val opt = a.process(datetime)
      opt match {
        case Some(d) => assert(d.isEqual(new DateTime(2014, 8, 27, 11, 20, 3, DateTimeZone.UTC)))
        case None => Failed
      }
    }
    "parse a date in the format MM/dd/YYYY" in {
      val datetime = "05/12/1982"
      val a = new DateParsingPipeline[String]()
      val opt = a.process(datetime)
      println(opt)
      opt match {
        case Some(d) => assert(d.isEqual(new DateTime(1982, 5, 12, 0, 0, 0)))
        case None => Failed
      }

    }

  }

  "The DateCardinalityPipeline" should {
    "Not explode when it only gets Nones" in {
      val a = new DateCardinalityPipeline()
      a.process(None)
      a.results should be (DateCounts(0,0,0))
    }
    "Say there was 1 year, etc when passed one item" in {
      val a = new DateCardinalityPipeline()
      a.process(Some(new DateTime(2014,8,27,11,20,3, DateTimeZone.UTC)))
      a.results should be (DateCounts(1,1,1))
    }
    "Say there was 1 year, multiple months and days, etc when passed 3 items" in {
      val a = new DateCardinalityPipeline()
      a.process(Some(new DateTime(2014,8,27,11,20,3, DateTimeZone.UTC)))
      a.process(Some(new DateTime(2014,8,23,11,20,3, DateTimeZone.UTC)))
      a.process(Some(new DateTime(2014,10,29,11,20,3, DateTimeZone.UTC)))


      a.results should be (DateCounts(1,2,3))
    }

    "Be able to be piped into with the DateParsingPipeline" in {
      val datetime = "2014-08-27T14:20:03Z"
      val a = new DateParsingPipeline[String]() andThen new DateCardinalityPipeline()
      a.process(datetime)
      a.results should be (DateCounts(1,1,1))
    }
  }

}
