@file:Suppress("ClassName")

package io.imtony.vdrive.fxterm.google.ext.drive

import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.toImmutableList
import com.google.api.services.drive.model.File as DriveFile

/*
.json	  application/json
.epub	  application/epub+zip
.jar	  application/java-archive
.js	    application/javascript
.ogx	  application/ogg
.doc	  application/msword
.pdf	  application/pdf
.rtf	  application/rtf
.xml	  application/xml
.xhtml	application/xhtml+xml
.zip	  application/zip
.arc	  application/octet-stream
.bin	  application/octet-stream
.7z	    application/x-7z-compressed
.abw	  application/x-abiword
.bz	    application/x-bzip
.bz2	  application/x-bzip2
.csh	  application/x-csh
.rar	  application/x-rar-compressed
.sh	    application/x-sh
.tar	  application/x-tar
        application/vnd.google-apps.audio
        application/vnd.google-apps.document
        application/vnd.google-apps.drive-sdk
        application/vnd.google-apps.drawing
        application/vnd.google-apps.file
        application/vnd.google-apps.folder
        application/vnd.google-apps.form
        application/vnd.google-apps.fusiontable
        application/vnd.google-apps.map
        application/vnd.google-apps.photo
        application/vnd.google-apps.presentation
        application/vnd.google-apps.script
        application/vnd.google-apps.shortcut
        application/vnd.google-apps.site
        application/vnd.google-apps.spreadsheet
        application/vnd.google-apps.unknown
        application/vnd.google-apps.video
.xul	  application/vnd.mozilla.xul+xml
.xls	  application/vnd.ms-excel
.ppt	  application/vnd.ms-powerpoint
.aac	  audio/aac
.midi	  audio/midi
.mid	  audio/midi
.oga	  audio/ogg
.wav	  audio/x-wav
.weba	  audio/webm
.woff	  font/woff
.woff2	font/woff2
.ttf	  font/ttf
.gif	  image/gif
.ico	  image/x-icon
.jpeg	  image/jpeg
.jpg	  image/jpeg
.svg	  image/svg+xml
.tiff	  image/tiff
.tif	  image/tiff
.webp	  image/webp
.css	  text/css
.csv	  text/csv
.htm	  text/html
.html	  text/html
.ics	  text/calendar
.webm	  video/webm
.3gp	  video/3gpp
.3g2	  video/3gpp2
.avi	  video/x-msvideo
.mpeg	  video/mpeg
.ogv	  video/ogg
 */

sealed class MimeType(
  /**
   * The actual mime string.
   */
  val mime: String
) {
  /**
   * Primary extension for this mime type. (Use [hasExtension] to check all extensions.)
   */
  open val ext: String = ""

  /**
   * Primary extensions for this mime type. (Use [hasExtension] to check all extensions.)
   */
  open val exts: List<String> = emptyList()

  /**
   * Whether this [MimeType] is a "final" mime type or just a category / grouping.
   */
  open val isFinalType: Boolean = false

  /**
   * Returns whether or not the input string is one of the common extensions for this mime type.
   */
  open fun hasExtension(input: String): Boolean = ext == input || exts.contains(input)

  /**
   * Converts this [MimeType] to a [String] usable in a [Drive.Files.List.q][com.google.api.services.drive.Drive.Files.List.q]
   * query string.
   */
  fun toGoogleRequestQuery(negated: Boolean = false): String = "mimeType${if (negated) "!=" else "="}'$mime'"

  override fun toString(): String = mime
  override fun hashCode(): Int = mime.hashCode()
  override fun equals(other: Any?): Boolean = when (other) {
    is MimeType -> this.mime == other.mime
    is String -> this.mime == other
    else -> false
  }

  companion object {
    val invalid: Invalid = Invalid
    val googleAudio: MimeCategory.Application.Google.Audio = MimeCategory.Application.Google.Audio
    val googleDocument: MimeCategory.Application.Google.Document = MimeCategory.Application.Google.Document
    val googleDriveSd: MimeCategory.Application.Google.DriveSdk = MimeCategory.Application.Google.DriveSdk
    val googleDrawing: MimeCategory.Application.Google.Drawing = MimeCategory.Application.Google.Drawing
    val googleFile: MimeCategory.Application.Google.File = MimeCategory.Application.Google.File
    val googleFolder: MimeCategory.Application.Google.Folder = MimeCategory.Application.Google.Folder
    val googleForm: MimeCategory.Application.Google.Form = MimeCategory.Application.Google.Form
    val googleFusionTable: MimeCategory.Application.Google.FusionTable = MimeCategory.Application.Google.FusionTable
    val googleMap: MimeCategory.Application.Google.Map = MimeCategory.Application.Google.Map
    val googlePhoto: MimeCategory.Application.Google.Photo = MimeCategory.Application.Google.Photo
    val googlePresentation: MimeCategory.Application.Google.Presentation = MimeCategory.Application.Google.Presentation
    val googleScript: MimeCategory.Application.Google.Script = MimeCategory.Application.Google.Script
    val googleShortcut: MimeCategory.Application.Google.Shortcut = MimeCategory.Application.Google.Shortcut
    val googleSite: MimeCategory.Application.Google.Site = MimeCategory.Application.Google.Site
    val googleSpreadsheet: MimeCategory.Application.Google.Spreadsheet = MimeCategory.Application.Google.Spreadsheet
    val googleUnknown: MimeCategory.Application.Google.Unknown = MimeCategory.Application.Google.Unknown
    val googleVideo: MimeCategory.Application.Google.Video = MimeCategory.Application.Google.Video
    val msExcel: MimeCategory.Application.Microsoft.Excel = MimeCategory.Application.Microsoft.Excel
    val msPowerpoint: MimeCategory.Application.Microsoft.Powerpoint = MimeCategory.Application.Microsoft.Powerpoint
    val json: MimeCategory.Application.Json = MimeCategory.Application.Json
    val epubZip: MimeCategory.Application.EpubZip = MimeCategory.Application.EpubZip
    val javaArchive: MimeCategory.Application.JavaArchive = MimeCategory.Application.JavaArchive
    val javascript: MimeCategory.Application.Javascript = MimeCategory.Application.Javascript
    val oggExecutable: MimeCategory.Application.Ogg = MimeCategory.Application.Ogg
    val msWord: MimeCategory.Application.MsWord = MimeCategory.Application.MsWord
    val pdf: MimeCategory.Application.Pdf = MimeCategory.Application.Pdf
    val rtf: MimeCategory.Application.Rtf = MimeCategory.Application.Rtf
    val xml: MimeCategory.Application.Xml = MimeCategory.Application.Xml
    val xhtmlXml: MimeCategory.Application.XhtmlXml = MimeCategory.Application.XhtmlXml
    val zip: MimeCategory.Application.Zip = MimeCategory.Application.Zip
    val octetStream: MimeCategory.Application.OctetStream = MimeCategory.Application.OctetStream
    val abiword: MimeCategory.Application.xAbiword = MimeCategory.Application.xAbiword
    val bzip: MimeCategory.Application.xBzip = MimeCategory.Application.xBzip
    val bzip2: MimeCategory.Application.xBzip2 = MimeCategory.Application.xBzip2
    val csh: MimeCategory.Application.xCsh = MimeCategory.Application.xCsh
    val rarCompressed: MimeCategory.Application.xRarCompressed = MimeCategory.Application.xRarCompressed
    val shell: MimeCategory.Application.xShell = MimeCategory.Application.xShell
    val tar: MimeCategory.Application.xTar = MimeCategory.Application.xTar
    val sevenZipCompressed: MimeCategory.Application.x7zCompressed = MimeCategory.Application.x7zCompressed
    val aac: MimeCategory.Audio.Aac = MimeCategory.Audio.Aac
    val midi: MimeCategory.Audio.Midi = MimeCategory.Audio.Midi
    val oggAudio: MimeCategory.Audio.Ogg = MimeCategory.Audio.Ogg
    val xWav: MimeCategory.Audio.XWav = MimeCategory.Audio.XWav
    val webmAudio: MimeCategory.Audio.Webm = MimeCategory.Audio.Webm
    val openFont: MimeCategory.Font.OpenFont = MimeCategory.Font.OpenFont
    val woff: MimeCategory.Font.Woff = MimeCategory.Font.Woff
    val woff2: MimeCategory.Font.Woff2 = MimeCategory.Font.Woff2
    val trueTypeFont: MimeCategory.Font.TrueTypeFont = MimeCategory.Font.TrueTypeFont
    val gif: MimeCategory.Image.Gif = MimeCategory.Image.Gif
    val icon: MimeCategory.Image.Icon = MimeCategory.Image.Icon
    val jpeg: MimeCategory.Image.Jpeg = MimeCategory.Image.Jpeg
    val svgXml: MimeCategory.Image.SvgXml = MimeCategory.Image.SvgXml
    val tiff: MimeCategory.Image.Tiff = MimeCategory.Image.Tiff
    val webp: MimeCategory.Image.Webp = MimeCategory.Image.Webp
    val css: MimeCategory.Text.Css = MimeCategory.Text.Css
    val csv: MimeCategory.Text.Csv = MimeCategory.Text.Csv
    val html: MimeCategory.Text.Html = MimeCategory.Text.Html
    val calendar: MimeCategory.Text.Calendar = MimeCategory.Text.Calendar
    val webmVideo: MimeCategory.Video.Webm = MimeCategory.Video.Webm
    val gpp: MimeCategory.Video.Gpp = MimeCategory.Video.Gpp
    val gpp2: MimeCategory.Video.Gpp2 = MimeCategory.Video.Gpp2
    val msVideo: MimeCategory.Video.MsVideo = MimeCategory.Video.MsVideo
    val mpeg: MimeCategory.Video.Mpeg = MimeCategory.Video.Mpeg
    val oggVideo: MimeCategory.Video.Ogg = MimeCategory.Video.Ogg

    val allMimes: ImmutableCollection<MimeType> by lazy {
      listOf(
        invalid,
        googleAudio,
        googleDocument,
        googleDriveSd,
        googleDrawing,
        googleFile,
        googleFolder,
        googleForm,
        googleFusionTable,
        googleMap,
        googlePhoto,
        googlePresentation,
        googleScript,
        googleShortcut,
        googleSite,
        googleSpreadsheet,
        googleUnknown,
        googleVideo,
        msExcel,
        msPowerpoint,
        json,
        epubZip,
        javaArchive,
        javascript,
        oggExecutable,
        msWord,
        pdf,
        rtf,
        xml,
        xhtmlXml,
        zip,
        octetStream,
        abiword,
        bzip,
        bzip2,
        csh,
        rarCompressed,
        shell,
        tar,
        sevenZipCompressed,
        aac,
        midi,
        oggAudio,
        xWav,
        webmAudio,
        openFont,
        woff,
        woff2,
        trueTypeFont,
        gif,
        icon,
        jpeg,
        svgXml,
        tiff,
        webp,
        css,
        csv,
        html,
        calendar,
        webmVideo,
        gpp,
        gpp2,
        msVideo,
        mpeg,
        oggVideo,
      ).toImmutableList()
    }

    fun from(file: DriveFile?): MimeType =
      if (file == null || file.mimeType == null) Invalid else fromString(file.mimeType)

    fun from(input: String?): MimeType = fromString(input)

    fun fromString(input: String?): MimeType = when (input) {
      "application/json" -> MimeCategory.Application.Json
      "application/epub+zip" -> MimeCategory.Application.EpubZip
      "application/java-archive" -> MimeCategory.Application.JavaArchive
      "application/javascript" -> MimeCategory.Application.Javascript
      "application/ogg" -> MimeCategory.Application.Ogg
      "application/msword" -> MimeCategory.Application.MsWord
      "application/pdf" -> MimeCategory.Application.Pdf
      "application/rtf" -> MimeCategory.Application.Rtf
      "application/xml" -> MimeCategory.Application.Xml
      "application/xhtml+xml" -> MimeCategory.Application.XhtmlXml
      "application/zip" -> MimeCategory.Application.Zip
      "application/octet-stream" -> MimeCategory.Application.OctetStream
      "application/x-7z-compressed" -> MimeCategory.Application.x7zCompressed
      "application/x-abiword" -> MimeCategory.Application.xAbiword
      "application/x-bzip" -> MimeCategory.Application.xBzip
      "application/x-bzip2" -> MimeCategory.Application.xShell
      "application/x-csh" -> MimeCategory.Application.xCsh
      "application/x-rar-compressed" -> MimeCategory.Application.xRarCompressed
      "application/x-sh" -> MimeCategory.Application.xShell
      "application/x-tar" -> MimeCategory.Application.xTar
      "application/vnd.google-apps.audio" -> MimeCategory.Application.Google.Audio
      "application/vnd.google-apps.document" -> MimeCategory.Application.Google.Document
      "application/vnd.google-apps.drive-sdk" -> MimeCategory.Application.Google.DriveSdk
      "application/vnd.google-apps.drawing" -> MimeCategory.Application.Google.Drawing
      "application/vnd.google-apps.file" -> MimeCategory.Application.Google.File
      "application/vnd.google-apps.folder" -> MimeCategory.Application.Google.Folder
      "application/vnd.google-apps.form" -> MimeCategory.Application.Google.Form
      "application/vnd.google-apps.fusiontable" -> MimeCategory.Application.Google.FusionTable
      "application/vnd.google-apps.map" -> MimeCategory.Application.Google.Map
      "application/vnd.google-apps.photo" -> MimeCategory.Application.Google.Photo
      "application/vnd.google-apps.presentation" -> MimeCategory.Application.Google.Presentation
      "application/vnd.google-apps.script" -> MimeCategory.Application.Google.Script
      "application/vnd.google-apps.shortcut" -> MimeCategory.Application.Google.Shortcut
      "application/vnd.google-apps.site" -> MimeCategory.Application.Google.Site
      "application/vnd.google-apps.spreadsheet" -> MimeCategory.Application.Google.Spreadsheet
      "application/vnd.google-apps.unknown" -> MimeCategory.Application.Google.Unknown
      "application/vnd.google-apps.video" -> MimeCategory.Application.Google.Video
      "application/vnd.ms-excel" -> MimeCategory.Application.Microsoft.Excel
      "application/vnd.ms-powerpoint" -> MimeCategory.Application.Microsoft.Powerpoint
      "audio/aac" -> MimeCategory.Audio.Aac
      "audio/midi" -> MimeCategory.Audio.Midi
      "audio/ogg" -> MimeCategory.Audio.Ogg
      "audio/x-wav" -> MimeCategory.Audio.XWav
      "audio/webm" -> MimeCategory.Audio.Webm
      "font/otf" -> MimeCategory.Font.OpenFont
      "font/woff" -> MimeCategory.Font.Woff
      "font/woff2" -> MimeCategory.Font.Woff2
      "font/ttf" -> MimeCategory.Font.TrueTypeFont
      "image/gif" -> MimeCategory.Image.Gif
      "image/x-icon" -> MimeCategory.Image.Icon
      "image/jpeg" -> MimeCategory.Image.Jpeg
      "image/svg+xml" -> MimeCategory.Image.SvgXml
      "image/tiff" -> MimeCategory.Image.Tiff
      "image/webp" -> MimeCategory.Image.Webp
      "text/css" -> MimeCategory.Text.Css
      "text/csv" -> MimeCategory.Text.Csv
      "text/html" -> MimeCategory.Text.Html
      "text/calendar" -> MimeCategory.Text.Calendar
      "video/webm" -> MimeCategory.Video.Webm
      "video/3gpp" -> MimeCategory.Video.Gpp
      "video/3gpp2" -> MimeCategory.Video.Gpp2
      "video/x-msvideo" -> MimeCategory.Video.MsVideo
      "video/mpeg" -> MimeCategory.Video.Mpeg
      "video/ogg" -> MimeCategory.Video.Ogg
      else -> Invalid
    }
  }
}

/**
 * Representation of an invalid mimeType, one that is either unknown, malformed,
 * blank or null.
 */
object Invalid : MimeType("INVALID") {
  override val isFinalType: Boolean = true

  override fun equals(other: Any?): Boolean = other is Invalid
  override fun hashCode(): Int = 0
  override fun toString(): String = "<Invalid MimeType>"
}

/**
 * Base class for all [MimeType] categories.
 */
sealed class MimeCategory(
  val mainType: String,
  val subType: String,
  val categoryName: String
) : MimeType("$mainType/$subType") {
  sealed class Application(subType: String) : MimeCategory("application", subType, "Application") {
    sealed class Google(googleType: String) : Application("vnd.google-apps.$googleType") {
      override fun hasExtension(input: String): Boolean = false

      object Audio : Google("audio") {
        override val isFinalType: Boolean = true
      }

      object Document : Google("document") {
        override val isFinalType: Boolean = true
      }

      object DriveSdk : Google("drive-sdk") {
        override val isFinalType: Boolean = true
      }

      object Drawing : Google("drawing") {
        override val isFinalType: Boolean = true
      }

      object File : Google("file") {
        override val isFinalType: Boolean = true
      }

      object Folder : Google("folder") {
        override val isFinalType: Boolean = true
      }

      object Form : Google("form") {
        override val isFinalType: Boolean = true
      }

      object FusionTable : Google("fusiontable") {
        override val isFinalType: Boolean = true
      }

      object Map : Google("map") {
        override val isFinalType: Boolean = true
      }

      object Photo : Google("photo") {
        override val isFinalType: Boolean = true
      }

      object Presentation : Google("presentation") {
        override val isFinalType: Boolean = true
      }

      object Script : Google("script") {
        override val isFinalType: Boolean = true
      }

      object Shortcut : Google("shortcut") {
        override val isFinalType: Boolean = true
      }

      object Site : Google("site") {
        override val isFinalType: Boolean = true
      }

      object Spreadsheet : Google("spreadsheet") {
        override val isFinalType: Boolean = true
      }

      object Unknown : Google("unknown") {
        override val isFinalType: Boolean = true
      }

      object Video : Google("video") {
        override val isFinalType: Boolean = true
      }
    }

    /**
     * Microsoft Application Mime Types
     */
    sealed class Microsoft(ms: String) : Application("vnd.ms-$ms") {
      /**
       * Excel MimeType
       */
      object Excel : Microsoft("excel") {
        override val exts: List<String> = listOf("xls", "xlsm", "xlsx", "xlsb", "xlt", "xltm")
        override val isFinalType: Boolean = true
      }

      /**
       * Powerpoint MimeType
       */
      object Powerpoint : Microsoft("powerpoint") {
        override val ext: String = "ppt"
        override val isFinalType: Boolean = true
      }
    }

    /**
     * Json MimeType
     */
    object Json : Application("json") {
      override val ext: String = "json"
      override val isFinalType: Boolean = true
    }

    /**
     *  EpubZip MimeType
     */
    object EpubZip : Application("epub+zip") {
      override val ext: String = "epub"
      override val isFinalType: Boolean = true
    }

    /**
     *  JavaArchive MimeType
     */
    object JavaArchive : Application("java-archive") {
      override val ext: String = "jar"
      override val isFinalType: Boolean = true
    }

    /**
     *  Javascript MimeType
     */
    object Javascript : Application("javascript") {
      override val ext: String = "js"
      override val isFinalType: Boolean = true
    }

    /**
     *  Ogg MimeType
     */
    object Ogg : Application("ogg") {
      override val ext: String = "ogx"
      override val isFinalType: Boolean = true
    }

    /**
     *  MsWord MimeType
     */
    object MsWord : Application("msword") {
      override val ext: String = "doc"
      override val isFinalType: Boolean = true
    }

    /**
     *  Pdf MimeType
     */
    object Pdf : Application("pdf") {
      override val ext: String = "pdf"
      override val isFinalType: Boolean = true
    }

    /**
     *  Rtf MimeType
     */
    object Rtf : Application("rtf") {
      override val ext: String = "rtf"
      override val isFinalType: Boolean = true
    }

    /**
     *  Xml MimeType
     */
    object Xml : Application("xml") {
      override val ext: String = "xml"
      override val isFinalType: Boolean = true
    }

    /**
     *  XhtmlXml MimeType
     */
    object XhtmlXml : Application("xhtml+xml") {
      override val ext: String = "xhtml"
      override val isFinalType: Boolean = true
    }

    /**
     *  Zip MimeType
     */
    object Zip : Application("zip") {
      override val ext: String = "zip"
      override val isFinalType: Boolean = true
    }

    /**
     *  OctetStream MimeType
     */
    object OctetStream : Application("octet-stream") {
      override val exts: List<String> = listOf("arc", "bin")
      override val isFinalType: Boolean = true
    }

    /**
     *  x7zCompressed MimeType
     */
    object x7zCompressed : Application("x-7z-compressed") {
      override val ext: String = "7z"
      override val isFinalType: Boolean = true
    }

    /**
     *  xAbiword MimeType
     */
    object xAbiword : Application("x-abiword") {
      override val ext: String = "abw"
      override val isFinalType: Boolean = true
    }

    /**
     *  xBzip MimeType
     */
    object xBzip : Application("x-bzip") {
      override val ext: String = "bz"
      override val isFinalType: Boolean = true
    }

    /**
     *  xBzip2 MimeType
     */
    object xBzip2 : Application("x-bzip2") {
      override val ext: String = "bz2"
      override val isFinalType: Boolean = true
    }

    /**
     *  xCsh MimeType
     */
    object xCsh : Application("x-csh") {
      override val ext: String = "csh"
      override val isFinalType: Boolean = true
    }

    /**
     *  xRarCompressed MimeType
     */
    object xRarCompressed : Application("x-rar-compressed") {
      override val ext: String = "rar"
      override val isFinalType: Boolean = true
    }

    /**
     *  xShell MimeType
     */
    object xShell : Application("x-sh") {
      override val ext: String = "sh"
      override val isFinalType: Boolean = true
    }

    /**
     *  xTar MimeType
     */
    object xTar : Application("x-tar") {
      override val ext: String = "tar"
      override val isFinalType: Boolean = true
    }
  }

  /**
   *  Audio MimeTypes
   */
  sealed class Audio(subType: String) : MimeCategory("audio", subType, "Audio") {
    /**
     *   Aac MimeType
     */
    object Aac : Audio("aac") {
      override val ext: String = "aac"
      override val isFinalType: Boolean = true
    }

    /**
     *   Midi MimeType
     */
    object Midi : Audio("midi") {
      override val exts: List<String> = listOf("mid", "midi")
      override val isFinalType: Boolean = true
    }

    /**
     *   Ogg MimeType
     */
    object Ogg : Audio("ogg") {
      override val ext: String = "oga"
      override val isFinalType: Boolean = true
    }

    /**
     *   XWav MimeType
     */
    object XWav : Audio("x-wav") {
      override val ext: String = "wav"
      override val isFinalType: Boolean = true
    }

    /**
     *   Webm MimeType
     */
    object Webm : Audio("webm") {
      override val ext: String = "weba"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Font MimeTypes
   */
  sealed class Font(subType: String) : MimeCategory("font", subType, "Font") {
    /**
     * OpenFont MimeTypes
     */
    object OpenFont : Font("oft") {
      override val ext: String = "oft"
      override val isFinalType: Boolean = true
    }

    /**
     * Woff MimeTypes
     */
    object Woff : Font("woff") {
      override val ext: String = "woff"
      override val isFinalType: Boolean = true
    }

    /**
     * Woff2 MimeTypes
     */
    object Woff2 : Font("woff2") {
      override val ext: String = "woff2"
      override val isFinalType: Boolean = true
    }

    /**
     * TrueTypeFont MimeTypes
     */
    object TrueTypeFont : Font("ttf") {
      override val ext: String = "ttf"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Image MimeTypes
   */
  sealed class Image(subType: String) : MimeCategory("image", subType, "Image") {
    object Gif : Image("gif") {
      override val ext: String = "gif"
      override val isFinalType: Boolean = true
    }

    object Icon : Image("x-icon") {
      override val ext: String = "icon"
      override val isFinalType: Boolean = true
    }

    object Jpeg : Image("jpeg") {
      override val exts: List<String> = listOf("jpg", "jpeg")
      override val isFinalType: Boolean = true
    }

    object SvgXml : Image("svg+xml") {
      override val ext: String = "svg"
      override val isFinalType: Boolean = true
    }

    object Tiff : Image("tiff") {
      override val exts: List<String> = listOf("tif", "tiff")
      override val isFinalType: Boolean = true
    }

    object Webp : Image("webp") {
      override val ext: String = "webp"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Text MimeTypes
   */
  sealed class Text(subType: String) : MimeCategory("text", subType, "Text") {
    object Css : Text("css") {
      override val ext: String = "css"
      override val isFinalType: Boolean = true
    }

    object Csv : Text("csv") {
      override val ext: String = "csv"
      override val isFinalType: Boolean = true
    }

    object Html : Text("html") {
      override val exts: List<String> = listOf("htm", "html")
      override val isFinalType: Boolean = true
    }

    object Calendar : Text("calendar") {
      override val ext: String = "ics"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Video MimeTypes
   */
  sealed class Video(subType: String) : MimeCategory("video", subType, "Video") {
    object Webm : Video("webm") {
      override val ext: String = "webm"
      override val isFinalType: Boolean = true
    }

    object Gpp : Video("3gpp") {
      override val ext: String = "3gp"
      override val isFinalType: Boolean = true
    }

    object Gpp2 : Video("3gpp2") {
      override val ext: String = "3g2"
      override val isFinalType: Boolean = true
    }

    object MsVideo : Video("x-msvideo") {
      override val ext: String = "avi"
      override val isFinalType: Boolean = true
    }

    object Mpeg : Video("mpeg") {
      override val exts: List<String> = listOf("mpg", "mpeg")
      override val isFinalType: Boolean = true
    }

    object Ogg : Video("ogg") {
      override val ext: String = "ogg"
      override val isFinalType: Boolean = true
    }
  }
}


/**
 * Converts this value [String] value to a [MimeType] object. Will return [MimeType.invalid]
 * if the [String] doesn't match any defined [MimeType] value.
 */
fun String.asMimeType(): MimeType = MimeType.from(this)

/**
 * Whether this [DriveFile][com.google.api.services.drive.model.File] is a Google Drive [Folder][MimeType.googleFolder].
 * This will return false if the [com.google.api.services.drive.model.File.mimeType] was not fetched from
 * the Drive Service.
 * #### I.e. this.mimeType == "application/vnd.google-apps.document"
 */
fun DriveFile.isDriveFolder(): Boolean = this.mimeType != null && this.mimeType == MimeType.googleFolder.mime

/**
 * Whether this [DriveFile][com.google.api.services.drive.model.File] is a Google Drive [Shortcut][MimeType.googleShortcut].
 * This will return false if the [com.google.api.services.drive.model.File.mimeType] was not fetched from
 * the Drive Service.
 * #### I.e. this.mimeType == "application/vnd.google-apps.shortcut"
 */
fun DriveFile.isDriveShortcut(): Boolean = this.mimeType != null && this.mimeType == MimeType.googleShortcut.mime

/**
 * This function returns true if this [DriveFile][com.google.api.services.drive.model.File] is ***NOT*** a
 * [Folder][MimeType.googleFolder], a [Shortcut][MimeType.googleShortcut], or [Invalid][MimeType.invalid].
 */
fun DriveFile.isDriveFile(): Boolean = this.mimeType != null && this.mimeType.let {
  it != MimeType.googleDocument.mime && it != MimeType.googleShortcut.mime && it != MimeType.invalid.mime
}

