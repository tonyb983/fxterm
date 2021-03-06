package io.imtony.vdrive.fxterm.gql.scalars

import kotlinx.datetime.Instant
import com.expediagroup.graphql.client.converter.ScalarConverter

@Suppress("unused")
class KotlinInstantScalarConverter : ScalarConverter<Instant> {
  override fun toJson(value: Instant): Any = value.toString()

  override fun toScalar(rawValue: Any): Instant = Instant.parse(rawValue.toString())
}
