package io.imtony.vdrive.fxterm.ui.libext.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.javafx.JavaFx
import javafx.animation.Animation
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.*
import javafx.stage.Window
import javafx.stage.WindowEvent


fun <T : Node> T.onMouseClickedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseClicked = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onContextMenuRequestedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ContextMenuEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ContextMenuEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onContextMenuRequested = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onDragDetectedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onDragDetected = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onDragDoneActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onDragDone = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onDragEnteredActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onDragEntered = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onDragExitedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onDragExited = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onDragDroppedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onDragDropped = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onDragOverActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onDragOver = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onKeyPressedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (KeyEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<KeyEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onKeyPressed = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onKeyReleasedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (KeyEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<KeyEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onKeyReleased = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onKeyTypedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (KeyEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<KeyEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onKeyTyped = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseDragEnteredActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseDragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseDragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseDragEntered = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseDragExitedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseDragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseDragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseDragExited = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseDragReleasedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseDragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseDragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseDragReleased = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseDragOverActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseDragEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseDragEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseDragOver = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseDraggedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseDragged = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseEnteredActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseEntered = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseExitedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseExited = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseMovedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseMoved = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMousePressedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMousePressed = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onMouseReleasedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (MouseEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<MouseEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMouseReleased = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onRotateActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (RotateEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<RotateEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onRotate = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onRotationStartedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (RotateEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<RotateEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onRotationStarted = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onRotationFinishedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (RotateEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<RotateEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onRotationFinished = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onInputMethodTextChangedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (InputMethodEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<InputMethodEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onInputMethodTextChanged = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onScrollActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ScrollEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ScrollEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onScroll = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onScrollStartedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ScrollEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ScrollEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onScrollStarted = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onScrollFinishedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ScrollEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ScrollEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onScrollFinished = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onSwipeUpActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (SwipeEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<SwipeEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onSwipeUp = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onSwipeDownActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (SwipeEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<SwipeEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onSwipeDown = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onSwipeLeftActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (SwipeEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<SwipeEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onSwipeLeft = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onSwipeRightActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (SwipeEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<SwipeEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onSwipeRight = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onTouchMovedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (TouchEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<TouchEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onTouchMoved = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onTouchStationaryActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (TouchEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<TouchEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onTouchStationary = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onTouchPressedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (TouchEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<TouchEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onTouchPressed = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onTouchReleasedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (TouchEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<TouchEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onTouchReleased = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onZoomActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ZoomEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ZoomEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onZoom = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onZoomStartedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ZoomEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ZoomEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onZoomStarted = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Node> T.onZoomFinishedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ZoomEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ZoomEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onZoomFinished = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ButtonBase> T.onActionActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ActionEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ActionEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onAction = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ChoiceBox<*>> T.onShowingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShowing = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ChoiceBox<*>> T.onShownActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShown = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ChoiceBox<*>> T.onHidingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHiding = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ChoiceBox<*>> T.onHiddenActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHidden = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ChoiceBox<*>> T.onActionActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ActionEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ActionEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onAction = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ComboBoxBase<*>> T.onShowingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShowing = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ComboBoxBase<*>> T.onShownActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShown = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ComboBoxBase<*>> T.onHidingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHiding = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ComboBoxBase<*>> T.onHiddenActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHidden = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ComboBoxBase<*>> T.onActionActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ActionEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ActionEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onAction = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : ContextMenu> T.onActionActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ActionEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ActionEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onAction = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : MenuItem> T.onActionActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ActionEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ActionEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onAction = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : MenuItem> T.onMenuValidationActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (Event) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<Event>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onMenuValidation = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : TextField> T.onActionActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ActionEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ActionEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onAction = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Animation> T.onFinishedActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (ActionEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<ActionEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onFinished = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Dialog<*>> T.onShowingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DialogEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DialogEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShowing = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Dialog<*>> T.onShownActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DialogEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DialogEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShown = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Dialog<*>> T.onHidingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DialogEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DialogEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHiding = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Dialog<*>> T.onHiddenActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DialogEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DialogEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHidden = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Dialog<*>> T.onCloseRequestActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (DialogEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<DialogEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onCloseRequest = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Window> T.onShowingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (WindowEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<WindowEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShowing = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Window> T.onShownActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (WindowEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<WindowEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onShown = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Window> T.onHidingActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (WindowEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<WindowEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHiding = EventHandler { event ->
    eventActor.offer(event)
  }
}

fun <T : Window> T.onHiddenActor(
  scope: CoroutineScope,
  dispatcher: CoroutineDispatcher = Dispatchers.JavaFx,
  capacity: Int = Channel.CONFLATED,
  action: suspend (WindowEvent) -> Unit
) {
  // launch one actor to handle all events on this node
  val eventActor = scope.actor<WindowEvent>(dispatcher, capacity) { // <--- Changed here
    for (event in channel) action(event) // pass event to action
  }
  // install a listener to offer events to this actor
  onHidden = EventHandler { event ->
    eventActor.offer(event)
  }
}
