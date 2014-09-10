package io.abacus.pipeline

import org.scalatest.WordSpec
import org.scalatest.ShouldMatchers

class CardinalityEstimationPipelineSpec extends WordSpec with ShouldMatchers {
  "The cardinality estimator" should {
    "count unique things" in {
      val data = List(1,1,2,3,4,1,1,2,10)
      val counter = new CardinalityEstimationPipeline[Int]()
      data.foreach(counter.process(_))
      counter.results should be (5)
    }
  }

}
