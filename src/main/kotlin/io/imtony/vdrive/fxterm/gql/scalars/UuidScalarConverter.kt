package io.imtony.vdrive.fxterm.gql.scalars

import java.util.UUID
import com.expediagroup.graphql.client.converter.ScalarConverter

@Suppress("unused")
class UuidScalarConverter : ScalarConverter<UUID> {
  override fun toJson(value: UUID): Any = value.toString()

  override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())
}
