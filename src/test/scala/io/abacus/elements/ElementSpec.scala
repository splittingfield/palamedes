package io.abacus.elements

import io.abacus.soroban.elements.Element.StringElement
import org.scalatest.{ShouldMatchers, WordSpec}

class ElementSpec extends WordSpec with ShouldMatchers {
  "The Element" should {
    "convert to a int when there are no commas" in {
      StringElement.toInt("123") should be (Some(123))
    }
    "convert to an int when there are commans" in {
      StringElement.toInt("1,234") should be (Some(1234))

    }
  }

}
