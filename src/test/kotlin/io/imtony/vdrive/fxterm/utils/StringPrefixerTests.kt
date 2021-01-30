package io.imtony.vdrive.fxterm.utils

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class StringPrefixerTests : DescribeSpec({
  describe("String Extension Tests") {
    it("Creates valid empty strings.") {
      val e1 = String.empty
      val e2 = String.empty
      e1 shouldBe ""
      e2 shouldBe ""
      e1 shouldBe e2
      e1 shouldBeSameInstanceAs e2
    }

    it("Should correctly handle null strings.") {
      "This".thisOrEmpty() shouldBe "This"
      (null as String?).thisOrEmpty() shouldBe ""
      val e = String.empty
      (null as String?).thisOrEmpty() shouldBeSameInstanceAs e
    }
  }

  describe("ConstStringPrefixer Tests") {
    it("Should correctly convert to and from strings") {
      val con = ConstStringPrefixer("->")
      con.toString("Tony") shouldBe "->Tony"
      con.fromString("->Tony") shouldBe "Tony"
    }
    it("Should not get fooled.") {
      val con = ConstStringPrefixer("->")
      con.fromString("Tony") shouldBe "Tony"
      con.fromString("->->->Tony") shouldBe "->->Tony"
    }
  }

  describe("PrefixedString Tests") {
    it("Should correctly prefix string properties.") {
      val pp = PrefixedString("->", "Tony")
      pp.prefix shouldBe "->"
      pp.innerValue shouldBe "Tony"
      pp.value shouldBe "->Tony"
      pp.value = "Hello"
      pp.value shouldBe "->Hello"
      pp.prefix = ">"
      pp.innerValue = "T"
      pp.value shouldBe ">T"
    }

    it("Should correctly handle binding.") {
      val pp = PrefixedString("> ", "ls")
      val b = pp.valueBinding
      b.value shouldBe "> ls"
      pp.value = "mkdir"
      b.value shouldBe "> mkdir"
      pp.prefixProperty().set("-> ")
      pp.innerValueProperty().set("rmDir")
    }
  }
})
