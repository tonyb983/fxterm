package io.imtony.vdrive.fxterm.serializers

import io.kotest.core.spec.style.DescribeSpec
import kotlinx.datetime.Instant

class InstantSerializerTests : DescribeSpec({
  val target = Instant.fromEpochMilliseconds(1_615_000_000_000)
  this.include(serializerTestFactory(InstantSerializer, target))
})
