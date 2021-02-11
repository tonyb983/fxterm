package io.imtony.vdrive.fxterm.generated.gql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.imtony.vdrive.fxterm.gql.scalars.UuidScalarConverter
import kotlin.Any
import kotlin.Boolean
import kotlin.String
import kotlin.Unit
import kotlin.jvm.JvmStatic
import org.springframework.web.reactive.function.client.WebClient

const val DELETE_NOTE: String =
    "mutation DeleteNote(${'$'}input: DeleteNoteInput!) {\r\n  deleteNote(input: ${'$'}input)\r\n}"

class DeleteNote(
  private val graphQLClient: GraphQLWebClient
) {
  suspend fun execute(variables: DeleteNote.Variables,
      requestBuilder: WebClient.RequestBodyUriSpec.() -> Unit = {}):
      GraphQLResponse<DeleteNote.Result> = graphQLClient.execute(DELETE_NOTE, "DeleteNote",
      variables, requestBuilder)

  data class Variables(
    val input: DeleteNote.DeleteNoteInput
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

  data class DeleteNoteInput(
    val id: DeleteNote.UUID
  )

  data class Result(
    val deleteNote: Boolean
  )
}
