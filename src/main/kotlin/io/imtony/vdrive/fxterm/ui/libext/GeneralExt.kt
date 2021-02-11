package io.imtony.vdrive.fxterm.ui.libext

import java.io.Closeable
import kotlinx.coroutines.DisposableHandle
import javafx.animation.Animation
import javafx.beans.property.BooleanProperty
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.*

fun Animation.skipOnClick(owner: Node) {
  fun skip() {
    this.delay = 0.seconds
    this.jumpTo("end")
    owner.onLeftClick { }
  }
  owner.onLeftClick { skip() }
}

fun BooleanProperty.toggle() {
  this.set(!this.get())
}

fun Parent.addMany(vararg nodes: Node) {
  for (node in nodes) {
    this.add(node)
  }
}

// fun Color.Companion.randomRgb() = Color.color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
// fun Color.Companion.randomHsb() = Color.hsb(Random.nextDouble(360.0), Random.nextDouble(), Random.nextDouble())

inline fun <reified T> using(subject: T, block: T.() -> Unit) {
  try {
    block.invoke(subject)
  } finally {
    if (subject is AutoCloseable) {
      subject.close()
    } else if (subject is Closeable) {
      subject.close()
    } else if (subject is DisposableHandle) {
      subject.dispose()
    }
  }
}
