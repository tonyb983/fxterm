package io.imtony.vdrive.fxterm.gql.scalars

import kotlinx.datetime.LocalDateTime
import com.expediagroup.graphql.client.converter.ScalarConverter

@Suppress("unused")
class KotlinTimeScalarConverter : ScalarConverter<LocalDateTime> {
  override fun toJson(value: LocalDateTime): Any = value.toString()

  override fun toScalar(rawValue: Any): LocalDateTime = LocalDateTime.parse(rawValue.toString())
}
