package io.imtony.vdrive.fxterm.ui.views

import io.imtony.vdrive.fxterm.ui.libext.skipOnClick
import javafx.animation.Interpolator
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class LoadingView : View("Starting View") {
  private val startingSize = 800.0 to 600.0
  // private val transitionTo: View by find<MainView>()

  private fun loadMainView() = replaceWith<MainView>(ViewTransition.FadeThrough(3.seconds))

  override val root: Parent = stackpane {
    fitToParentSize()
    rectangle {
      val (x, y) = startingSize
      this.width = x
      this.height = y
      this.fill = Color.BLACK
    }
    text("Welcome") {
      this.fill = Color.GHOSTWHITE
      this.textAlignment = TextAlignment.CENTER
      this.opacity = 0.0
      this.font = Font.font(60.0)
      this.isUnderline = true
      this.style(true) {
        this.fontWeight = FontWeight.BOLD
      }
      this.opacityProperty().animate(1.0, 2.seconds, Interpolator.EASE_IN) {
        this.delay = 2.seconds
        this.setOnFinished {
          this@text.fill = Color.TRANSPARENT
          loadMainView()
        }
        this.skipOnClick(this@text)
      }
    }
  }
}
