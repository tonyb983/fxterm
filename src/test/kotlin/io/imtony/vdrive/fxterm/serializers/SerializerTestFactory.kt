package io.imtony.vdrive.fxterm.serializers

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.describeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer

inline fun <reified TTarget> serializerTestFactory(
  serializer: KSerializer<TTarget>,
  target: TTarget
) = describeSpec {
  val sName = serializer::class.simpleName ?: "Unknown Serializer"
  val tName = TTarget::class.simpleName ?: "Unknown Target"

  describe("$tName Serializer ($sName) Tests:") {
    it("Successfully Round Trip Serializes") {
      val json = AppSerializer.createJsonSerializer()
      val cbor = AppSerializer.createCborSerializer()
      val mp = AppSerializer.createMsgPackSerializer()

      val encoded1 = json.encodeToString(serializer, target)
      val encoded2 = cbor.encodeToByteArray(serializer, target)
      val encoded3 = mp.encodeToByteArray(serializer, target)

      val decoded1 = json.decodeFromString(serializer, encoded1)
      val decoded2 = cbor.decodeFromByteArray(serializer, encoded2)
      val decoded3 = mp.decodeFromByteArray(serializer, encoded3)

      println("Target ($tName): $target")
      println("Encoded1: $encoded1")
      println("Encoded2: $encoded2")
      println("Encoded3: $encoded3")
      println("Decoded1: $decoded1")
      println("Decoded2: $decoded2")
      println("Decoded3: $decoded3")

      withClue({ "JSON Round Trip" }) { decoded1 shouldBe target }
      withClue({ "CBOR Round Trip" }) { decoded2 shouldBe target }
      withClue({ "MsgPack Round Trip" }) { decoded3 shouldBe target }
      withClue({ "JSON to CBOR" }) { decoded1 shouldBe decoded2 }
      withClue({ "CBOR to MsgPack" }) { decoded2 shouldBe decoded3 }
      withClue({ "MsgPack to JSON" }) { decoded3 shouldBe decoded1 }
    }
  }
}