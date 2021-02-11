@file:Suppress("unused")

package io.imtony.vdrive.fxterm.ui.views

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.javafx.JavaFx
import javafx.animation.Animation
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.*
import tornadofx.*
import io.imtony.vdrive.fxterm.ui.libext.coroutines.*

@ObsoleteCoroutinesApi
abstract class CoroutineView(title: String? = null, icon: Node? = null) : View(title, icon), CoroutineScope {
  private var job = Job()

  /**
   * The context of this scope.
   * Context is encapsulated by the scope and used for implementation of coroutine builders that are extensions on the scope.
   * Accessing this property in general code is not recommended for any purposes except accessing the [Job] instance for advanced usages.
   *
   * By convention, should contain an instance of a [job][Job] to enforce structured concurrency.
   */
  final override val coroutineContext: CoroutineContext
    get() = Dispatchers.JavaFx + job

  /**
   * Called when a Component is detached from the Scene
   */
  final override fun onUndock() {
    super.onUndock()
    job.cancel()
    job = Job()
  }

  open fun viewUndocked() { }

  // Node Context Menu
  fun <T : Node> T.onContextMenuRequestedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ContextMenuEvent) -> Unit
  ) = onContextMenuRequestedActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Drag Events

  fun <T : Node> T.onDragDetectedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onDragDetectedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onDragDoneActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (DragEvent) -> Unit
  ) = onDragDoneActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onDragEnteredActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (DragEvent) -> Unit
  ) = onDragEnteredActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onDragExitedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (DragEvent) -> Unit
  ) = onDragExitedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onDragDroppedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (DragEvent) -> Unit
  ) = onDragDroppedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onDragOverActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (DragEvent) -> Unit
  ) = onDragOverActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Key Events

  fun <T : Node> T.onKeyPressedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (KeyEvent) -> Unit
  ) = onKeyPressedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onKeyReleasedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (KeyEvent) -> Unit
  ) = onKeyReleasedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onKeyTypedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (KeyEvent) -> Unit
  ) = onKeyTypedActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Mouse Events

  fun <T : Node> T.onMouseClickedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onMouseClickedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseDragEnteredActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseDragEvent) -> Unit
  ) = onMouseDragEnteredActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseDragExitedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseDragEvent) -> Unit
  ) = onMouseDragExitedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseDragReleasedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseDragEvent) -> Unit
  ) = onMouseDragReleasedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseDragOverActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseDragEvent) -> Unit
  ) = onMouseDragOverActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseDraggedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onMouseDraggedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseEnteredActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onMouseEnteredActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseExitedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onMouseExitedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseMovedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onMouseMovedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMousePressedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onMousePressedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onMouseReleasedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (MouseEvent) -> Unit
  ) = onMouseReleasedActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Rotate Events

  fun <T : Node> T.onRotateActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (RotateEvent) -> Unit
  ) = onRotateActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onRotationStartedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (RotateEvent) -> Unit
  ) = onRotationStartedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onRotationFinishedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (RotateEvent) -> Unit
  ) = onRotationFinishedActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Input Method Events

  fun <T : Node> T.onInputMethodTextChangedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (InputMethodEvent) -> Unit
  ) = onInputMethodTextChangedActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Scroll Events

  fun <T : Node> T.onScrollActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ScrollEvent) -> Unit
  ) = onScrollActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onScrollStartedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ScrollEvent) -> Unit
  ) = onScrollStartedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onScrollFinishedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ScrollEvent) -> Unit
  ) = onScrollFinishedActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Swipe Events

  fun <T : Node> T.onSwipeUpActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (SwipeEvent) -> Unit
  ) = onSwipeUpActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onSwipeDownActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (SwipeEvent) -> Unit
  ) = onSwipeDownActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onSwipeLeftActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (SwipeEvent) -> Unit
  ) = onSwipeLeftActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onSwipeRightActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (SwipeEvent) -> Unit
  ) = onSwipeRightActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Touch Events

  fun <T : Node> T.onTouchMovedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (TouchEvent) -> Unit
  ) = onTouchMovedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onTouchStationaryActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (TouchEvent) -> Unit
  ) = onTouchStationaryActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onTouchPressedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (TouchEvent) -> Unit
  ) = onTouchPressedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onTouchReleasedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (TouchEvent) -> Unit
  ) = onTouchReleasedActor(this@CoroutineView, dispatcher, capacity, action)

  // Node Zoom Events

  fun <T : Node> T.onZoomActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ZoomEvent) -> Unit
  ) = onZoomActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onZoomStartedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ZoomEvent) -> Unit
  ) = onZoomStartedActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : Node> T.onZoomFinishedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ZoomEvent) -> Unit
  ) = onZoomFinishedActor(this@CoroutineView, dispatcher, capacity, action)

  // ButtonBase Events

  fun <T : ButtonBase> T.onActionActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ActionEvent) -> Unit
  ) = onActionActor(this@CoroutineView, dispatcher, capacity, action)

  // ChoiceBox Events

  fun <T : ChoiceBox<*>> T.onShowingActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onShowingActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ChoiceBox<*>> T.onShownActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onShownActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ChoiceBox<*>> T.onHidingActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onHidingActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ChoiceBox<*>> T.onHiddenActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onHiddenActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ChoiceBox<*>> T.onActionActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ActionEvent) -> Unit
  ) = onActionActor(this@CoroutineView, dispatcher, capacity, action)

  // ComboBoxBase Events

  fun <T : ComboBoxBase<*>> T.onShowingActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onShowingActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ComboBoxBase<*>> T.onShownActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onShownActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ComboBoxBase<*>> T.onHidingActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onHidingActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ComboBoxBase<*>> T.onHiddenActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onHiddenActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : ComboBoxBase<*>> T.onActionActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ActionEvent) -> Unit
  ) = onActionActor(this@CoroutineView, dispatcher, capacity, action)

  // ContextMenu Events

  fun <T : ContextMenu> T.onActionActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ActionEvent) -> Unit
  ) = onActionActor(this@CoroutineView, dispatcher, capacity, action)

  // MenuItem Events

  fun <T : MenuItem> T.onActionActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ActionEvent) -> Unit
  ) = onActionActor(this@CoroutineView, dispatcher, capacity, action)

  fun <T : MenuItem> T.onMenuValidationActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (Event) -> Unit
  ) = onMenuValidationActor(this@CoroutineView, dispatcher, capacity, action)

  // TextField Events

  fun <T : TextField> T.onActionActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ActionEvent) -> Unit
  ) = onActionActor(this@CoroutineView, dispatcher, capacity, action)

  // Animation Events

  fun <T : Animation> T.onFinishedActor(
    dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
    capacity: Int = Channel.CONFLATED,
    action: suspend (ActionEvent) -> Unit
  ) = onFinishedActor(this@CoroutineView, dispatcher, capacity, action)
}

@ObsoleteCoroutinesApi
suspend fun CoroutineView.uiThread(block: suspend CoroutineScope.() -> Unit) = withContext(this.coroutineContext, block)

@ObsoleteCoroutinesApi
suspend fun CoroutineView.bgThread(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Default, block)


