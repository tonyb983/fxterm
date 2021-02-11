package io.imtony.vdrive.fxterm.ui.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.*
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.stage.PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT
import javafx.util.Duration
import org.controlsfx.control.PopOver.ArrowLocation.BOTTOM_CENTER
import org.controlsfx.control.PopOver.ArrowLocation.BOTTOM_LEFT
import tornadofx.*
import tornadofx.controlsfx.*
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.jfxButton

class ControlToolbarView : View("Control Toolbar") {
  val buttons = listOf(
    ToolbarButton(
      "Back",
      CARET_SQUARE_ALT_LEFT,
      "18"
    ) {
      if (it == null) {
        log.warning { "Navigator passed to ControlButton is null." }
      }

      it?.navigateBack()
    },
    ToolbarButton(
      "List All Notes",
      LIST_ALT,
      "18"
    ) {
      if (it == null) {
        log.warning { "Navigator passed to ControlButton is null." }
      }

      it?.showAllNotes()
    },
    ToolbarButton(
      "Search Notes",
      SEARCH,
      "18"
    ) {
      if (it == null) {
        log.warning { "Navigator passed to ControlButton is null." }
      }
      it?.setMainContent(find<SearchNotesView>().root)
    }
  )

  private var mainView: MainView? = null

  init {
    MainView.InstanceProperty.addListener { obs, old, new ->
      if (new != null) {
        mainView = new
      }
    }
  }

  override val root: Parent = hbox(
    spacing = 10,
    alignment = Pos.CENTER
  ) {

    buttons.forEach {
      jfxButton(graphic = FontAwesomeIconView(it.icon, it.iconSize)) {
        action { it.onClick.invoke(mainView?.getNavigator()) }
        val popUp = popover(CONTENT_TOP_RIGHT, BOTTOM_CENTER) {
          text(it.description)
        }
        onHover { hovering ->
          if (hovering) {
            popUp.show(this)
          } else {
            popUp.hide(Duration.millis(100.0))
          }
        }
      }
    }
  }
}

data class ToolbarButton(
  val description: String,
  val icon: FontAwesomeIcon,
  val iconSize: String,
  val onClick: (MainView.Navigator?) -> Unit,
)