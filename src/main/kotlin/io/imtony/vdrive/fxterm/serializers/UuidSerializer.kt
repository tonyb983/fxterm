package io.imtony.vdrive.fxterm.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * kotlinx.serialization [KSerializer] to encode and decode [UUID] values.
 */
object UuidSerializer : KSerializer<UUID> {
  private val serializer: KSerializer<String> = String.serializer()

  override val descriptor: SerialDescriptor = serializer.descriptor

  override fun deserialize(decoder: Decoder): UUID =
    UUID.fromString(decoder.decodeString())

  override fun serialize(encoder: Encoder, value: UUID): Unit =
    serializer.serialize(encoder, value.toString())
}
