package io.imtony.vdrive.fxterm.ui.libext

import io.imtony.vdrive.fxterm.ui.views.CoroutineView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*

@ObsoleteCoroutinesApi
class CoroutineTestView : CoroutineView(), TestingView {
  private fun Node.onClick(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) {
    // launch one actor to handle all events on this node
    val eventActor = this@CoroutineTestView.actor<MouseEvent>(dispatcher, capacity = Channel.CONFLATED) { // <--- Changed here
      for (event in channel) action(event) // pass event to action
    }
    // install a listener to offer events to this actor
    onMouseClicked = EventHandler { event ->
      eventActor.offer(event)
    }
  }

  override val key: String = "cofx"
  private val testViewTitle: StringProperty = stringProperty("Coroutine Test View")
  private val testViewContent: ObjectProperty<Node> = objectProperty()

  private val textProperty = stringProperty("Click the Button...")

  init {
    this.testViewTitle.set("Coroutine Test View")
    this.testViewContent.set(
      VBox(10.0).apply {
        val l = text(textProperty) {
          fill = Color.valueOf("#C0C0C0")
        }
        val c = circle {
          radius = 20.0
          fill = Color.valueOf("#FF4081")
        }

        setup(l, c)
      }
    )
  }

  override val root: Parent = titledpane {
    fitToParentSize()
    setMinSize(400.0, 400.0)
    this.isCollapsible = false
    this.textProperty().bind(testViewTitle)
    this.contentProperty().bind(testViewContent)
  }

  fun setup(text: Text, clickReceiver: Node) {
    clickReceiver.onClick { // start coroutine when the circle is clicked
      for (i in 10 downTo 1) { // countdown from 10 to 1
        text.text = "Countdown $i ..." // update text
        delay(500) // wait half a second
      }
      text.text = "Done!"
    }
  }
}
