package io.imtony.vdrive.fxterm

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import tornadofx.*
import kotlin.reflect.KClass
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import io.imtony.vdrive.fxterm.ui.models.NoteResultParser
import io.imtony.vdrive.fxterm.ui.views.LoadingView
import io.imtony.vdrive.fxterm.ui.views.MainView
import io.imtony.vdrive.fxterm.ui.views.OldMainView

class SpringContainer(private val context: ConfigurableApplicationContext) : DIContainer {
  override fun <T : Any> getInstance(type: KClass<T>): T = context.getBean(type.java)

  override fun <T : Any> getInstance(type: KClass<T>, name: String): T = context.getBean(name, type.java)
}

/**
 * The main application class.
 */
@SpringBootApplication
class FxTermApp : App(MainView::class) {
  private lateinit var context: ConfigurableApplicationContext

  override fun init() {
    this.context = SpringApplication.run(this.javaClass)
    context.autowireCapableBeanFactory.autowireBean(this)

    FX.dicontainer = SpringContainer(context)
  }

  @Bean
  fun getWebClient(): GraphQLWebClient = GraphQLWebClient("http://localhost:8080/graphql")

  @Bean
  fun getResultParser(): NoteResultParser = NoteResultParser()

  companion object {
    fun createWebClient() = GraphQLWebClient("http://localhost:8080/graphql")

    fun getResultParser(): NoteResultParser = NoteResultParser()
  }
}
