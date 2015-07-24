package io.abacus.pipeline

import com.rojoma.json.v3.util.AutomaticJsonCodecBuilder
import com.twitter.algebird.{HLL, HyperLogLogMonoid}
import io.abacus.soroban.elements.Element
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat}

class DateParsingPipeline[T]()(implicit ev: Element[T]) extends Transformer[T,Option[DateTime]] {
  import io.abacus.pipeline.DateParsingPipeline._ // scalastyle:ignore import.grouping
  override def process(elem: T): Option[DateTime] = {
    try {
      val date = isoFmt.parseDateTime(elem.toString)
      Some(date)
    }
    catch {
      case e:Exception =>
        try {
          val date = humanFmt.parseLocalDate(elem.toString).toDateTimeAtStartOfDay
          Some(date)
        } catch {
          case e: Exception => None
        }
    }
  }
}

object DateParsingPipeline {
  val isoFmt = ISODateTimeFormat.dateTimeNoMillis()
  val humanFmt = DateTimeFormat.forPattern("MM/dd/yyyy")
}

case class DateCounts(years: Long, yearMonths: Long, yearMonthDays: Long)
object DateCounts {
  implicit val codec = AutomaticJsonCodecBuilder[DateCounts]
}

class DateCardinalityPipeline() extends Pipeline[Option[DateTime], Option[DateTime], DateCounts ] {
  import io.abacus.pipeline.DateCardinalityPipeline._ // scalastyle:ignore import.grouping
  private val hllbits = 12
  val hllYear = new HyperLogLogMonoid(hllbits)
  val hllYearMonth = new HyperLogLogMonoid(hllbits)
  val hllYearMonthDay = new HyperLogLogMonoid(hllbits)
  var sumHllyear: HLL = hllYear.zero
  var sumHllyearMonth: HLL = hllYearMonth.zero
  var sumHllyearMonthDay: HLL = hllYearMonthDay.zero

  override def results: DateCounts = {
    DateCounts(hllYear.sizeOf(sumHllyear).estimate,
      hllYearMonth.sizeOf(sumHllyearMonth).estimate,
      hllYearMonthDay.sizeOf(sumHllyearMonthDay).estimate)
  }

  override def process(elem: Option[DateTime]): Option[DateTime] = {
    elem match {
      case Some(dt) =>
        val year = yearFmt.print(dt)
        val yearMonth = yearMonthFmt.print(dt)
        val yearMonthDay = yearMonthDayFmt.print(dt)
        val itemYear = hllYear(year.getBytes)
        val itemYearMonth = hllYearMonth(yearMonth.getBytes)
        val itemYearMonthDay = hllYearMonthDay(yearMonthDay.getBytes)
        sumHllyear = hllYear.plus(sumHllyear, itemYear)
        sumHllyearMonth = hllYearMonth.plus(sumHllyearMonth, itemYearMonth)
        sumHllyearMonthDay = hllYearMonthDay.plus(sumHllyearMonthDay, itemYearMonthDay)
      case None =>
    }
    elem
  }
}

object DateCardinalityPipeline {
  val yearFmt = ISODateTimeFormat.year()
  val yearMonthFmt = ISODateTimeFormat.yearMonth()
  val yearMonthDayFmt = ISODateTimeFormat.yearMonthDay()
}
