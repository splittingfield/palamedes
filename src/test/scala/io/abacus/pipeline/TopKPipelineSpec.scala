package io.abacus.pipeline

import org.scalatest.WordSpec
import org.scalatest.ShouldMatchers

class TopKPipelineSpec extends WordSpec with ShouldMatchers {
  "The top K pipeline" should {
    "count the top 3 things in a list of ints" in {
      val data = List(1,1,1,2,3,4,5,5,5,5,6,7,8,9,9,9,9,9,2,2,2,2,2)
      val a = new TopKPipeline[Int](3)
      data.foreach(a.process(_))
      a.results should be (Seq((2,6),(9,5),(5,4)))
    }
  }

}
