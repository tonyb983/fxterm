package io.imtony.vdrive.fxterm.serializers

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.*

class UuidSerializerTests : DescribeSpec({
  describe("UUID Serializer Tests") {
    it("Should serialize and deserialize a UUID.") {
      val uuid = UUID.fromString("ea9e45f1-1771-4172-b048-42fccbead45e")

      val json = AppSerializer.createJsonSerializer()
      val cbor = AppSerializer.createCborSerializer()
      val mp = AppSerializer.createMsgPackSerializer()

      val encoded1 = json.encodeToString(UuidSerializer, uuid)
      val encoded2 = cbor.encodeToByteArray(UuidSerializer, uuid)
      val encoded3 = mp.encodeToByteArray(UuidSerializer, uuid)

      val decoded1 = json.decodeFromString(UuidSerializer, encoded1)
      val decoded2 = cbor.decodeFromByteArray(UuidSerializer, encoded2)
      val decoded3 = mp.decodeFromByteArray(UuidSerializer, encoded3)

      println("UUID: $uuid")
      println("Encoded1: $encoded1")
      println("Encoded2: $encoded2")
      println("Encoded3: $encoded3")
      println("Decoded1: $decoded1")
      println("Decoded2: $decoded2")
      println("Decoded3: $decoded3")

      withClue({ "JSON Round Trip" }) { decoded1 shouldBe uuid }
      withClue({ "CBOR Round Trip" }) { decoded2 shouldBe uuid }
      withClue({ "MsgPack Round Trip" }) { decoded3 shouldBe uuid }
      withClue({ "JSON to CBOR" }) { decoded1 shouldBe decoded2 }
      withClue({ "CBOR to MsgPack" }) { decoded2 shouldBe decoded3 }
      withClue({ "MsgPack to JSON" }) { decoded3 shouldBe decoded1 }
    }
  }

  val target = UUID.fromString("ea9e45f1-1771-4172-b048-42fccbead45e")
  include(serializerTestFactory(UuidSerializer, target))
})
