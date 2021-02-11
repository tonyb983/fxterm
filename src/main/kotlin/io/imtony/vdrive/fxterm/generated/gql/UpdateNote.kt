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

const val UPDATE_NOTE: String =
    "mutation UpdateNote(${'$'}input: UpdateNoteInput!) {\r\n  updateNote(input: ${'$'}input) {\r\n    id\r\n    title\r\n    content\r\n    tags\r\n    created\r\n    updated\r\n  }\r\n}"

class UpdateNote(
  private val graphQLClient: GraphQLWebClient
) {
  suspend fun execute(variables: UpdateNote.Variables,
      requestBuilder: WebClient.RequestBodyUriSpec.() -> Unit = {}):
      GraphQLResponse<UpdateNote.Result> = graphQLClient.execute(UPDATE_NOTE, "UpdateNote",
      variables, requestBuilder)

  data class Variables(
    val input: UpdateNote.UpdateNoteInput
  )

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
   * The input required to update a note. All fields are optional except the ID which must pointto a
   * valid note. If all other fields are blank the update will be ignored.
   */
  data class UpdateNoteInput(
    val content: String? = null,
    val id: UpdateNote.UUID,
    val tags: List<String>? = null,
    val title: String? = null
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
   * Representation of a single 'Note' object.
   */
  data class Note(
    /**
     * The UUID of the Note.
     */
    val id: UpdateNote.UUID,
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
    val created: UpdateNote.LocalDateTime,
    /**
     * The LocalDateTime of when the Note was last updated.
     */
    val updated: UpdateNote.LocalDateTime
  )

  data class Result(
    val updateNote: UpdateNote.Note?
  )
}
