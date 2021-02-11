package io.imtony.vdrive.fxterm.utils

import java.time.LocalDateTime
import java.util.Calendar.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import io.imtony.vdrive.fxterm.ui.models.NoteModel
import io.imtony.vdrive.fxterm.generated.gql.GetAllNotes as RealGetAllNotes

private val staticNotes = listOf(
  NoteModel(
    UUID.fromString("09d22d56-59b7-444d-8a82-543abba5c403"),
    "Note 1 Title",
    "This is the content for the first note.",
    arrayOf("Todo", "Work"),
    LocalDateTime.of(2020, 6, 21, 12, 0, 0),
    LocalDateTime.of(2020, 6, 28, 12, 0, 0),
  ),
  NoteModel(
    UUID.fromString("1c8c4044-ea6f-4ab4-b20d-150d2ea57f13"),
    "Note 2 Title",
    "This is the content for the second note.",
    arrayOf("Todo", "Work"),
    LocalDateTime.of(2020, 11, 21, 12, 0, 0),
    LocalDateTime.of(2020, 12, 28, 12, 0, 0),
  ),
  NoteModel(
    UUID.fromString("583006f7-0cdf-41e6-9218-f89cb2f5cf12"),
    "Note 3 Title",
    "This is the content for the third note.",
    arrayOf("Todo", "Work"),
    LocalDateTime.of(2021, 1, 10, 12, 0, 0),
    LocalDateTime.of(2021, 2, 2, 12, 0, 0),
  ),
  NoteModel(
    UUID.fromString("cb780bfc-c083-43c8-be74-ebe460cb6d02"),
    "Note 4 Title",
    "This is the content for the fourth note.",
    arrayOf("Todo", "Work"),
    LocalDateTime.of(2020, 10, 21, 12, 0, 0),
    LocalDateTime.of(2021, 1, 28, 12, 0, 0),
  ),
  NoteModel(
    UUID.fromString("c53c0fac-8fdb-4598-b411-867ec16ef853"),
    "Note 5 Title",
    "This is the content for the fifth note.",
    arrayOf("Todo", "Work"),
    LocalDateTime.of(2021, 2, 3, 12, 0, 0),
    LocalDateTime.of(2021, 2, 3, 14, 0, 0),
  ),
  NoteModel(
    UUID.fromString("3c41427b-f2f4-42d7-bffb-ab9bc743fd1b"),
    "Note 6 Title",
    "This is the content for the sith note. It has a typo.",
    arrayOf("Todo", "Work"),
    LocalDateTime.of(2021, 2, 3, 16, 0, 0),
    LocalDateTime.of(2021, 2, 3, 16, 0, 0),
  ),
)

object FakeDataSource {
  private val noteMap by lazy { ConcurrentHashMap(staticNotes.associateBy { it.id }) }

  fun randomNote() = noteMap.values.random()

  class GetAllNotes(webClient: GraphQLWebClient?) {
    fun execute(): GraphQLResponse<RealGetAllNotes.Result> = GraphQLResponse(
      RealGetAllNotes.Result(noteMap.values.map {
        RealGetAllNotes.Note(
          RealGetAllNotes.UUID(it.id),
          it.title,
          it.content,
          it.tags.toList(),
          RealGetAllNotes.LocalDateTime(it.created),
          RealGetAllNotes.LocalDateTime(it.updated)
        )
      })
    )
  }
}