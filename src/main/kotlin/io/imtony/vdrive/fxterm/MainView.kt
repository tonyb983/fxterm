package io.imtony.vdrive.fxterm

import io.imtony.vdrive.fxterm.commands.CommandProcessor
import io.imtony.vdrive.fxterm.fs.DriveFileSystem
import io.imtony.vdrive.fxterm.google.services.GoogleServiceCollection
import io.imtony.vdrive.fxterm.utils.getLineEnding
import io.imtony.vdrive.fxterm.utils.lockProperty
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
import kotlin.coroutines.CoroutineContext

private val EOL by lazy { String.getLineEnding() }

class MainView : View("Main View"), CoroutineScope {

  private val outputHistory = mutableListOf<String>()
  private val outputTexts = observableListOf<Text>()
  private val outputDisplay = stringProperty("")

  private val currentInput = stringProperty("")
  private val inputLock = lockProperty

  private val consoleFont = Font.loadFont(resources.url("/fonts/consola.ttf").toExternalForm(), 12.0)

  private var google: GoogleServiceCollection by singleAssign()
  private var DFS: DriveFileSystem by singleAssign()
  private var commandProcessor: CommandProcessor by singleAssign()

  init {
    inputLock.lock()
    launch { runInit() }
  }

  private suspend fun runInit() {
    google = GoogleServiceCollection.createDefault()
    DFS = DriveFileSystem.newAsync(this, google.drive).await()
    commandProcessor = CommandProcessor(google, DFS)
  }

  private fun addOutput(text: String) {
    outputHistory.add(text)
    outputDisplay.plusAssign("${text}${EOL}")
  }

  private fun onPressedEnter(tf: TextField) {
    inputLock.lock()
    val currentText = tf.text
    tf.clear()
    this@MainView.launch {
      commandProcessor.processCommand(currentText, inputLock, outputTexts)
    }
  }

  override val root: Parent = borderpane {
    setPrefSize(800.0, 600.0)
    center {
      fitToParentSize()
      scrollpane {
        fitToParentSize()
        style {
          this.backgroundColor = multi(Color.rgb(42, 33, 57))
        }
        // text(outputDisplay) { fitToParentSize() }
        textflow {
          fitToParentSize()
          bindChildren(outputTexts) { it }
          style {
            this.font = consoleFont
            this.backgroundColor = multi(Color.rgb(42, 33, 57))
          }
        }
      }
    }

    bottom {
      prefHeight(50.0)
      fitToParentWidth()
      textfield(currentInput) {
        prefHeight(50.0)
        disableWhen { inputLock.whenLocked }
        this.promptText = "This is the prompt text."
        setOnAction { onPressedEnter(this) }
      }
    }
  }

  private var job = Job()
  override val coroutineContext: CoroutineContext
    get() = Dispatchers.JavaFx + job
}
