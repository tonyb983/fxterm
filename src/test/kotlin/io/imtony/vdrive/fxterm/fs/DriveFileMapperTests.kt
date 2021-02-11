package io.imtony.vdrive.fxterm.fs

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KClass

private interface Interface {
  fun sayType(): String = "Interface"

  companion object : KFactory<Interface>() {
    override val constructors: Map<KClass<out Interface>, DefaultConstructor<out Interface>> = mapOf(
      ClassA::class to ::ClassA,
      ClassB::class to ::ClassB,
      ClassC::class to ::ClassC,
      ClassD::class to ClassD::new,
    )
  }
}

private abstract class Abstract : Interface {
  override fun sayType(): String = "Abstract"
}

private open class ClassA : Interface {
  override fun sayType(): String = "ClassA"
}

private open class ClassB : ClassA() {
  override fun sayType(): String = "ClassB"
}

private open class ClassC : Abstract() {
  override fun sayType(): String = "ClassC"
}

private class ClassD private constructor() : Interface {
  override fun sayType(): String = "ClassD"

  companion object {
    fun new(): ClassD = ClassD()
  }
}

class InstanceFactoryTests : DescribeSpec({
  describe("InstanceFactory interface tests:") {
    it("Works!") {

      val a: ClassA = Interface.newOrThrow()
      val b: ClassB = Interface.newOrThrow()
      val c: ClassC? = Interface.new()
      c.shouldNotBeNull()

      val d: ClassD? = Interface.new(ClassD::class)
      d.shouldNotBeNull()

      a.shouldBeInstanceOf<ClassA>()
      a.shouldBeInstanceOf<Interface>()

      b.shouldBeInstanceOf<ClassA>()
      b.shouldBeInstanceOf<ClassB>()
      b.shouldBeInstanceOf<Interface>()

      c.shouldBeInstanceOf<Abstract>()
      c.shouldBeInstanceOf<ClassC>()
      c.shouldBeInstanceOf<Interface>()

      d.shouldBeInstanceOf<ClassD>()
      d.shouldBeInstanceOf<Interface>()

      println("A: " + a.sayType())
      println("B: " + b.sayType())
      println("C: " + c.sayType())
      println("D: " + d.sayType())
    }
  }
})
