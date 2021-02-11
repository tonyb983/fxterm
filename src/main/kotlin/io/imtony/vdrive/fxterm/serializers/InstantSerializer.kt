package io.imtony.vdrive.fxterm.serializers

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * kotlinx.serialization [KSerializer] to encode and decode [Instant] values.
 */
object InstantSerializer : KSerializer<Instant> {
  private val serializer: KSerializer<Long> = Long.serializer()

  override val descriptor: SerialDescriptor = serializer.descriptor

  override fun deserialize(decoder: Decoder): Instant = Instant
    .fromEpochMilliseconds(serializer.deserialize(decoder))

  override fun serialize(encoder: Encoder, value: Instant): Unit =
    serializer.serialize(encoder, value.toEpochMilliseconds())
}
