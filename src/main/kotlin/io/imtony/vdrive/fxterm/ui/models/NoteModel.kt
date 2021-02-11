@file:Suppress("unused")

package io.imtony.vdrive.fxterm.ui.models

import java.time.format.DateTimeFormatter
import java.util.UUID
import mu.KLogging
import javafx.geometry.Pos
import javafx.scene.Parent
import org.springframework.stereotype.Service
import tornadofx.*
import io.imtony.vdrive.fxterm.generated.gql.*

/**
 * The main model that represents a note. These should never be constructed
 * manually.
 * @property id The unique [UUID] that identifies this note.
 * @property title The title for this note.
 * @property content The main content body for this note.
 * @property tags The tags associated with this note.
 * @property created The time this note was originally created.
 * @property updated The time this note was last updated.
 */
@Suppress("MemberVisibilityCanBePrivate")
class NoteModel(
  val id: UUID,
  val title: String,
  val content: String,
  val tags: Array<String> = arrayOf(),
  val created: java.time.LocalDateTime,
  val updated: java.time.LocalDateTime,
) {

  override fun equals(other: Any?): Boolean = other is NoteModel && this.id == other.id

  override fun hashCode(): Int = this.id.hashCode()

  override fun toString(): String = "NoteModel[" +
    "id=$id," +
    "title='$title'," +
    "content='$content'," +
    "tags=[${tags.joinToString(",")}]," +
    "created=$created," +
    "updated=$updated]"

  companion object : KLogging()
}

@Service
class NoteResultParser {
  fun parse(other: GetNoteById.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: GetAllNotes.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: UpdateNote.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: CreateNote.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: GetNotesCreatedAfter.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: GetNotesCreatedBefore.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: GetNotesCreatedBetween.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: GetNotesUpdatedAfter.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: GetNotesUpdatedBefore.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

//  fun parse(other: GetNotesUpdatedBetween.Note): NoteModel = NoteModel(
//    other.id.value,
//    other.title,
//    other.content,
//    other.tags.toTypedArray(),
//    other.created.value,
//    other.updated.value
//  )

  fun parse(other: SearchNotesByContent.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: SearchNotesByTag.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: SearchNotesByText.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )

  fun parse(other: SearchNotesByTitle.Note): NoteModel = NoteModel(
    other.id.value,
    other.title,
    other.content,
    other.tags.toTypedArray(),
    other.created.value,
    other.updated.value
  )
}

class NoteViewModel : ItemViewModel<NoteModel>() {
  val id = bind(NoteModel::id)
  val title = bind(NoteModel::title)
  val content = bind(NoteModel::content)
  val tags = bind(NoteModel::tags)
  val created = bind(NoteModel::created)
  val updated = bind(NoteModel::updated)

  val idString = id.stringBinding {
    it?.toString() ?: "<Null>"
  }
  val tagsString = tags.stringBinding {
    if (it == null || it.isEmpty()) "[]" else "[${it.joinToString(", ")}]"
  }
  val createdString = created.stringBinding {
    it?.format(dateFormat) ?: "<Null>"
  }
  val updatedString = updated.stringBinding {
    it?.format(dateFormat) ?: "<Null>"
  }

  companion object {
    private val dateFormat = DateTimeFormatter.ofPattern("h:m a E MMM d, y")
  }
}

class NoteListFragment : ListCellFragment<NoteModel>() {
  private val dateFormat = DateTimeFormatter.ofPattern("h:m a E MMM d, y")

  val note = NoteViewModel().bindTo(this)

  override val root: Parent = vbox(10, Pos.TOP_CENTER) {
    with (note) {
      text(idString)
      text(title)
      text(content)
      text(tagsString)
      text(createdString)
      text(updatedString)
    }
  }
}