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

const val CREATE_NOTE: String =
    "mutation CreateNote(${'$'}input: CreateNoteInput!) {\r\n  createNote(input: ${'$'}input) {\r\n    id\r\n    title\r\n    content\r\n    tags\r\n    created\r\n    updated\r\n  }\r\n}"

class CreateNote(
  private val graphQLClient: GraphQLWebClient
) {
  suspend fun execute(variables: CreateNote.Variables,
      requestBuilder: WebClient.RequestBodyUriSpec.() -> Unit = {}):
      GraphQLResponse<CreateNote.Result> = graphQLClient.execute(CREATE_NOTE, "CreateNote",
      variables, requestBuilder)

  data class Variables(
    val input: CreateNote.CreateNoteInput
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
   * The input required to create a new note. All fields are optional, if none are provided the new
   * note will be created with an empty title, empty content, an empty tag array, and 'now' as the
   * creation date.
   */
  data class CreateNoteInput(
    val content: String? = null,
    val created: CreateNote.LocalDateTime? = null,
    val tags: List<String>? = null,
    val title: String? = null
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
   * Representation of a single 'Note' object.
   */
  data class Note(
    /**
     * The UUID of the Note.
     */
    val id: CreateNote.UUID,
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
    val created: CreateNote.LocalDateTime,
    /**
     * The LocalDateTime of when the Note was last updated.
     */
    val updated: CreateNote.LocalDateTime
  )

  data class Result(
    val createNote: CreateNote.Note?
  )
}
