@file:Suppress("unused")

package io.imtony.vdrive.fxterm.ui.libext.jfoenix

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoField
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import com.jfoenix.controls.*
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import com.jfoenix.transitions.CachedTransition
import com.jfoenix.transitions.hamburger.*
import com.jfoenix.validation.*
import com.jfoenix.validation.base.ValidatorBase
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Window
import javafx.util.Callback
import javafx.util.Duration
import javafx.util.StringConverter
import tornadofx.*
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.HamburgerTrigger.DoubleClick
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.HamburgerTrigger.LeftClick
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.HamburgerTrigger.LongPress
import io.imtony.vdrive.fxterm.ui.libext.jfoenix.HamburgerTrigger.RightClick

// Buttons
fun EventTarget.jfxButton(
  text: String = "",
  graphic: Node? = null,
  op: JFXButton.() -> Unit = {}
) = JFXButton(text).attachTo(this, op) {
  if (graphic != null) it.graphic = graphic
}

fun EventTarget.jfxButton(
  text: ObservableValue<String>,
  graphic: Node? = null,
  op: JFXButton.() -> Unit = {}
) = JFXButton().attachTo(this, op) {
  it.textProperty().bind(text)
  if (graphic != null) it.graphic = graphic
}

// Check Box
fun EventTarget.jfxCheckBox(
  text: String? = null,
  property: Property<Boolean>? = null,
  op: JFXCheckBox.() -> Unit = {}
) = JFXCheckBox(text).attachTo(this, op) {
  if (property != null) it.bind(property)
}

fun <S> TableColumn<S, Boolean?>.useJfxCheckBox(editable: Boolean = true) = apply {
  cellFormat {
    graphic = cache {
      alignment = Pos.CENTER
      jfxCheckBox {
        if (editable) {
          selectedProperty().bindBidirectional(itemProperty())

          setOnAction {
            tableView.edit(index, tableColumn)
            commitEdit(!isSelected)
          }
        } else {
          selectedProperty().bind(itemProperty())
        }
      }
    }
  }
  if (editable) {
    runLater {
      tableView?.isEditable = true
    }
  }
}

// Combo Box
fun <T> EventTarget.jfxComboBox(property: Property<T>? = null, values: List<T>? = null, op: JFXComboBox<T>.() -> Unit = {}) =
  JFXComboBox<T>().attachTo(this, op) {
    if (values != null) it.items = values as? ObservableList<T> ?: values.asObservable()
    if (property != null) it.bind(property)
  }

fun <T> JFXComboBox<T>.cellFormat(scope: Scope, formatButtonCell: Boolean = true, formatter: ListCell<T>.(T) -> Unit) {
  cellFactory = Callback {
    //ListView may be defined or not, so properties are set the safe way
    SmartListCell(scope, it, mapOf<Any, Any>("tornadofx.cellFormat" to formatter))
  }
  if (formatButtonCell) buttonCell = cellFactory.call(null)
}

fun <T> JFXComboBox<T>.bindSelected(property: Property<T>) {
  selectionModel.selectedItemProperty().onChange {
    property.value = it
  }
}

// Hamburger
sealed class HamburgerTrigger {
  class LeftClick(val count: Int = 1) : HamburgerTrigger()
  class RightClick(val count: Int = 1) : HamburgerTrigger()
  object DoubleClick : HamburgerTrigger()
  class LongPress(val threshold: Duration = 1.seconds, val consume: Boolean = true) : HamburgerTrigger()

  fun leftClick(count: Int = 1) = LeftClick(count)
  fun rightClick(count: Int = 1) = RightClick(count)
  fun doubleClick() = DoubleClick
  fun longPress(threshold: Duration = 1.seconds, consume: Boolean = true) = LongPress(threshold, consume)
}

fun EventTarget.jfxHamburger(op: JFXHamburger.() -> Unit = {}) = JFXHamburger().attachTo(this, op)

inline fun <reified T> JFXHamburger.addHamburgerTransition(
  trigger: HamburgerTrigger = LeftClick(),
  transition: HamburgerTransition,
  modifyTransition: T.() -> Unit = {},
  crossinline onTriggerAction: T.() -> Unit = {
    this.rate = this.rate * -1.0
    this.play()
  }
)
  where T : HamburgerTransition,
        T : CachedTransition = this.apply {
  val transTask = transition.getAnimation(this) as T
  transTask.rate = -1.0
  transTask.apply(modifyTransition)
  when (trigger) {
    is LeftClick -> this.onLeftClick(trigger.count) {
      onTriggerAction.invoke(transTask)
    }
    is RightClick -> this.onRightClick(trigger.count) {
      onTriggerAction.invoke(transTask)
    }
    is DoubleClick -> this.onDoubleClick {
      onTriggerAction.invoke(transTask)
    }
    is LongPress -> this.longpress(trigger.threshold, trigger.consume) {
      onTriggerAction.invoke(transTask)
    }
  }
}

inline fun JFXHamburger.useBasicCloseTransition(
  trigger: HamburgerTrigger = LeftClick(),
  modifyTransition: HamburgerBasicCloseTransition.() -> Unit = {},
  crossinline onTriggerAction: HamburgerBasicCloseTransition.() -> Unit = {
    this.rate = this.rate * -1.0
    this.play()
  }
) = addHamburgerTransition(trigger, HamburgerBasicCloseTransition(), modifyTransition, onTriggerAction)

inline fun JFXHamburger.useSlideCloseTransition(
  trigger: HamburgerTrigger = LeftClick(),
  modifyTransition: HamburgerSlideCloseTransition.() -> Unit = {},
  crossinline onTriggerAction: HamburgerSlideCloseTransition.() -> Unit = {
    this.rate = this.rate * -1.0
    this.play()
  }
) = addHamburgerTransition(trigger, HamburgerSlideCloseTransition(), modifyTransition, onTriggerAction)

inline fun JFXHamburger.useNextArrowTransition(
  trigger: HamburgerTrigger = LeftClick(),
  modifyTransition: HamburgerNextArrowBasicTransition.() -> Unit = {},
  crossinline onTriggerAction: HamburgerNextArrowBasicTransition.() -> Unit = {
    this.rate = this.rate * -1.0
    this.play()
  }
) = addHamburgerTransition(trigger, HamburgerNextArrowBasicTransition(), modifyTransition, onTriggerAction)

inline fun JFXHamburger.useBackArrowTransition(
  trigger: HamburgerTrigger = LeftClick(),
  modifyTransition: HamburgerBackArrowBasicTransition.() -> Unit = {},
  crossinline onTriggerAction: HamburgerBackArrowBasicTransition.() -> Unit = {
    this.rate = this.rate * -1.0
    this.play()
  }
) = addHamburgerTransition(trigger, HamburgerBackArrowBasicTransition(), modifyTransition, onTriggerAction)

// Input Fields
fun EventTarget.jfxTextField(value: String? = null, op: JFXTextField.() -> Unit = {}) = JFXTextField().attachTo(this, op) {
  if (value != null) it.text = value
}

fun EventTarget.jfxTextField(property: ObservableValue<String>, op: JFXTextField.() -> Unit = {}) = jfxTextField().apply {
  bind(property)
  op(this)
}

@JvmName("jfxTextFieldNumber")
fun EventTarget.jfxTextField(property: ObservableValue<Number>, op: JFXTextField.() -> Unit = {}) = jfxTextField().apply {
  bind(property)
  op(this)
}

@JvmName("jfxTextFieldInt")
fun EventTarget.jfxTextField(property: ObservableValue<Int>, op: JFXTextField.() -> Unit = {}) = jfxTextField().apply {
  bind(property)
  op(this)
}

fun EventTarget.jfxPasswordField(value: String? = null, op: JFXPasswordField.() -> Unit = {}) = JFXPasswordField().attachTo(this, op) {
  if (value != null) it.text = value
}

fun EventTarget.jfxPasswordField(property: ObservableValue<String>, op: JFXPasswordField.() -> Unit = {}) = jfxPasswordField().apply {
  bind(property)
  op(this)
}

// Progress Bar
fun EventTarget.jfxProgressBar(initialValue: Double? = null, op: JFXProgressBar.() -> Unit = {}) = JFXProgressBar().attachTo(this, op) {
  if (initialValue != null) it.progress = initialValue
}

fun EventTarget.jfxProgressBar(property: ObservableValue<Number>, op: JFXProgressBar.() -> Unit = {}) = jfxProgressBar().apply {
  bind(property)
  op(this)
}

fun <S> TableColumn<S, out Number?>.useJfxProgressBar(
  scope: Scope,
  afterCommit: (TableColumn.CellEditEvent<S, Number?>) -> Unit = {}
) = apply {
  cellFormat(scope) {
    addClass(Stylesheet.progressBarTableCell)
    graphic = cache {
      jfxProgressBar(itemProperty().doubleBinding { it?.toDouble() ?: 0.0 }) {
        useMaxWidth = true
      }
    }
  }
  @Suppress("UNCHECKED_CAST")
  (this as? TableColumn<S, Number?>)?.setOnEditCommit {
    val property = it.tableColumn.getCellObservableValue(it.rowValue) as? Property<Number?>
    property?.value = it.newValue?.toDouble()
    afterCommit(it as TableColumn.CellEditEvent<S, Number?>)
  }
}

// Radio Button

private const val TOGGLE_GROUP_KEY = "tornadofx.toggleGroupValue"

/**
 * Create a radiobutton inside the current or given toggle group. The optional value parameter will be matched against
 * the extension property `selectedValueProperty()` on Toggle Group. If the #ToggleGroup.selectedValueProperty is used,
 * it's value will be updated to reflect the value for this radio button when it's selected.
 *
 * Likewise, if the `selectedValueProperty` of the ToggleGroup is updated to a value that matches the value for this
 * radiobutton, it will be automatically selected.
 */
fun EventTarget.jfxRadioButton(
  text: String? = null,
  group: ToggleGroup? = getToggleGroup(),
  value: Any? = null,
  op: JFXRadioButton.() -> Unit = {}
) = JFXRadioButton().attachTo(this, op) {
  it.text = if (value != null && text == null) value.toString() else text ?: ""
  it.properties[TOGGLE_GROUP_KEY] = value ?: text
  if (group != null) it.toggleGroup = group
}

// Slider
fun EventTarget.jfxSlider(
  min: Number? = null,
  max: Number? = null,
  value: Number? = null,
  orientation: Orientation? = null,
  op: JFXSlider.() -> Unit = {}
) = JFXSlider().attachTo(this, op) {
  if (min != null) it.min = min.toDouble()
  if (max != null) it.max = max.toDouble()
  if (value != null) it.value = value.toDouble()
  if (orientation != null) it.orientation = orientation
}

fun <T> EventTarget.jfxSlider(
  range: ClosedRange<T>,
  value: Number? = null,
  orientation: Orientation? = null,
  op: JFXSlider.() -> Unit = {}
): JFXSlider
  where T : Comparable<T>,
        T : Number {
  return jfxSlider(range.start, range.endInclusive, value, orientation, op)
}

// Spinner

fun EventTarget.jfxSpinner(
  startingAngle: Double = JFXSpinner.INDETERMINATE_PROGRESS,
  op: JFXSpinner.() -> Unit = {}
) = JFXSpinner(startingAngle).attachTo(this, op)

// JFX Tabs / TabPane
fun JFXTabPane.tab(text: String? = null, tag: Any? = null, op: Tab.() -> Unit = {}): Tab {
  val tab = Tab(text ?: tag?.toString())
  tab.tag = tag
  tabs.add(tab)
  return tab.also(op)
}

// Toggle Button
/**
 * Create a toggleButton inside the current or given toggle group. The optional value parameter will be matched against
 * the extension property `selectedValueProperty()` on Toggle Group. If the #ToggleGroup.selectedValueProperty is used,
 * it's value will be updated to reflect the value for this radio button when it's selected.
 *
 * Likewise, if the `selectedValueProperty` of the ToggleGroup is updated to a value that matches the value for this
 * toggleButton, it will be automatically selected.
 */
fun EventTarget.jfxToggleButton(
  text: String? = null,
  group: ToggleGroup? = getToggleGroup(),
  selectFirst: Boolean = true,
  value: Any? = null,
  op: JFXToggleButton.() -> Unit = {}
) = JFXToggleButton().attachTo(this, op) {
  it.text = if (value != null && text == null) value.toString() else text ?: ""
  it.properties[TOGGLE_GROUP_KEY] = value ?: text
  if (group != null) it.toggleGroup = group
  if (it.toggleGroup?.selectedToggle == null && selectFirst) it.isSelected = true
}

fun EventTarget.jfxToggleButton(
  text: ObservableValue<String>? = null,
  group: ToggleGroup? = getToggleGroup(),
  selectFirst: Boolean = true,
  value: Any? = null,
  op: JFXToggleButton.() -> Unit = {}
) = JFXToggleButton().attachTo(this, op) {
  it.textProperty().bind(text)
  it.properties[TOGGLE_GROUP_KEY] = value ?: text
  if (group != null) it.toggleGroup = group
  if (it.toggleGroup?.selectedToggle == null && selectFirst) it.isSelected = true
}

fun EventTarget.jfxToggleButton(
  group: ToggleGroup? = getToggleGroup(),
  selectFirst: Boolean = true,
  value: Any? = null,
  op: JFXToggleButton.() -> Unit = {}
) = JFXToggleButton().attachTo(this, op) {
  it.properties["tornadofx.toggleGroupValue"] = value
  if (group != null) it.toggleGroup = group
  if (it.toggleGroup?.selectedToggle == null && selectFirst) it.isSelected = true
}

fun JFXToggleButton.whenSelected(op: () -> Unit) {
  selectedProperty().onChange { if (it) op() }
}

// List View
fun <T> EventTarget.jfxListView(values: ObservableList<T>? = null, op: JFXListView<T>.() -> Unit = {}) =
  JFXListView<T>().attachTo(this, op) {
    if (values != null) {
      if (values is SortedFilteredList<T>) values.bindTo(it)
      else it.items = values
    }
  }

fun <T> EventTarget.jfxListView(values: ReadOnlyListProperty<T>, op: JFXListView<T>.() -> Unit = {}) =
  jfxListView(values as ObservableValue<ObservableList<T>>, op)

fun <T> EventTarget.jfxListView(values: ObservableValue<ObservableList<T>>, op: JFXListView<T>.() -> Unit = {}) = JFXListView<T>()
  .attachTo(this, op) {
    fun rebinder() {
      (it.items as? SortedFilteredList<T>)?.bindTo(it)
    }
    it.itemsProperty().bind(values)
    rebinder()
    it.itemsProperty().onChange {
      rebinder()
    }
  }

class JFXListCellCache<T>(private val cacheProvider: (T) -> Node) {
  private val store = mutableMapOf<T, Node>()
  fun getOrCreateNode(value: T) = store.getOrPut(value, { cacheProvider(value) })
}

@Suppress("MemberVisibilityCanBePrivate")
abstract class JFXListCellFragment<T> : ItemFragment<T>() {
  val cellProperty: ObjectProperty<JFXListCell<T>?> = SimpleObjectProperty()
  var cell by cellProperty
  val editingProperty = SimpleBooleanProperty(false)
  val editing by editingProperty

  open fun startEdit() {
    cell?.startEdit()
  }

  open fun commitEdit(newValue: T) {
    cell?.commitEdit(newValue)
  }

  open fun cancelEdit() {
    cell?.cancelEdit()
  }

  open fun onEdit(op: () -> Unit) {
    editingProperty.onChange { if (it) op() }
  }
}

fun <T> JFXListView<T>.bindSelected(property: Property<T>) {
  selectionModel.selectedItemProperty().onChange {
    property.value = it
  }
}

fun <T> JFXListView<T>.bindSelected(model: ItemViewModel<T>) = this.bindSelected(model.itemProperty)

fun <T> JFXListView<T>.onEdit(scope: Scope = FX.defaultScope, eventListener: JFXListCell<T>.(EditEventType, T?) -> Unit) {
  isEditable = true
  properties["tornadofx.editSupport"] = eventListener
  // Install a edit capable cellFactory it none is present. The default cellFormat factory will do.
  if (properties["tornadofx.editCapable"] != true) cellFormat(scope) { }
}

/**
 * Calculate a unique Node per item and set this Node as the graphic of the ListCell.
 *
 * To support this feature, a custom cellFactory is automatically installed, unless an already
 * compatible cellFactory is found. The cellFactories installed via #cellFormat already knows
 * how to retrieve cached values.
 */
fun <T> JFXListView<T>.cellCache(scope: Scope = FX.defaultScope, cachedGraphicProvider: (T) -> Node) {
  properties["tornadofx.cellCache"] = ListCellCache(cachedGraphicProvider)
  // Install a cache capable cellFactory it none is present. The default cellFormat factory will do.
  if (properties["tornadofx.cellCacheCapable"] != true) {
    cellFormat(scope) { }
  }
}

fun <T> JFXListView<T>.multiSelect(enable: Boolean = true) {
  selectionModel.selectionMode = if (enable) SelectionMode.MULTIPLE else SelectionMode.SINGLE
}

// Tree Table View
fun <T> EventTarget.jfxTreeView(root: TreeItem<T>? = null, op: JFXTreeView<T>.() -> Unit = {}) = JFXTreeView<T>().attachTo(this, op) {
  if (root != null) it.root = root
}

fun <T : RecursiveTreeObject<T>?> EventTarget.jfxTreeTableView(root: TreeItem<T>? = null, op: JFXTreeTableView<T>.() -> Unit = {}) =
  JFXTreeTableView<T>()
    .attachTo(this, op) {
      if (root != null) it.root = root
    }

fun <T : Any> JFXTreeView<T>.lazyPopulate(
  leafCheck: (LazyTreeItem<T>) -> Boolean = { !it.hasChildren() },
  itemProcessor: (LazyTreeItem<T>) -> Unit = {},
  childFactory: (TreeItem<T>) -> List<T>?
) {
  fun createItem(value: T) = LazyTreeItem(value, leafCheck, itemProcessor, childFactory).also(itemProcessor)

  requireNotNull(root) { "You must set a root TreeItem before calling lazyPopulate" }

  task {
    childFactory.invoke(root)
  } success {
    root.children.setAll(it?.map(::createItem) ?: emptyList())
  }
}

/**
 * Create a column using the propertyName of the attribute you want shown.
 */
fun <S : RecursiveTreeObject<S>?, T> JFXTreeTableView<S>.jfxColumn(
  title: String,
  propertyName: String,
  op: JFXTreeTableColumn<S, T>.() -> Unit = {}
): JFXTreeTableColumn<S, T> {
  val column = JFXTreeTableColumn<S, T>(title)
  column.cellValueFactory = TreeItemPropertyValueFactory<S, T>(propertyName)
  addColumnInternal(column)
  return column.also(op)
}

/**
 * Create a column using the getter of the attribute you want shown.
 */
@JvmName("pojoColumn")
fun <S : RecursiveTreeObject<S>?, T> JFXTreeTableView<S>.jfxColumn(title: String, getter: KFunction<T>): JFXTreeTableColumn<S, T> {
  val startIndex = if (getter.name.startsWith("is") && getter.name[2].isUpperCase()) 2 else 3
  val propName = getter.name.substring(startIndex).decapitalize()
  return this.jfxColumn(title, propName)
}

val <T : RecursiveTreeObject<T>?> JFXTreeTableView<T>.selectedCell: TreeTablePosition<T, *>?
  get() = selectionModel.selectedCells.firstOrNull()

val <T : RecursiveTreeObject<T>?> JFXTreeTableView<T>.selectedColumn: TreeTableColumn<T, *>?
  get() = selectedCell?.tableColumn

val <T : RecursiveTreeObject<T>?> JFXTreeTableView<T>.selectedValue: Any?
  get() = selectedColumn?.getCellObservableValue(selectionModel.selectedItem)?.value

inline fun <reified S : RecursiveTreeObject<S>?, T> JFXTreeTableView<S>.jfxColumn(
  title: String,
  prop: KMutableProperty1<S, T>,
  noinline op: JFXTreeTableColumn<S, T>.() -> Unit = {}
): JFXTreeTableColumn<S, T> {
  val column = JFXTreeTableColumn<S, T>(title)
  column.cellValueFactory = Callback { observable(it.value.value, prop) }
  addColumnInternal(column)
  return column.also(op)
}

inline fun <reified S : RecursiveTreeObject<S>?, T> JFXTreeTableView<S>.jfxColumn(
  title: String,
  prop: KProperty1<S, T>,
  noinline op: JFXTreeTableColumn<S, T>.() -> Unit = {}
): JFXTreeTableColumn<S, T> {
  val column = JFXTreeTableColumn<S, T>(title)
  column.cellValueFactory = Callback { observable(it.value.value, prop) }
  addColumnInternal(column)
  return column.also(op)
}

@JvmName(name = "columnForObservableProperty")
inline fun <reified S : RecursiveTreeObject<S>?, T> JFXTreeTableView<S>.jfxColumn(
  title: String,
  prop: KProperty1<S, ObservableValue<T>>
): JFXTreeTableColumn<S, T> {
  val column = JFXTreeTableColumn<S, T>(title)
  column.cellValueFactory = Callback { prop.call(it.value.value) }
  addColumnInternal(column)
  return column
}

inline fun <S : RecursiveTreeObject<S>?, reified T> JFXTreeTableView<S>.jfxColumn(
  title: String,
  observableFn: KFunction<ObservableValue<T>>
): JFXTreeTableColumn<S, T> {
  val column = JFXTreeTableColumn<S, T>(title)
  column.cellValueFactory = Callback { observableFn.call(it.value) }
  addColumnInternal(column)
  return column
}

/**
 * Create a column with a value factory that extracts the value from the given callback.
 */
inline fun <reified S : RecursiveTreeObject<S>?, T> JFXTreeTableView<S>.jfxColumn(
  title: String,
  noinline valueProvider: (TreeTableColumn.CellDataFeatures<S, T>) -> ObservableValue<T>
): JFXTreeTableColumn<S, T> {
  val column = JFXTreeTableColumn<S, T>(title)
  column.cellValueFactory = Callback { valueProvider(it) }
  addColumnInternal(column)
  return column
}

// Pickers
fun EventTarget.jfxColorPicker(
  color: Color? = null,
  mode: ColorPickerMode = ColorPickerMode.Button,
  op: JFXColorPicker.() -> Unit = {}
) = JFXColorPicker().attachTo(this, op) {
  if (mode == ColorPickerMode.MenuButton) it.addClass(JFXColorPicker.STYLE_CLASS_BUTTON)
  else if (mode == ColorPickerMode.SplitMenuButton) it.addClass(JFXColorPicker.STYLE_CLASS_SPLIT_BUTTON)
  if (color != null) it.value = color
}

fun EventTarget.jfxColorPicker(
  colorProperty: ObjectProperty<Color>,
  mode: ColorPickerMode = ColorPickerMode.Button,
  op: JFXColorPicker.() -> Unit = {}
) = JFXColorPicker().apply { bind(colorProperty) }.attachTo(this, op) {
  if (mode == ColorPickerMode.MenuButton) it.addClass(JFXColorPicker.STYLE_CLASS_BUTTON)
  else if (mode == ColorPickerMode.SplitMenuButton) it.addClass(JFXColorPicker.STYLE_CLASS_SPLIT_BUTTON)
}

fun EventTarget.jfxDatePicker(op: JFXDatePicker.() -> Unit = {}) = JFXDatePicker().attachTo(this, op)
fun EventTarget.jfxDatePicker(property: Property<LocalDate>, op: JFXDatePicker.() -> Unit = {}) = jfxDatePicker().apply {
  bind(property)
  op(this)
}

fun EventTarget.jfxTimePicker(op: JFXTimePicker.() -> Unit = {}) = JFXTimePicker().attachTo(this, op)
fun EventTarget.jfxTimePicker(property: Property<LocalTime>, op: JFXTimePicker.() -> Unit = {}) = jfxTimePicker().apply {
  bind(property)
  op(this)
}

// Dialog
fun EventTarget.jfxDialog(
  op: JFXDialog.() -> Unit
) = JFXDialog().attachTo(this, op)

fun EventTarget.jfxDialog(
  dialogContainer: StackPane,
  content: Region,
  transitionType: JFXDialog.DialogTransition,
  op: JFXDialog.() -> Unit
) = JFXDialog(dialogContainer, content, transitionType).attachTo(this, op)

fun EventTarget.jfxDialog(
  dialogContainer: StackPane,
  content: Region,
  transitionType: JFXDialog.DialogTransition,
  overlayClose: Boolean,
  op: JFXDialog.() -> Unit
) = JFXDialog(dialogContainer, content, transitionType, overlayClose).attachTo(this, op)

// Popup
@Deprecated("Not working yet.")
fun EventTarget.jfxPopup(
  op: JFXPopup.() -> Unit
) = JFXPopup().apply(op)

@Deprecated("Not working yet.")
fun EventTarget.jfxPopup(
  content: Region,
  op: JFXPopup.() -> Unit
) = JFXPopup(content).apply(op)

// Drawer
fun EventTarget.jfxDrawer(
  op: JFXDrawer.() -> Unit
) = JFXDrawer().attachTo(this, op)

fun EventTarget.jfxDrawer(
  duration: Duration,
  op: JFXDrawer.() -> Unit
) = JFXDrawer(duration).attachTo(this, op)

// Tooltip
fun Node.jfxTooltip(text: String? = null, pos: Pos = Pos.BOTTOM_CENTER, graphic: Node? = null, op: JFXTooltip.() -> Unit = {}): JFXTooltip {
  val newToolTip = JFXTooltip(text, pos)
  graphic?.apply { newToolTip.graphic = this }
  newToolTip.op()
  if (this is Control) tooltip = newToolTip else JFXTooltip.install(this, newToolTip)
  return newToolTip
}

// NodesList
fun EventTarget.jfxNodesList(
  op: JFXNodesList.() -> Unit
) = JFXNodesList().attachTo(this, op)

// Alert
fun Node.jfxAlertWithContents(
  show: Boolean = true,
  window: Window? = FX.primaryStage.scene?.window,
  op: JFXAlert<Unit>.() -> Array<Node>
): JFXAlert<Unit> = JFXAlert<Unit>(window).apply {
  this.setContent(*op.invoke(this))
}.also {
  if (show) it.show()
}

fun Node.jfxAlertWithContent(
  show: Boolean = true,
  window: Window? = FX.primaryStage.scene?.window,
  op: JFXAlert<Unit>.() -> Node
): JFXAlert<Unit> =
  JFXAlert<Unit>(window).apply {
    this.setContent(op.invoke(this))
  }.also {
    if (show) it.show()
  }

fun Node.jfxAlertUnit(op: JFXAlert<Unit>.() -> Unit = {}) = JFXAlert<Unit>().apply(op)
fun Node.jfxAlertStandard(op: JFXAlert<ButtonType>.() -> Unit = {}) = JFXAlert<ButtonType>().apply(op)
fun <TResult : Any?> Node.jfxAlert(op: JFXAlert<TResult>.() -> Unit = {}) = JFXAlert<TResult>().apply(op)
fun Node.jfxAlertUnit(window: Window?, op: JFXAlert<Unit>.() -> Unit = {}) = JFXAlert<Unit>(window).apply(op)
fun Node.jfxAlertStandard(window: Window?, op: JFXAlert<ButtonType>.() -> Unit = {}) = JFXAlert<ButtonType>(window).apply(op)
fun <TResult : Any?> Node.jfxAlert(window: Window?, op: JFXAlert<TResult>.() -> Unit = {}) = JFXAlert<TResult>(window).apply(op)

// ChipView
fun <T> EventTarget.jfxChipView(
  valueConverter: StringConverter<T>,
  op: JFXChipView<T>.() -> Unit
) = JFXChipView<T>().attachTo(this, op) {
  it.converterProperty().set(valueConverter)
}

// Masonry
fun EventTarget.jfxMasonPane(
  op: JFXMasonryPane.() -> Unit
) = JFXMasonryPane().attachTo(this, op) {
  @Suppress("INACCESSIBLE_TYPE")
  it.layoutMode = JFXMasonryPane.LayoutMode.MASONRY
}

// Masonry - Bin Packing
fun EventTarget.jfxBinPackingPane(
  op: JFXMasonryPane.() -> Unit
) = JFXMasonryPane().attachTo(this, op) {
  @Suppress("INACCESSIBLE_TYPE")
  it.layoutMode = JFXMasonryPane.LayoutMode.BIN_PACKING
}

// Badge
fun EventTarget.jfxBadge(
  op: JFXBadge.() -> Unit
) = JFXBadge().attachTo(this, op)

fun EventTarget.jfxBadge(
  control: Node? = null,
  pos: Pos = Pos.TOP_RIGHT,
  op: JFXBadge.() -> Unit
) = JFXBadge(control, pos).attachTo(this, op)

// Snack Bar
fun EventTarget.jfxSnackBar(
  snackbarPane: Pane,
  op: JFXSnackbar.() -> Unit
) = JFXSnackbar(snackbarPane).attachTo(this, op)

private fun millisNow() = LocalDateTime.now().get(ChronoField.MILLI_OF_DAY)
private fun getNextTick(d: Duration) = millisNow().toDouble().plus(d.toMillis())

fun <T : ValidatorBase> TextInputControl.jfxValidator(validator: T, config: T.() -> Unit = {}) {
  validator.srcControl = this
  this.textProperty().onChange { validator.validate() }
  validator.apply(config)
}

fun <T : ValidatorBase> TextInputControl.jfxValidator(validator: T, cooldownMillis: Long, config: T.() -> Unit = {}) {
  validator.srcControl = this
  var nextUpdate = millisNow().toLong() + cooldownMillis
  this.textProperty().onChange {
    val now = millisNow()
    if (nextUpdate >= now) {
      return@onChange
    }

    validator.validate()
    nextUpdate = now + cooldownMillis
  }
  validator.apply(config)
}

fun <T : ValidatorBase> TextInputControl.jfxValidator(validator: T, validationCooldown: Duration, config: T.() -> Unit = {}): Unit =
  this.jfxValidator(validator, validationCooldown.toMillis().toLong(), config)

fun TextInputControl.jfxDoubleValidator(message: String? = null, config: DoubleValidator.() -> Unit = {}) =
  this.jfxValidator(if (message == null) DoubleValidator() else DoubleValidator(message), config)

fun TextInputControl.jfxIntegerValidator(message: String? = null, config: IntegerValidator.() -> Unit = {}) =
  this.jfxValidator(if (message == null) IntegerValidator() else IntegerValidator(message), config)

fun TextInputControl.jfxNumberValidator(message: String? = null, config: NumberValidator.() -> Unit = {}) =
  this.jfxValidator(if (message == null) NumberValidator() else NumberValidator(message), config)

fun TextInputControl.jfxRegexValidator(regex: String, message: String? = null, config: RegexValidator.() -> Unit = {}) =
  this.jfxValidator(if (message == null) RegexValidator().also { it.regexPattern = regex } else RegexValidator(message).also {
    it.regexPattern = regex
  }, config)

fun TextInputControl.jfxRequiredFieldValidator(message: String? = null, config: RequiredFieldValidator.() -> Unit = {}) =
  this.jfxValidator(if (message == null) RequiredFieldValidator() else RequiredFieldValidator(message), config)

fun TextInputControl.jfxStringLengthValidator(length: Int, message: String? = null, config: StringLengthValidator.() -> Unit = {}) =
  this.jfxValidator(if (message == null) StringLengthValidator(length) else StringLengthValidator(length, message), config)

fun <TComboBox> ComboBoxBase<TComboBox>.jfxRequiredFieldValidator(message: String? = null, config: RequiredFieldValidator.() -> Unit = {}) {
  val validator = if (message == null) RequiredFieldValidator() else RequiredFieldValidator(message)
  validator.srcControl = this
  this.valueProperty().onChange { validator.validate() }
  validator.apply(config)
}

fun <TComboBox> ComboBoxBase<TComboBox>.jfxRequiredFieldValidator(
  message: String? = null,
  cooldownMillis: Long,
  config: RequiredFieldValidator.() -> Unit = {}
) {
  val validator = if (message == null) RequiredFieldValidator() else RequiredFieldValidator(message)
  validator.srcControl = this
  var nextUpdate = millisNow().toLong() + cooldownMillis
  this.valueProperty().onChange {
    val now = millisNow()
    if (nextUpdate >= now) {
      return@onChange
    }

    validator.validate()
    nextUpdate = now + cooldownMillis
  }
  validator.apply(config)
}

fun <TComboBox> ComboBoxBase<TComboBox>.jfxRequiredFieldValidator(
  message: String? = null,
  cooldown: Duration,
  config: RequiredFieldValidator.() -> Unit = {}
) =
  this.jfxRequiredFieldValidator(message, cooldown.toMillis().toLong(), config)
