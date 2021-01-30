package io.imtony.vdrive.fxterm.utils

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class LockPropertyTests : DescribeSpec({
  describe("LockProperty.kt Tests") {
    it("Should create a lock property that is unlocked by default") {
      val lp = LockProperty()
      lp.shouldNotBeNull()
      lp.isLocked shouldBe false
      lp.isUnlocked shouldBe true
      lp.whenUnlocked.get() shouldBe true
      lp.whenLocked.get() shouldBe false
    }

    it("Should properly bind.") {
      val lp = lockProperty
      val binding = lp.whenLocked

      binding.get() shouldBe false
      lp.lock()
      binding.get() shouldBe true
      lp.unlock()
      binding.get() shouldBe false
    }

    it("Should toggle") {
      val lp = lockProperty

      lp.isLocked shouldBe false
      lp.toggle()
      lp.isLocked shouldBe true
    }
  }
})
