package io.imtony.vdrive.fxterm.ui.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Orientation.HORIZONTAL
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.jfxButton
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.jfxComboBox
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.jfxTextField
import io.imtony.vdrive.fxterm.ui.models.NoteModel
import io.imtony.vdrive.fxterm.utils.FakeDataSource

enum class SearchType {
  Title,
  Content,
  Tag,
  FullText;

  override fun toString(): String = when (this) {
    Title -> "Title"
    Content -> "Content"
    Tag -> "Tag"
    FullText -> "Full Text"
  }

  companion object {
    val valueList = values().toList()
  }
}

class SearchNotesView : View("My View") {
  private val searchText = stringProperty("")
  private val searchType: ObjectProperty<SearchType> = objectProperty(SearchType.Title)

  fun executeSearch() {
    log.warning { "Executing search for '${searchText.get()}' in category '${searchType.get()}'" }
    searchResultList.clear()
    searchResultList.add(FakeDataSource.randomNote())
  }

  private val searchResultList: ObservableList<NoteModel> = observableListOf()

  private val noteListView by di<NoteListViewFragment>()

  private val searchResults = ScrollPane().apply {
    noteListView.notes.bind(searchResultList) { it }
    this.content = noteListView.root.also {
      fitToParentSize()
    }
  }

  override val root = borderpane {
    top {
      hbox(10, CENTER) {
        fitToParentWidth()
        jfxTextField(searchText) {
          action {
            if (searchText.get().isNotBlank()) {
              executeSearch()
              this.text = ""
            }
          }
        }
        separator(VERTICAL)
        jfxComboBox(searchType, SearchType.valueList) { }
        separator(VERTICAL)
        jfxButton(graphic = FontAwesomeIconView(FontAwesomeIcon.SEARCH, "16")) {
          action {
            if (searchText.get().isNotBlank()) {
              executeSearch()
              this.text = ""
            }
          }
        }
      }
    }
    center {
      scrollpane {
        fitToParentSize()
        noteListView.notes.bind(searchResultList) { it }
        this.content = noteListView.root.also { fitToParentSize() }
      }
    }
  }
}
