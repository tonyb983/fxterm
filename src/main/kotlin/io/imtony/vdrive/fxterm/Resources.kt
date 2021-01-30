@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.imtony.vdrive.fxterm

import io.imtony.vdrive.fxterm.google.ext.drive.MimeType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.media.Media
import javafx.scene.text.Text
import tornadofx.*
import java.io.InputStream
import java.net.URL
import javax.json.JsonArray
import javax.json.JsonObject

/**
 * Represents an application resource wrapped in a convenience object allowing for easy transformations of the path.
 *
 * @property resource The actual resource path (with $projectDir/src/main/resources/ considered the top-level root directory).
 */
data class AppResource(
  /**
   * The raw [resource] path.
   *
   * Example: a resource in physical path
   *
   * `file(projectDir.resolve("/src/main/resources/json/jsonData.json"))`
   *
   * should be
   * given the [resource] value of
   *
   * `"/json/jsonData.json"`.
   */
  val resource: String
) {
  /**
   * Represents this [resource] path in url form.
   *
   * #### Same as calling: `this.javaClass.getResource(resource)`
   */
  val url: URL get(): URL = this.javaClass.getResource(resource)

  /**
   * This [resource] path as an external form url string.
   *
   * #### Same as calling: `this.javaClass.getResource(resource).toExternalForm()`
   */
  val urlString: String get(): String = url.toExternalForm()

  /**
   * Wraps this [resource] item into a JavaFX [Media] class.
   *
   * #### Same as calling: `Media(urlString)`
   */
  val media: Media get(): Media = Media(urlString)

  /**
   * Gets this resource as an [InputStream].
   *
   * #### Same as calling: `this.javaClass.getResourceAsStream(resource)`
   */
  val stream: InputStream get(): InputStream = this.javaClass.getResourceAsStream(resource)

  /**
   * Gets this resource as a [JsonObject].
   *
   * #### Same as calling: `stream.toJSON()`
   *
   * @see toJSON
   */
  val json: JsonObject get(): JsonObject = stream.toJSON()

  /**
   * Gets this resource as a [JsonArray].
   *
   * #### Same as calling: `stream.toJSONArray()`
   *
   * @see toJSONArray
   */
  val jsonArray: JsonArray get(): JsonArray = stream.toJSONArray()

  /**
   * Gets this resource as a [String]. Opens and then closes a [java.io.BufferedReader] to do so. Note,
   * this is one of few methods of this class that are cached using [lazy] instead of being
   * created on demand each time requested.
   *
   * #### Same as calling: `stream.use { it.bufferedReader().readText() }`
   *
   * @see java.io.BufferedReader
   */
  val text: String by lazy { stream.use { it.bufferedReader().readText() } }

  /**
   * Gets this resource as a JavaFX [Image]. Note, this does not display the image, use [toImageView]
   * if that is your intent.
   *
   * #### Same as calling: `Image(stream)`
   */
  val image: Image get(): Image = Image(stream)

  /**
   * Gets this resource as a JavaFX [ImageView]. [bgLoading] is forwarded to the [Image] constructor to
   * indicate if this [Image] is being created in the background or not.
   *
   * #### Same as calling: `ImageView(Image(urlString, bgLoading))`
   */
  fun toImageView(bgLoading: Boolean = false): ImageView = ImageView(Image(urlString, bgLoading))

  /**
   * Gets this resource as an [InputStream].
   *
   * #### Same as calling: `Text(text).apply(block)`
   */
  fun toText(block: Text.() -> Unit = {}): Text = Text(text).apply(block)
}

/**
 * Tree structured object holding all resources used in this app.
 */
object Resources {
  private const val root = ""

  /**
   * The `creds` folder of the application resources.
   */
  object Creds {
    private const val base = "$root/creds"

    /**
     * The '/creds/credentials.json' resource.
     */
    object Credentials {
      /**
       * The raw path of this resource.
       *
       * #### /creds/credentials.json
       */
      const val Path: String = "$base/credentials.json"

      /**
       * The name of this resource (for ex. for use in DI frameworks).
       *
       * #### credsFile
       */
      const val Name: String = "credsFile"

      /**
       * The [MimeType] of this resource object.
       *
       * #### [MimeType.json]
       */
      val Mime: MimeType = MimeType.json

      /**
       * The [AppResource] version of this resource.
       */
      val Instance: AppResource by lazy { AppResource(Path) }
    }
  }

  /**
   * The `fonts` folder of the application resources.
   */
  object Fonts {
    private const val base = "$root/fonts"

    /**
     * The '/fonts/ActorRegular.ttf' resource.
     */
    object ActorRegular {
      /**
       * The raw path of this resource.
       *
       * #### /fonts/ActorRegular.ttf
       */
      const val Path: String = "$base/ActorRegular.ttf"

      /**
       * The name of this resource (for ex. for use in DI frameworks).
       *
       * #### ActorRegular
       */
      const val Name: String = "ActorRegular"

      /**
       * The [MimeType] of this resource object.
       *
       * #### [MimeType.trueTypeFont]
       */
      val Mime: MimeType = MimeType.trueTypeFont

      /**
       * The [AppResource] version of this resource.
       */
      val Instance: AppResource by lazy { AppResource(Path) }
    }

    /**
     * The '/fonts/Consola.ttf' resource.
     */
    object Consola {
      /**
       * The raw path of this resource.
       *
       * #### /fonts/Consola.ttf
       */
      const val Path: String = "$base/Consola.ttf"

      /**
       * The name of this resource (for ex. for use in DI frameworks).
       *
       * #### Consola
       */
      const val Name: String = "Consola"

      /**
       * The [MimeType] of this resource object.
       *
       * #### [MimeType.trueTypeFont]
       */
      val Mime: MimeType = MimeType.trueTypeFont

      /**
       * The [AppResource] version of this resource.
       */
      val Instance: AppResource by lazy { AppResource(Path) }
    }

    /**
     * The '/fonts/GadugiBold.ttf' resource.
     */
    object GadugiBold {
      /**
       * The raw path of this resource.
       *
       * #### /fonts/GadugiBold.ttf
       */
      const val Path: String = "$base/GadugiBold.ttf"

      /**
       * The name of this resource (for ex. for use in DI frameworks).
       *
       * #### GadugiBold
       */
      const val Name: String = "GadugiBold"

      /**
       * The [MimeType] of this resource object.
       *
       * #### [MimeType.trueTypeFont]
       */
      val Mime: MimeType = MimeType.trueTypeFont

      /**
       * The [AppResource] version of this resource.
       */
      val Instance: AppResource by lazy { AppResource(Path) }
    }

    /**
     * The '/fonts/LatoRegular.ttf' resource.
     */
    object LatoRegular {
      /**
       * The raw path of this resource.
       *
       * #### /fonts/LatoRegular.ttf
       */
      const val Path: String = "$base/LatoRegular.ttf"

      /**
       * The name of this resource (for ex. for use in DI frameworks).
       *
       * #### LatoRegular
       */
      const val Name: String = "LatoRegular"

      /**
       * The [MimeType] of this resource object.
       *
       * #### [MimeType.trueTypeFont]
       */
      val Mime: MimeType = MimeType.trueTypeFont

      /**
       * The [AppResource] version of this resource.
       */
      val Instance: AppResource by lazy { AppResource(Path) }
    }

    /**
     * The '/fonts/Overpass.ttf' resource.
     */
    object Overpass {
      /**
       * The raw path of this resource.
       *
       * #### /fonts/Overpass.ttf
       */
      const val Path: String = "$base/Overpass.ttf"

      /**
       * The name of this resource (for ex. for use in DI frameworks).
       *
       * #### Overpass
       */
      const val Name: String = "Overpass"

      /**
       * The [MimeType] of this resource object.
       *
       * #### [MimeType.trueTypeFont]
       */
      val Mime: MimeType = MimeType.trueTypeFont

      /**
       * The [AppResource] version of this resource.
       */
      val Instance: AppResource by lazy { AppResource(Path) }
    }

    /**
     * The '/fonts/UbuntuBook.ttf' resource.
     */
    object UbuntuBook {
      /**
       * The raw path of this resource.
       *
       * #### /fonts/UbuntuBook.ttf
       */
      const val Path: String = "$base/UbuntuBook.ttf"

      /**
       * The name of this resource (for ex. for use in DI frameworks).
       *
       * #### UbuntuBook
       */
      const val Name: String = "UbuntuBook"

      /**
       * The [MimeType] of this resource object.
       *
       * #### [MimeType.trueTypeFont]
       */
      val Mime: MimeType = MimeType.trueTypeFont

      /**
       * The [AppResource] version of this resource.
       */
      val Instance: AppResource by lazy { AppResource(Path) }
    }
  }
}
