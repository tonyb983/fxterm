package io.imtony.vdrive.fxterm.serializers

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPackConfiguration
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

object AppSerializer {
  /**
   * Configures and creates a [Json] serializer with the app-wide settings.
   */
  fun createJsonSerializer(): Json = Json {
    serializersModule = SerializersModule {
      contextual(UuidSerializer)
      contextual(InstantSerializer)
      contextual(LocalDateTimeSerializer)
    }
    allowSpecialFloatingPointValues = true
    // allowStructuredMapKeys = true
    encodeDefaults = true
    // ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    prettyPrintIndent = "  "
  }

  /**
   * A [ThreadLocal] instance of a default configured [Json] serializer.
   */
  val json: ThreadLocal<Json> = ThreadLocal.withInitial(this::createJsonSerializer)

  /**
   * Configures and creates a [Cbor] serializer with the app-wide settings.
   */
  fun createCborSerializer(): Cbor = Cbor {
    serializersModule = SerializersModule {
      contextual(UuidSerializer)
      contextual(InstantSerializer)
      contextual(LocalDateTimeSerializer)
    }
    encodeDefaults = true
  }

  /**
   * A [ThreadLocal] instance of a default configured [Cbor] serializer.
   */
  val cbor: ThreadLocal<Cbor> = ThreadLocal.withInitial(this::createCborSerializer)

  /**
   * Configures and creates a [MsgPack] serializer with the app-wide settings.
   */
  fun createMsgPackSerializer(): MsgPack = MsgPack(MsgPackConfiguration.default, SerializersModule {
    contextual(UuidSerializer)
    contextual(InstantSerializer)
    contextual(LocalDateTimeSerializer)
  })

  /**
   * A [ThreadLocal] instance of a default configured [MsgPack] serializer.
   */
  val msgPack: ThreadLocal<MsgPack> = ThreadLocal.withInitial(this::createMsgPackSerializer)
}
