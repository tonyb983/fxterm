package io.imtony.vdrive.fxterm

import dispatch.core.DispatcherProvider
import io.imtony.vdrive.fxterm.commands.CommandProcessor
import io.imtony.vdrive.fxterm.fs.DriveFileSystem
import io.imtony.vdrive.fxterm.google.services.GoogleServiceCollection
import io.imtony.vdrive.fxterm.utils.getLineEnding
import io.imtony.vdrive.fxterm.utils.lockProperty
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
import kotlin.coroutines.CoroutineContext

private val EOL by lazy { String.getLineEnding() }

/**
 * The main / starting view for the application.
 */
class MainView : View("Main View"), CoroutineScope {

  private val outputHistory = mutableListOf<String>()
  private val outputTexts = observableListOf<Text>()
  private val outputDisplay = stringProperty("")

  private val currentInput = stringProperty("")
  private val inputLock = lockProperty

  private val consoleFont by lazy { FontLocker.getFont(AppFonts.Consola, 12) }

  private var google: GoogleServiceCollection by singleAssign()
  private var DFS: DriveFileSystem by singleAssign()
  private var commandProcessor: CommandProcessor by singleAssign()

  init {
    inputLock.lock()
    runLater(Duration.millis(200.0)) {
      launch { runInit() }
    }
  }

  private suspend fun runInit() {
    google = GoogleServiceCollection.createDefault()
    DFS = DriveFileSystem.newAsync(this, google.drive).await()
    commandProcessor = CommandProcessor(google, DFS)
    runLater {
      addOutput("Listing root Drive contents.")
      DFS.getDirContents("/").forEach { addOutput("\t - $it") }
      inputLock.unlock()
    }
  }

  private fun addOutput(text: String) {
    outputHistory.add(text)
    outputDisplay.plusAssign("$text$EOL")
  }

  private fun onPressedEnter(tf: TextField) {
    inputLock.lock()
    val currentText = tf.text
    tf.clear()
    this@MainView.launch {
      commandProcessor.processCommand(currentText, inputLock, outputTexts)
    }
  }

  /**
   * The root node for this [UIComponent].
   */
  override val root: Parent = borderpane {
    setPrefSize(800.0, 600.0)
    center {
      fitToParentSize()
      scrollpane {
        fitToParentSize()
        this.background = Background(BackgroundFill(Color.rgb(42, 33, 57), CornerRadii.EMPTY, Insets(5.0)))
        // text(outputDisplay) { fitToParentSize() }
        this.content = textflow {
          this.background = Background(BackgroundFill(Color.rgb(42, 33, 57), CornerRadii.EMPTY, Insets(5.0)))
          fitToParentSize()
          outputDisplay.onChange {
            this@scrollpane.vvalue = 1.0
          }
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
    get() = Dispatchers.JavaFx + job + DispatcherProvider()
}
