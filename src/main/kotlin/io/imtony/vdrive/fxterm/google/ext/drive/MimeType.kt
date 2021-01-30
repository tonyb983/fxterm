@file:Suppress(
  "ClassName",
  "StringLiteralDuplication",
  "MemberVisibilityCanBePrivate",
  "unused",
  "EndOfSentenceFormat"
)

package io.imtony.vdrive.fxterm.google.ext.drive

import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.toImmutableList
import org.apache.http.Consts
import org.apache.http.entity.ContentType
import java.nio.charset.Charset
import com.google.api.services.drive.model.File as DriveFile

/**
 * Represents a mimeType [String].
 *
 * @property mime The actual mime [String].
 */
sealed class MimeType(
  /**
   * The actual mime [String].
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
   * Converts this [MimeType] to a [String] usable in a
   * [Drive.Files.List.q][com.google.api.services.drive.Drive.Files.List.q]
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

  /**
   * Uses this [MimeType] with [ContentType.create] and the given [charset].
   */
  fun createContentType(charset: Charset = Consts.ISO_8859_1): ContentType = ContentType.create(this.mime, charset)

  /**
   * Uses this [MimeType] with [ContentType.getByMimeType].
   */
  fun getContentType(): ContentType = ContentType.getByMimeType(this.mime)

  companion object {

    /** */
    val invalid: MimeType by lazy { Invalid }

    /** application/vnd.google-apps.audio */
    val googleAudio: MimeType by lazy { MimeCategory.Application.Google.Audio }

    /** application/vnd.google-apps.document */
    val googleDocument: MimeType by lazy { MimeCategory.Application.Google.Document }

    /** application/vnd.google-apps.drive-sdk */
    val googleDriveSdk: MimeType by lazy { MimeCategory.Application.Google.DriveSdk }

    /** application/vnd.google-apps.drawing */
    val googleDrawing: MimeType by lazy { MimeCategory.Application.Google.Drawing }

    /** application/vnd.google-apps.file */
    val googleFile: MimeType by lazy { MimeCategory.Application.Google.File }

    /** application/vnd.google-apps.folder */
    val googleFolder: MimeType by lazy { MimeCategory.Application.Google.Folder }

    /** application/vnd.google-apps.form */
    val googleForm: MimeType by lazy { MimeCategory.Application.Google.Form }

    /** application/vnd.google-apps.fusiontable */
    val googleFusionTable: MimeType by lazy {
      MimeCategory.Application.Google.FusionTable
    }

    /** application/vnd.google-apps.map */
    val googleMap: MimeType by lazy { MimeCategory.Application.Google.Map }

    /** application/vnd.google-apps.photo */
    val googlePhoto: MimeType by lazy { MimeCategory.Application.Google.Photo }

    /** application/vnd.google-apps.presentation */
    val googlePresentation: MimeType by lazy {
      MimeCategory.Application.Google.Presentation
    }

    /** application/vnd.google-apps.script */
    val googleScript: MimeType by lazy { MimeCategory.Application.Google.Script }

    /** application/vnd.google-apps.shortcut */
    val googleShortcut: MimeType by lazy { MimeCategory.Application.Google.Shortcut }

    /** application/vnd.google-apps.site */
    val googleSite: MimeType by lazy { MimeCategory.Application.Google.Site }

    /** application/vnd.google-apps.spreadsheet */
    val googleSpreadsheet: MimeType by lazy {
      MimeCategory.Application.Google.Spreadsheet
    }

    /** application/vnd.google-apps.unknown */
    val googleUnknown: MimeType by lazy { MimeCategory.Application.Google.Unknown }

    /** application/vnd.google-apps.video */
    val googleVideo: MimeType by lazy { MimeCategory.Application.Google.Video }

    /** application/vnd.ms-excel */
    val msExcel: MimeType by lazy { MimeCategory.Application.Microsoft.Excel }

    /** application/vnd.ms-powerpoint */
    val msPowerpoint: MimeType by lazy {
      MimeCategory.Application.Microsoft.Powerpoint
    }

    /** application/vnd.openxmlformats-officedocument.wordprocessingml.document */
    val openWordDocument: MimeType by lazy {
      MimeCategory.Application.OpenWordDocument
    }

    /** application/vnd.openxmlformats-officedocument.spreadsheetml.sheet */
    val openExcelDocument: MimeType by lazy {
      MimeCategory.Application.OpenExcelDocument
    }

    /** application/vnd.openxmlformats-officedocument.presentationml.presentation */
    val openPowerpointDocument: MimeType by lazy {
      MimeCategory.Application.OpenPowerpointDocument
    }

    /** application/vnd.oasis.opendocument.document */
    val openOfficeDoc: MimeType by lazy {
      MimeCategory.Application.OpenOfficeDocument
    }

    /** application/x-vnd.oasis.opendocument.spreadsheet */
    val openOfficeSheet: MimeType by lazy {
      MimeCategory.Application.OpenOfficeSpreadsheet
    }

    /** application/vnd.oasis.opendocument.presentation */
    val openOfficePresentation: MimeType by lazy {
      MimeCategory.Application.OpenOfficePresentation
    }

    /** application/json */
    val json: MimeType by lazy { MimeCategory.Application.Json }

    /** application/epub+zip */
    val epubZip: MimeType by lazy { MimeCategory.Application.EpubZip }

    /** application/java-archive */
    val javaArchive: MimeType by lazy { MimeCategory.Application.JavaArchive }

    /** application/javascript */
    val javascript: MimeType by lazy { MimeCategory.Application.Javascript }

    /** application/ogg */
    val oggExecutable: MimeType by lazy { MimeCategory.Application.Ogg }

    /** application/msword */
    val msWord: MimeType by lazy { MimeCategory.Application.MsWord }

    /** application/pdf */
    val pdf: MimeType by lazy { MimeCategory.Application.Pdf }

    /** application/rtf */
    val rtf: MimeType by lazy { MimeCategory.Application.Rtf }

    /** application/xml */
    val xml: MimeType by lazy { MimeCategory.Application.Xml }

    /** application/xhtml+xml */
    val xhtmlXml: MimeType by lazy { MimeCategory.Application.XhtmlXml }

    /** application/zip */
    val zip: MimeType by lazy { MimeCategory.Application.Zip }

    /** application/octet-stream */
    val octetStream: MimeType by lazy { MimeCategory.Application.OctetStream }

    /** application/x-abiword */
    val abiword: MimeType by lazy { MimeCategory.Application.xAbiword }

    /** application/x-bzip */
    val bzip: MimeType by lazy { MimeCategory.Application.xBzip }

    /** application/x-bzip2 */
    val bzip2: MimeType by lazy { MimeCategory.Application.xBzip2 }

    /** application/x-csh */
    val csh: MimeType by lazy { MimeCategory.Application.xCsh }

    /** application/x-rar-compressed */
    val rarCompressed: MimeType by lazy { MimeCategory.Application.xRarCompressed }

    /** application/x-sh */
    val shell: MimeType by lazy { MimeCategory.Application.xShell }

    /** application/x-tar */
    val tar: MimeType by lazy { MimeCategory.Application.xTar }

    /** application/x-7z-compressed */
    val sevenZipCompressed: MimeType by lazy { MimeCategory.Application.x7zCompressed }

    /** audio/aac */
    val aac: MimeType by lazy { MimeCategory.Audio.Aac }

    /** audio/midi */
    val midi: MimeType by lazy { MimeCategory.Audio.Midi }

    /** audio/ogg */
    val oggAudio: MimeType by lazy { MimeCategory.Audio.Ogg }

    /** audio/x-wav */
    val xWav: MimeType by lazy { MimeCategory.Audio.XWav }

    /** audio/webm */
    val webmAudio: MimeType by lazy { MimeCategory.Audio.Webm }

    /** font/otf */
    val openFont: MimeType by lazy { MimeCategory.Font.OpenFont }

    /** font/woff */
    val woff: MimeType by lazy { MimeCategory.Font.Woff }

    /** font/woff2 */
    val woff2: MimeType by lazy { MimeCategory.Font.Woff2 }

    /** font/ttf */
    val trueTypeFont: MimeType by lazy { MimeCategory.Font.TrueTypeFont }

    /** image/gif */
    val gif: MimeType by lazy { MimeCategory.Image.Gif }

    /** image/x-icon */
    val icon: MimeType by lazy { MimeCategory.Image.Icon }

    /** image/jpeg */
    val jpeg: MimeType by lazy { MimeCategory.Image.Jpeg }

    /** image/svg+xml */
    val svgXml: MimeType by lazy { MimeCategory.Image.SvgXml }

    /** image/tiff */
    val tiff: MimeType by lazy { MimeCategory.Image.Tiff }

    /** image/webp */
    val webp: MimeType by lazy { MimeCategory.Image.Webp }

    /** text/plain */
    val plain: MimeType by lazy { MimeCategory.Text.Plain }

    /** text/css */
    val css: MimeType by lazy { MimeCategory.Text.Css }

    /** text/csv */
    val csv: MimeType by lazy { MimeCategory.Text.Csv }

    /** text/tab-separated-values */
    val tsv: MimeType by lazy { MimeCategory.Text.Tsv }

    /** text/html */
    val html: MimeType by lazy { MimeCategory.Text.Html }

    /** text/calendar */
    val calendar: MimeType by lazy { MimeCategory.Text.Calendar }

    /** video/webm */
    val webmVideo: MimeType by lazy { MimeCategory.Video.Webm }

    /** video/3gpp */
    val gpp: MimeType by lazy { MimeCategory.Video.Gpp }

    /** video/3gpp2 */
    val gpp2: MimeType by lazy { MimeCategory.Video.Gpp2 }

    /** video/x-msvideo */
    val msVideo: MimeType by lazy { MimeCategory.Video.MsVideo }

    /** video/mpeg */
    val mpeg: MimeType by lazy { MimeCategory.Video.Mpeg }

    /** video/ogg */
    val oggVideo: MimeType by lazy { MimeCategory.Video.Ogg }

    /** All of the existing mime types. */
    val allMimes: ImmutableCollection<MimeType> by lazy {
      listOf(
        invalid,
        googleAudio,
        googleDocument,
        googleDriveSdk,
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
        openWordDocument,
        openExcelDocument,
        openPowerpointDocument,
        openOfficePresentation,
        openOfficeDoc,
        openOfficeSheet,
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
        plain,
        css,
        csv,
        tsv,
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

    /**
     * Gets the [DriveFile.mimeType] from this [file] and converts it to a [MimeType].
     */
    fun from(file: DriveFile?): MimeType =
      if (file == null || file.mimeType == null) Invalid else fromString(file.mimeType)

    /**
     * Converts this [input] [String] to a [MimeType]. Null or empty or unknown strings will become [Invalid].
     */
    fun from(input: String?): MimeType = fromString(input)

    /**
     * Converts this [input] [String] to a [MimeType]. Null or empty or unknown strings will become [Invalid].
     */
    @Suppress("LongMethod")
    fun fromString(input: String?): MimeType = when (input) {
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
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> openWordDocument
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> openExcelDocument
      "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> openPowerpointDocument
      "application/vnd.oasis.opendocument.document" -> openOfficeDoc
      "application/x-vnd.oasis.opendocument.spreadsheet" -> openOfficeSheet
      "application/vnd.oasis.opendocument.presentation" -> openOfficePresentation
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
      "application/x-abiword" -> MimeCategory.Application.xAbiword
      "application/x-bzip" -> MimeCategory.Application.xBzip
      "application/x-bzip2" -> MimeCategory.Application.xShell
      "application/x-csh" -> MimeCategory.Application.xCsh
      "application/x-rar-compressed" -> MimeCategory.Application.xRarCompressed
      "application/x-sh" -> MimeCategory.Application.xShell
      "application/x-tar" -> MimeCategory.Application.xTar
      "application/x-7z-compressed" -> MimeCategory.Application.x7zCompressed
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
      "text/plain" -> MimeCategory.Text.Plain
      "text/css" -> MimeCategory.Text.Css
      "text/csv" -> MimeCategory.Text.Csv
      "text/html" -> MimeCategory.Text.Html
      "text/calendar" -> MimeCategory.Text.Calendar
      "text/tab-separated-values" -> MimeCategory.Text.Tsv
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
 *
 * @property categoryName The formal name of this category.
 */
sealed class MimeCategory(
  /**
   * The main type for this mimeType.
   */
  mainType: String,
  /**
   * The sub type for this mimeType.
   */
  subType: String,
  /**
   * The *formal* name for this category, i.e. Application, Audio, Font, etc.
   */
  val categoryName: String
) : MimeType("$mainType/$subType") {
  /**
   *  Application MimeTypes.
   */
  sealed class Application(subType: String) : MimeCategory("application", subType, "Application") {
    /**
     *  Google MimeTypes.
     */
    sealed class Google(googleType: String) : Application("vnd.google-apps.$googleType") {
      override fun hasExtension(input: String): Boolean = false

      /**
       *  GoogleAudio MimeType.
       */
      object Audio : Google("audio") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Document MimeType.
       */
      object Document : Google("document") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google DriveSdk MimeType.
       */
      object DriveSdk : Google("drive-sdk") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Drawing MimeType.
       */
      object Drawing : Google("drawing") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google File MimeType.
       */
      object File : Google("file") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Folder MimeType.
       */
      object Folder : Google("folder") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Form MimeType.
       */
      object Form : Google("form") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google FusionTable MimeType.
       */
      object FusionTable : Google("fusiontable") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Map MimeType.
       */
      object Map : Google("map") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Photo MimeType.
       */
      object Photo : Google("photo") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Presentation MimeType.
       */
      object Presentation : Google("presentation") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Script MimeType.
       */
      object Script : Google("script") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Shortcut MimeType.
       */
      object Shortcut : Google("shortcut") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Site MimeType.
       */
      object Site : Google("site") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Sheets MimeType.
       */
      object Spreadsheet : Google("spreadsheet") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Unknown Google MimeType.
       */
      object Unknown : Google("unknown") {
        override val isFinalType: Boolean = true
      }

      /**
       *  Google Video MimeType.
       */
      object Video : Google("video") {
        override val isFinalType: Boolean = true
      }
    }

    /**
     * Microsoft Application Mime Types.
     */
    sealed class Microsoft(ms: String) : Application("vnd.ms-$ms") {
      /**
       * Excel MimeType.
       */
      object Excel : Microsoft("excel") {
        override val exts: List<String> = listOf("xls", "xlsm", "xlsx", "xlsb", "xlt", "xltm")
        override val isFinalType: Boolean = true
      }

      /**
       * Powerpoint MimeType.
       */
      object Powerpoint : Microsoft("powerpoint") {
        override val ext: String = "ppt"
        override val isFinalType: Boolean = true
      }
    }

    /**
     * The OpenOffice Document format.
     */
    object OpenOfficeDocument : Application("vnd.oasis.opendocument.text") {
      override val isFinalType: Boolean = true
    }

    /**
     * The OpenOffice Spreadsheet format.
     */
    object OpenOfficeSpreadsheet : Application("x-vnd.oasis.opendocument.spreadsheet") {
      override val isFinalType: Boolean = true
    }

    /**
     * The OpenOffice Presentation format.
     */
    object OpenOfficePresentation : Application(
      "vnd.oasis.opendocument.presentation"
    ) {
      override val isFinalType: Boolean = true
    }

    /**
     * The OpenDocument version of a Microsoft Word document.
     */
    object OpenWordDocument : Application("vnd.openxmlformats-officedocument.wordprocessingml.document") {
      override val isFinalType: Boolean = true
    }

    /**
     * The OpenDocument version of a Microsoft Excel document.
     */
    object OpenExcelDocument : Application("vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
      override val isFinalType: Boolean = true
    }

    /**
     * The OpenDocument version of a Microsoft Powerpoint document.
     */
    object OpenPowerpointDocument : Application(
      "vnd.openxmlformats-officedocument.presentationml.presentation"
    ) {
      override val isFinalType: Boolean = true
    }

    /**
     * Json MimeType.
     */
    object Json : Application("json") {
      override val ext: String = "json"
      override val isFinalType: Boolean = true
    }

    /**
     *  EpubZip MimeType.
     */
    object EpubZip : Application("epub+zip") {
      override val ext: String = "epub"
      override val isFinalType: Boolean = true
    }

    /**
     *  JavaArchive MimeType.
     */
    object JavaArchive : Application("java-archive") {
      override val ext: String = "jar"
      override val isFinalType: Boolean = true
    }

    /**
     *  Javascript MimeType.
     */
    object Javascript : Application("javascript") {
      override val ext: String = "js"
      override val isFinalType: Boolean = true
    }

    /**
     *  Ogg MimeType.
     */
    object Ogg : Application("ogg") {
      override val ext: String = "ogx"
      override val isFinalType: Boolean = true
    }

    /**
     *  MsWord MimeType.
     */
    object MsWord : Application("msword") {
      override val ext: String = "doc"
      override val isFinalType: Boolean = true
    }

    /**
     *  Pdf MimeType.
     */
    object Pdf : Application("pdf") {
      override val ext: String = "pdf"
      override val isFinalType: Boolean = true
    }

    /**
     *  Rtf MimeType.
     */
    object Rtf : Application("rtf") {
      override val ext: String = "rtf"
      override val isFinalType: Boolean = true
    }

    /**
     *  Xml MimeType.
     */
    object Xml : Application("xml") {
      override val ext: String = "xml"
      override val isFinalType: Boolean = true
    }

    /**
     *  XhtmlXml MimeType.
     */
    object XhtmlXml : Application("xhtml+xml") {
      override val ext: String = "xhtml"
      override val isFinalType: Boolean = true
    }

    /**
     *  Zip MimeType.
     */
    object Zip : Application("zip") {
      override val ext: String = "zip"
      override val isFinalType: Boolean = true
    }

    /**
     *  OctetStream MimeType.
     */
    object OctetStream : Application("octet-stream") {
      override val exts: List<String> = listOf("arc", "bin")
      override val isFinalType: Boolean = true
    }

    /**
     *  x7zCompressed MimeType.
     */
    object x7zCompressed : Application("x-7z-compressed") {
      override val ext: String = "7z"
      override val isFinalType: Boolean = true
    }

    /**
     *  xAbiword MimeType.
     */
    object xAbiword : Application("x-abiword") {
      override val ext: String = "abw"
      override val isFinalType: Boolean = true
    }

    /**
     *  xBzip MimeType.
     */
    object xBzip : Application("x-bzip") {
      override val ext: String = "bz"
      override val isFinalType: Boolean = true
    }

    /**
     *  xBzip2 MimeType.
     */
    object xBzip2 : Application("x-bzip2") {
      override val ext: String = "bz2"
      override val isFinalType: Boolean = true
    }

    /**
     *  xCsh MimeType.
     */
    object xCsh : Application("x-csh") {
      override val ext: String = "csh"
      override val isFinalType: Boolean = true
    }

    /**
     *  xRarCompressed MimeType.
     */
    object xRarCompressed : Application("x-rar-compressed") {
      override val ext: String = "rar"
      override val isFinalType: Boolean = true
    }

    /**
     *  xShell MimeType.
     */
    object xShell : Application("x-sh") {
      override val ext: String = "sh"
      override val isFinalType: Boolean = true
    }

    /**
     *  xTar MimeType.
     */
    object xTar : Application("x-tar") {
      override val ext: String = "tar"
      override val isFinalType: Boolean = true
    }
  }

  /**
   *  Audio MimeTypes.
   */
  sealed class Audio(subType: String) : MimeCategory("audio", subType, "Audio") {
    /**
     *   Aac MimeType.
     */
    object Aac : Audio("aac") {
      override val ext: String = "aac"
      override val isFinalType: Boolean = true
    }

    /**
     *   Midi MimeType.
     */
    object Midi : Audio("midi") {
      override val exts: List<String> = listOf("mid", "midi")
      override val isFinalType: Boolean = true
    }

    /**
     *   Ogg MimeType.
     */
    object Ogg : Audio("ogg") {
      override val ext: String = "oga"
      override val isFinalType: Boolean = true
    }

    /**
     *   XWav MimeType.
     */
    object XWav : Audio("x-wav") {
      override val ext: String = "wav"
      override val isFinalType: Boolean = true
    }

    /**
     *   Webm MimeType.
     */
    object Webm : Audio("webm") {
      override val ext: String = "weba"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Font MimeTypes.
   */
  sealed class Font(subType: String) : MimeCategory("font", subType, "Font") {
    /**
     * OpenFont MimeTypes.
     */
    object OpenFont : Font("oft") {
      override val ext: String = "oft"
      override val isFinalType: Boolean = true
    }

    /**
     * Woff MimeTypes.
     */
    object Woff : Font("woff") {
      override val ext: String = "woff"
      override val isFinalType: Boolean = true
    }

    /**
     * Woff2 MimeTypes.
     */
    object Woff2 : Font("woff2") {
      override val ext: String = "woff2"
      override val isFinalType: Boolean = true
    }

    /**
     * TrueTypeFont MimeTypes.
     */
    object TrueTypeFont : Font("ttf") {
      override val ext: String = "ttf"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Image MimeTypes.
   */
  sealed class Image(subType: String) : MimeCategory("image", subType, "Image") {
    /**
     *  Gif MimeType.
     */
    object Gif : Image("gif") {
      override val ext: String = "gif"
      override val isFinalType: Boolean = true
    }

    /**
     *  Icon MimeType.
     */
    object Icon : Image("x-icon") {
      override val ext: String = "icon"
      override val isFinalType: Boolean = true
    }

    /**
     *  Jpeg MimeType.
     */
    object Jpeg : Image("jpeg") {
      override val exts: List<String> = listOf("jpg", "jpeg")
      override val isFinalType: Boolean = true
    }

    /**
     *  SvgXml MimeType.
     */
    object SvgXml : Image("svg+xml") {
      override val ext: String = "svg"
      override val isFinalType: Boolean = true
    }

    /**
     *  Tiff MimeType.
     */
    object Tiff : Image("tiff") {
      override val exts: List<String> = listOf("tif", "tiff")
      override val isFinalType: Boolean = true
    }

    /**
     *  Webp MimeType.
     */
    object Webp : Image("webp") {
      override val ext: String = "webp"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Text MimeTypes.
   */
  sealed class Text(subType: String) : MimeCategory("text", subType, "Text") {
    /**
     *  Css MimeType.
     */
    object Css : Text("css") {
      override val ext: String = "css"
      override val isFinalType: Boolean = true
    }

    /**
     *  Csv MimeType.
     */
    object Csv : Text("csv") {
      override val ext: String = "csv"
      override val isFinalType: Boolean = true
    }

    /**
     * Tab Separated Value MimeType.
     */
    object Tsv : Text("tab-separated-values") {
      override val ext: String = "tsv"
      override val isFinalType: Boolean = true
    }

    /**
     *  Html MimeType.
     */
    object Html : Text("html") {
      override val exts: List<String> = listOf("htm", "html")
      override val isFinalType: Boolean = true
    }

    /**
     *  Calendar MimeType.
     */
    object Calendar : Text("calendar") {
      override val ext: String = "ics"
      override val isFinalType: Boolean = true
    }

    /**
     *  Plain Text MimeType.
     */
    object Plain : Text("plain") {
      override val ext: String = "txt"
      override val isFinalType: Boolean = true
    }
  }

  /**
   * Video MimeTypes.
   */
  sealed class Video(subType: String) : MimeCategory("video", subType, "Video") {
    /**
     *  Webm MimeType.
     */
    object Webm : Video("webm") {
      override val ext: String = "webm"
      override val isFinalType: Boolean = true
    }

    /**
     *  Gpp MimeType.
     */
    object Gpp : Video("3gpp") {
      override val ext: String = "3gp"
      override val isFinalType: Boolean = true
    }

    /**
     *  Gpp2 MimeType.
     */
    object Gpp2 : Video("3gpp2") {
      override val ext: String = "3g2"
      override val isFinalType: Boolean = true
    }

    /**
     *  MsVideo MimeType.
     */
    object MsVideo : Video("x-msvideo") {
      override val ext: String = "avi"
      override val isFinalType: Boolean = true
    }

    /**
     *  Mpeg MimeType.
     */
    object Mpeg : Video("mpeg") {
      override val exts: List<String> = listOf("mpg", "mpeg")
      override val isFinalType: Boolean = true
    }

    /**
     *  Ogg MimeType.
     */
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
 * Whether this [DriveFile][com.google.api.services.drive.model.File] is a Google
 * Drive [Shortcut][MimeType.googleShortcut].
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
