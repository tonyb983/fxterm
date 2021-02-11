package io.imtony.vdrive.fxterm.gql.scalars

import java.time.Instant
import com.expediagroup.graphql.client.converter.ScalarConverter

@Suppress("unused")
class JavaInstantScalarConverter : ScalarConverter<Instant> {
  override fun toJson(value: Instant): Any = value.toString()

  override fun toScalar(rawValue: Any): Instant = Instant.parse(rawValue.toString())
}
