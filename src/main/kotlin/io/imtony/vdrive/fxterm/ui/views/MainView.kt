package io.imtony.vdrive.fxterm.ui.views

import java.util.Stack
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import org.controlsfx.control.MasterDetailPane
import org.controlsfx.control.NotificationPane
import tornadofx.*
import io.imtony.vdrive.fxterm.FxTermApp
import io.imtony.vdrive.fxterm.generated.gql.GetAllNotes
import io.imtony.vdrive.fxterm.ui.models.NoteListFragment
import io.imtony.vdrive.fxterm.ui.models.NoteModel
import io.imtony.vdrive.fxterm.ui.models.NoteResultParser
import io.imtony.vdrive.fxterm.utils.FakeDataSource

@ObsoleteCoroutinesApi
class MainView : CoroutineView("Main View") {
  private val showNotification = booleanProperty(false)

  private val detailSide = objectProperty(Side.RIGHT)
  private val showDetails = booleanProperty(false)
  private val dividerPos = floatProperty(0.5F)

  private lateinit var notifications: NotificationPane
  private lateinit var masterDetailPane: MasterDetailPane
  private lateinit var borderPane: BorderPane
  //private lateinit var masonPane: JFXMasonryPane

  private var allNotes = observableListOf<NoteModel>()
  private val noteListView = find<NoteListView> {
    this.notes.bind(allNotes) { it }
  }

  val frag by di<NoteListViewFragment>()

  private val webClient: GraphQLWebClient = FxTermApp.createWebClient()
  private val noteParser: NoteResultParser = FxTermApp.getResultParser()

  override fun onBeforeShow() {
    if (instance.get() == null) instance.set(this)

    launch {
      val results: GraphQLResponse<GetAllNotes.Result> = FakeDataSource.GetAllNotes(webClient).execute()
      val data: GetAllNotes.Result? = results.data
      val errors: List<GraphQLError>? = results.errors
      if (errors != null) {
        log.warning { "Errors were returned from graphql query:\n\t${errors.joinToString("\n\t")}" }
      }

      data?.getAllNotes?.let { notes ->
        notes.mapTo(allNotes) { noteParser.parse(it) }
        log.info { "Loaded ${notes.size} notes into observable note list." }
      }
    }
  }

  private val defaultContent: Node = HBox().apply {
    alignment = Pos.CENTER
    text("No content to display...")
  }
  private val currentContent: ObjectProperty<Node> = objectProperty(defaultContent)
  private val navigation = Navigator(defaultContent, currentContent, noteListView)

  fun getNavigator() = navigation

  override val root: Parent = borderpane {
    setPrefSize(800.0, 600.0)
    fitToParentSize()
    borderPane = this
    currentContent.addListener { _, _, new ->
      center = new ?: defaultContent
      this.requestLayout()
    }
    currentContent.set(noteListView.root)
    bottom = find<ControlToolbarView>().root
  }

  companion object {
    private val instance: ObjectProperty<MainView> = objectProperty()

    val InstanceProperty: ReadOnlyObjectProperty<MainView> get() = instance

    val Instance: MainView get() = instance.get()
  }

  class Navigator(
    val defaultContent: Node,
    val currentContent: ObjectProperty<Node>,
    val listView: NoteListView
  ) {
    private val navigation: Stack<Node> = Stack<Node>().apply {
      this.push(defaultContent)
    }

    fun setMainContent(node: Node) {
      val current = currentContent.get()
      if (node == current) {
        return
      }

      navigation.push(current)
      currentContent.set(node)
    }

    fun navigateBack() {
      if (!navigation.empty()) {
        currentContent.set(navigation.pop())
      }
    }

    fun showAllNotes() {
      setMainContent(listView.root)
    }
  }
}
