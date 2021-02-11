package io.imtony.vdrive.fxterm.serializers

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * kotlinx.serialization [KSerializer] to encode and decode [LocalDateTime] values.
 */
object LocalDateTimeAsStringSerializer : KSerializer<LocalDateTime> {
  private val serializer: KSerializer<String> = String.serializer()

  override val descriptor: SerialDescriptor = serializer.descriptor

  override fun deserialize(decoder: Decoder): LocalDateTime = LocalDateTime
    .parse(serializer.deserialize(decoder))

  override fun serialize(encoder: Encoder, value: LocalDateTime): Unit =
    serializer.serialize(encoder, value.toString())
}

private const val YR_INDEX = 0
private const val MON_INDEX = 1
private const val DAY_INDEX = 2
private const val HR_INDEX = 3
private const val MIN_INDEX = 4
private const val SEC_INDEX = 5
private const val NANO_INDEX = 6

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
  private val serializer: KSerializer<String> = String.serializer()

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("localDateTime") {
    element<Int>("year")        // 0 = YR_INDEX
    element<Int>("monthNumber") // 1 = MON_INDEX
    element<Int>("dayOfMonth")  // 2 = DAY_INDEX
    element<Int>("hour")        // 3 = HR_INDEX
    element<Int>("minute")      // 4 = MIN_INDEX
    element<Int>("second")      // 5 = SEC_INDEX
    element<Int>("nanosecond")  // 6 = NANO_INDEX
  }

  override fun deserialize(decoder: Decoder): LocalDateTime = decoder.decodeStructure(descriptor) {
    var y = -1
    var mon = -1
    var d = -1
    var h = -1
    var m = -1
    var s = -1
    var n = -1
    if (decodeSequentially()) { // sequential decoding protocol
      y = decodeIntElement(descriptor, YR_INDEX)
      mon = decodeIntElement(descriptor, MON_INDEX)
      d = decodeIntElement(descriptor, DAY_INDEX)
      h = decodeIntElement(descriptor, HR_INDEX)
      m = decodeIntElement(descriptor, MIN_INDEX)
      s = decodeIntElement(descriptor, SEC_INDEX)
      n = decodeIntElement(descriptor, NANO_INDEX)
    } else {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          YR_INDEX -> y = decodeIntElement(descriptor, index)
          MON_INDEX -> mon = decodeIntElement(descriptor, index)
          DAY_INDEX -> d = decodeIntElement(descriptor, index)
          HR_INDEX -> h = decodeIntElement(descriptor, index)
          MIN_INDEX -> m = decodeIntElement(descriptor, index)
          SEC_INDEX -> s = decodeIntElement(descriptor, index)
          NANO_INDEX -> n = decodeIntElement(descriptor, index)
          CompositeDecoder.DECODE_DONE -> break
          else -> error("Unexpected index: $index")
        }
      }
    }
    require(d in 0..31 && h in 0..24 && m in 0..60 && s in 0..60)

    LocalDateTime(y, mon, d, h, m, s, n)
  }

  override fun serialize(encoder: Encoder, value: LocalDateTime): Unit = encoder.encodeStructure(descriptor) {
    encodeIntElement(descriptor, YR_INDEX, value.year)
    encodeIntElement(descriptor, MON_INDEX, value.monthNumber)
    encodeIntElement(descriptor, DAY_INDEX, value.dayOfMonth)
    encodeIntElement(descriptor, HR_INDEX, value.hour)
    encodeIntElement(descriptor, MIN_INDEX, value.minute)
    encodeIntElement(descriptor, SEC_INDEX, value.second)
    encodeIntElement(descriptor, NANO_INDEX, value.nanosecond)
  }
}
