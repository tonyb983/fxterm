package io.imtony.vdrive.fxterm.serializers

import io.kotest.core.spec.style.DescribeSpec
import kotlinx.datetime.LocalDateTime

class LocalDateTimeSerializerTests : DescribeSpec({
  val time = LocalDateTime(2021, 2, 3, 19, 57, 20, 100)
  this.include(serializerTestFactory(LocalDateTimeSerializer, time))
  this.include(serializerTestFactory(LocalDateTimeAsStringSerializer, time))
})
