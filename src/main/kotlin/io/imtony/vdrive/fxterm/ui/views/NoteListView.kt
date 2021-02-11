package io.imtony.vdrive.fxterm.ui.views

import javafx.collections.ObservableList
import javafx.scene.Parent
import org.springframework.stereotype.Component
import tornadofx.*
import io.imtony.vdrive.fxterm.ui.models.NoteListFragment
import io.imtony.vdrive.fxterm.ui.models.NoteModel

class NoteListView(
  val notes: ObservableList<NoteModel> = observableListOf()
) : View("My View") {

  override val root = scrollpane(true, fitToHeight = true) {
    listview(notes) {
      cellFragment(NoteListFragment::class)
    }
  }
}

@Component
class NoteListViewFragment : Fragment("Note List") {
  val notes: ObservableList<NoteModel> = observableListOf()
  override val root = listview(notes) {
    cellFragment(NoteListFragment::class)
  }
}