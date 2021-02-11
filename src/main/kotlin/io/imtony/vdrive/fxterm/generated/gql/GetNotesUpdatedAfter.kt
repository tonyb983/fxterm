package io.imtony.vdrive.fxterm.generated.gql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.imtony.vdrive.fxterm.gql.scalars.JavaTimeScalarConverter
import io.imtony.vdrive.fxterm.gql.scalars.UuidScalarConverter
import kotlin.Any
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import org.springframework.web.reactive.function.client.WebClient

const val GET_NOTES_UPDATED_AFTER: String =
    "query GetNotesUpdatedAfter(${'$'}time: LocalDateTime!) {\r\n  getNotesUpdatedAfter(time: ${'$'}time) {\r\n    id\r\n    title\r\n    content\r\n    tags\r\n    created\r\n    updated\r\n  }\r\n}"

class GetNotesUpdatedAfter(
  private val graphQLClient: GraphQLWebClient
) {
  suspend fun execute(variables: GetNotesUpdatedAfter.Variables,
      requestBuilder: WebClient.RequestBodyUriSpec.() -> Unit = {}):
      GraphQLResponse<GetNotesUpdatedAfter.Result> = graphQLClient.execute(GET_NOTES_UPDATED_AFTER,
      "GetNotesUpdatedAfter", variables, requestBuilder)

  data class Variables(
    val time: GetNotesUpdatedAfter.LocalDateTime
  )

  /**
   * A type representing a kotlinx.datetime.JavaTime serialized as an ISO String.
   */
  data class LocalDateTime(
    val value: java.time.LocalDateTime
  ) {
    @JsonValue
    fun rawValue() = converter.toJson(value)

    companion object {
      val converter: JavaTimeScalarConverter = JavaTimeScalarConverter()

      @JsonCreator
      @JvmStatic
      fun create(rawValue: Any) = LocalDateTime(converter.toScalar(rawValue))
    }
  }

  /**
   * A type representing a formatted java.util.UUID
   */
  data class UUID(
    val value: java.util.UUID
  ) {
    @JsonValue
    fun rawValue() = converter.toJson(value)

    companion object {
      val converter: UuidScalarConverter = UuidScalarConverter()

      @JsonCreator
      @JvmStatic
      fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))
    }
  }

  /**
   * Representation of a single 'Note' object.
   */
  data class Note(
    /**
     * The UUID of the Note.
     */
    val id: GetNotesUpdatedAfter.UUID,
    /**
     * The title of the Note. May be blank.
     */
    val title: String,
    /**
     * The content of the Note. May be blank.
     */
    val content: String,
    /**
     * The tags applied to the Note. May be empty but never null.
     */
    val tags: List<String>,
    /**
     * The LocalDateTime of when the Note was created.
     */
    val created: GetNotesUpdatedAfter.LocalDateTime,
    /**
     * The LocalDateTime of when the Note was last updated.
     */
    val updated: GetNotesUpdatedAfter.LocalDateTime
  )

  data class Result(
    val getNotesUpdatedAfter: List<GetNotesUpdatedAfter.Note>
  )
}
