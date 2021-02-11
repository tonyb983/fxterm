package io.imtony.vdrive.fxterm.gql.scalars

import java.time.LocalDateTime
import com.expediagroup.graphql.client.converter.ScalarConverter

@Suppress("unused")
class JavaTimeScalarConverter : ScalarConverter<LocalDateTime> {
  override fun toJson(value: LocalDateTime): Any = value.toString()

  override fun toScalar(rawValue: Any): LocalDateTime = LocalDateTime.parse(rawValue.toString())
}
