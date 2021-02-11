package io.imtony.vdrive.fxterm.ui.libext

import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.*

interface TestingView {
  val key: String
  val root: Parent
}

fun EventTarget.add(testingView: TestingView) = plusAssign(testingView.root)

abstract class TestViewBase : View("Base Test View"), TestingView {
  protected val testViewTitle: StringProperty = stringProperty("Default Title")
  protected val testViewContent: ObjectProperty<Node> = objectProperty()

  abstract override val key: String

  final override val root: Parent = titledpane(testViewTitle, testViewContent.value, false) {
    fitToParentSize()
    setMinSize(400.0, 400.0)
    testViewContent.addListener { _, _, new ->
      if (new != null) {
        this.content = new
        this.requestLayout()
      }
    }
  }
}
