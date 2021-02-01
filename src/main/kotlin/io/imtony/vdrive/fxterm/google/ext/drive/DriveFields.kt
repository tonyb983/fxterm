package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveRequest
import com.google.api.services.drive.model.About
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.User
import mu.KLogging

interface GeneralBuilder {
  val finalValue: String
}

interface DriveRequestFieldsBuilder : GeneralBuilder, AutoCloseable {
  fun addQuery(input: String?): Boolean
}

interface GenericFieldBuilder<TTarget, TThis : GenericFieldBuilder<TTarget, TThis>> : DriveRequestFieldsBuilder {
  fun addFields(block: TThis.() -> Unit): Unit
}

open class BaseFieldBuilder<TTarget, TBuilder : BaseFieldBuilder<TTarget, TBuilder>>(
  protected val targetClass: Class<TTarget>,
  protected val builderClass: Class<TBuilder>,
  protected val groupPrefix: String? = null,
  protected val owner: DriveRequestFieldsBuilder? = null,
) :
  GenericFieldBuilder<TTarget, TBuilder> {
  constructor(
    targetClass: Class<TTarget>,
    builderClass: Class<TBuilder>,
    groupPrefix: String? = null,
    owner: DriveRequestFieldsBuilder? = null,
    block: TBuilder.() -> Unit
  ) : this(targetClass, builderClass, groupPrefix, owner) {
    addFields(block)
  }

  protected val queries: MutableSet<String> = mutableSetOf()

  override val finalValue: String
    get() = if (queries.isEmpty()) "" else {
      if (groupPrefix != null) {
        "$groupPrefix(${queries.joinToString(",")})"
      } else {
        queries.joinToString(",")
      }
    }

  override fun addFields(block: TBuilder.() -> Unit) {
    block.invoke(this as? TBuilder ?: throw TypeCastException("'this' cannot be cast to type ${builderClass.simpleName}"))
  }

  override fun addQuery(input: String?): Boolean = if (input.isNullOrBlank()) {
    false
  } else {
    queries.add(input)
  }

  protected var isClosed = false

  override fun close() {
    val alreadyClosed = !isClosed
    val queriesEmpty = queries.isEmpty()
    val ownerNull = owner == null

    if (alreadyClosed || queriesEmpty || ownerNull) {
      logger.warn { "Warning, unable to run close. alreadyClosed: $alreadyClosed queriesEmpty: $queriesEmpty ownerNull: $ownerNull" }
      return
    }

    owner!!.addQuery(finalValue)
    isClosed = true
  }

  companion object : KLogging()
}

class DriveFilesListFieldBuilder(
  owner: DriveRequestFieldsBuilder? = null,
) : BaseFieldBuilder<FileList, DriveFilesListFieldBuilder>(
  FileList::class.java,
  DriveFilesListFieldBuilder::class.java,
  null,
  owner,
) {
  constructor(
    owner: DriveRequestFieldsBuilder? = null,
    block: DriveFilesListFieldBuilder.() -> Unit
  ) : this(owner) {
    addFields(block)
  }

  companion object : KLogging()
}


inline fun <reified TModel, reified TBuilder : BaseFieldBuilder<TModel, TBuilder>> fieldBuilderFactory(
  owner: DriveRequestFieldsBuilder? = null,
  groupPrefix: String? = null,
  noinline block: TBuilder.() -> Unit
): TBuilder? = when {
  TModel::class.java == Drive.Files.List::class.java &&
    TBuilder::class.java == DriveFilesListRequestFieldsBuilder::class.java -> DriveFilesListRequestFieldsBuilder(
    owner
  ) as TBuilder
  TModel::class.java == File::class.java &&
    TBuilder::class.java == DriveFilesListRequestFieldsBuilder::class.java -> DriveFileFieldBuilder(
    owner
  ) as TBuilder

  else -> null
}

inline fun <reified TModel, reified TBuilder : BaseFieldBuilder<TModel, TBuilder>> fieldBuilderFactory(
): TBuilder? = when {
  TModel::class.java == Drive.Files.List::class.java -> DriveFilesListRequestFieldsBuilder() as TBuilder
  TModel::class.java == File::class.java -> DriveFileFieldBuilder() as TBuilder

  else -> null
}

class DriveFilesListRequestFieldsBuilder(
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<Drive.Files.List, DriveFilesListRequestFieldsBuilder>(
  Drive.Files.List::class.java,
  DriveFilesListRequestFieldsBuilder::class.java,
  null,
  owner,
) {
  val nextPageToken: Boolean get() = addQuery("nextPageToken")

  fun maxResults(max: Int): Boolean {
    if (max < 1 || max > 1000) {
      logger.warn { "MaxResults can only be set to a value between 1 and 1000 (inclusive). Max: $max" }
    }

    return addQuery("maxResults=${max.coerceIn(1, 1000)}")
  }

  fun files(block: DriveFileFieldBuilder.() -> Unit = {}) {
    DriveFileFieldBuilder(this).use(block)
  }

  companion object : KLogging()
}

class DriveFileFieldBuilder(
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<File, DriveFileFieldBuilder>(
  File::class.java,
  DriveFileFieldBuilder::class.java,
  "files",
  owner
) {
  val id: Boolean get() = addQuery("id")
  val name: Boolean get() = addQuery("name")
  val mimeType: Boolean get() = addQuery("mimeType")
  val description: Boolean get() = addQuery("description")
  val starred: Boolean get() = addQuery("starred")
  val trashed: Boolean get() = addQuery("trashed")
  val explicitlyTrashed: Boolean get() = addQuery("explicitlyTrashed")

  fun trashingUser(block: DriveUserFieldBuilder.() -> Unit) {
    DriveUserFieldBuilder("trashingUser", this).use(block)
  }

  val trashedTime: Boolean get() = addQuery("trashedTime")
  val parents: Boolean get() = addQuery("parents")
  val properties: Boolean get() = addQuery("properties")
  val appProperties: Boolean get() = addQuery("appProperties")
  val spaces: Boolean get() = addQuery("spaces")
  val version: Boolean get() = addQuery("version")
  val webContentLink: Boolean get() = addQuery("webContentLink")
  val webViewLink: Boolean get() = addQuery("webViewLink")
  val iconLink: Boolean get() = addQuery("iconLink")
  val hasThumbnail: Boolean get() = addQuery("hasThumbnail")
  val thumbnailLink: Boolean get() = addQuery("thumbnailLink")
  val thumbnailVersion: Boolean get() = addQuery("thumbnailVersion")
  val viewedByMe: Boolean get() = addQuery("viewedByMe")
  val viewedByMeTime: Boolean get() = addQuery("viewedByMeTime")
  val createdTime: Boolean get() = addQuery("createdTime")
  val modifiedTime: Boolean get() = addQuery("modifiedTime")
  val modifiedByMeTime: Boolean get() = addQuery("modifiedByMeTime")
  val modifiedByMe: Boolean get() = addQuery("modifiedByMe")
  val sharedWithMeTime: Boolean get() = addQuery("sharedWithMeTime")

  fun sharingUser(block: DriveUserFieldBuilder.() -> Unit) {
    DriveUserFieldBuilder("sharingUser", this).use(block)
  }

  val owners: Boolean get() = addQuery("owners")
  val teamDriveId: Boolean get() = addQuery("teamDriveId")
  val driveId: Boolean get() = addQuery("driveId")

  fun lastModifyingUser(block: DriveUserFieldBuilder.() -> Unit) {
    DriveUserFieldBuilder("lastModifyingUser", this).use(block)
  }

  val shared: Boolean get() = addQuery("shared")
  val ownedByMe: Boolean get() = addQuery("ownedByMe")

  fun capabilities(block: DriveFileCapabilitiesFieldBuilder.() -> Unit) {
    DriveFileCapabilitiesFieldBuilder(owner = this).use(block)
  }

  val viewersCanCopyContent: Boolean get() = addQuery("viewersCanCopyContent")
  val copyRequiresWriterPermission: Boolean get() = addQuery("copyRequiresWriterPermission")
  val writersCanShare: Boolean get() = addQuery("writersCanShare")

  // permissions

  val permissionIds: Boolean get() = addQuery("permissionIds")
  val hasAugmentedPermissions: Boolean get() = addQuery("hasAugmentedPermissions")
  val folderColorRgb: Boolean get() = addQuery("folderColorRgb")
  val originalFilename: Boolean get() = addQuery("originalFilename")
  val fullFileExtension: Boolean get() = addQuery("fullFileExtension")
  val fileExtension: Boolean get() = addQuery("fileExtension")
  val md5Checksum: Boolean get() = addQuery("md5Checksum")
  val size: Boolean get() = addQuery("size")
  val quotaBytesUsed: Boolean get() = addQuery("quotaBytesUsed")
  val headRevisionId: Boolean get() = addQuery("headRevisionId")

  // contentHints
  // imageMediaMetadata
  fun imageMediaMetadata(block: DriveFileImageMediaMetadataFieldBuilder.() -> Unit) {
    DriveFileImageMediaMetadataFieldBuilder(owner = this).use(block)
  }
  // videoMediaMetadata

  val isAppAuthorized: Boolean get() = addQuery("isAppAuthorized")
  val exportLinks: Boolean get() = addQuery("exportLinks")
  val shortcutDetails: Boolean get() = addQuery("shortcutDetails/*")

  fun simpleDefaults() {
    id
    name
    mimeType
  }

  fun fileSystemDefaults() {
    id
    name
    mimeType
    starred
    trashed
    parents
    webContentLink
    webViewLink
    hasThumbnail
    thumbnailLink
    properties
    appProperties
    createdTime
    modifiedTime
    shortcutDetails
    originalFilename
    fullFileExtension
    fileExtension
  }
  // contentRestrictions

  companion object : KLogging()
}

class DriveUserFieldBuilder(
  grouping: String = "user",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<User, DriveUserFieldBuilder>(
  User::class.java,
  DriveUserFieldBuilder::class.java,
  grouping,
  owner
) {
  val displayName: Boolean get() = addQuery("displayName")
  val emailAddress: Boolean get() = addQuery("emailAddress")
  val kind: Boolean get() = addQuery("kind")
  val me: Boolean get() = addQuery("me")
  val permissionId: Boolean get() = addQuery("permissionId")
  val photoLink: Boolean get() = addQuery("photoLink")

  companion object : KLogging()
}

class DriveFileCapabilitiesFieldBuilder(
  grouping: String = "capabilities",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<File.Capabilities, DriveFileCapabilitiesFieldBuilder>(
  File.Capabilities::class.java,
  DriveFileCapabilitiesFieldBuilder::class.java,
  grouping,
  owner
) {
  val canAddChildren: Boolean get() = addQuery("canAddChildren")
  val canAddMyDriveParent: Boolean get() = addQuery("canAddMyDriveParent")
  val canChangeCopyRequiresWriterPermission: Boolean get() = addQuery("canChangeCopyRequiresWriterPermission")
  val canChangeViewersCanCopyContent: Boolean get() = addQuery("canChangeViewersCanCopyContent")
  val canComment: Boolean get() = addQuery("canComment")
  val canCopy: Boolean get() = addQuery("canCopy")
  val canDelete: Boolean get() = addQuery("canDelete")
  val canDeleteChildren: Boolean get() = addQuery("canDeleteChildren")
  val canDownload: Boolean get() = addQuery("canDownload")
  val canEdit: Boolean get() = addQuery("canEdit")
  val canListChildren: Boolean get() = addQuery("canListChildren")
  val canModifyContent: Boolean get() = addQuery("canModifyContent")
  val canMoveChildrenOutOfDrive: Boolean get() = addQuery("canMoveChildrenOutOfDrive")
  val canMoveChildrenOutOfTeamDrive: Boolean get() = addQuery("canMoveChildrenOutOfTeamDrive")
  val canMoveChildrenWithinDrive: Boolean get() = addQuery("canMoveChildrenWithinDrive")
  val canMoveChildrenWithinTeamDrive: Boolean get() = addQuery("canMoveChildrenWithinTeamDrive")
  val canMoveItemIntoTeamDrive: Boolean get() = addQuery("canMoveItemIntoTeamDrive")
  val canMoveItemOutOfDrive: Boolean get() = addQuery("canMoveItemOutOfDrive")
  val canMoveItemOutOfTeamDrive: Boolean get() = addQuery("canMoveItemOutOfTeamDrive")
  val canMoveItemWithinDrive: Boolean get() = addQuery("canMoveItemWithinDrive")
  val canMoveItemWithinTeamDrive: Boolean get() = addQuery("canMoveItemWithinTeamDrive")
  val canMoveTeamDriveItem: Boolean get() = addQuery("canMoveTeamDriveItem")
  val canReadDrive: Boolean get() = addQuery("canReadDrive")
  val canReadRevisions: Boolean get() = addQuery("canReadRevisions")
  val canReadTeamDrive: Boolean get() = addQuery("canReadTeamDrive")
  val canRemoveChildren: Boolean get() = addQuery("canRemoveChildren")
  val canRemoveMyDriveParent: Boolean get() = addQuery("canRemoveMyDriveParent")
  val canRename: Boolean get() = addQuery("canRename")
  val canShare: Boolean get() = addQuery("canShare")
  val canTrash: Boolean get() = addQuery("canTrash")
  val canTrashChildren: Boolean get() = addQuery("canTrashChildren")
  val canUntrash: Boolean get() = addQuery("canUntrash")
}

class DriveFileImageMediaMetadataFieldBuilder(
  grouping: String = "imageMediaMetadata",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<File.ImageMediaMetadata, DriveFileImageMediaMetadataFieldBuilder>(
  File.ImageMediaMetadata::class.java,
  DriveFileImageMediaMetadataFieldBuilder::class.java,
  grouping,
  owner
) {
  val aperture: Boolean get() = addQuery("aperture")
  val cameraMake: Boolean get() = addQuery("cameraMake")
  val cameraModel: Boolean get() = addQuery("cameraModel")
  val colorSpace: Boolean get() = addQuery("colorSpace")
  val exposureBias: Boolean get() = addQuery("exposureBias")
  val exposureMode: Boolean get() = addQuery("exposureMode")
  val exposureTime: Boolean get() = addQuery("exposureTime")
  val flashUsed: Boolean get() = addQuery("flashUsed")
  val focalLength: Boolean get() = addQuery("focalLength")
  val height: Boolean get() = addQuery("height")
  val isoSpeed: Boolean get() = addQuery("isoSpeed")
  val lens: Boolean get() = addQuery("lens")

  fun location(block: DriveLocationFieldBuilder.() -> Unit) {
    DriveLocationFieldBuilder(owner = this).use(block)
  }

  val maxApertureValue: Boolean get() = addQuery("maxApertureValue")
  val meteringMode: Boolean get() = addQuery("meteringMode")
  val rotation: Boolean get() = addQuery("rotation")
  val sensor: Boolean get() = addQuery("sensor")
  val subjectDistance: Boolean get() = addQuery("subjectDistance")
  val time: Boolean get() = addQuery("time")
  val whiteBalance: Boolean get() = addQuery("whiteBalance")
  val width: Boolean get() = addQuery("width")
}

class DriveLocationFieldBuilder(
  grouping: String = "location",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<File.ImageMediaMetadata.Location, DriveLocationFieldBuilder>(
  File.ImageMediaMetadata.Location::class.java,
  DriveLocationFieldBuilder::class.java,
  grouping,
  owner
) {
  val altitude: Boolean get() = addQuery("altitude")
  val latitude: Boolean get() = addQuery("latitude")
  val longitude: Boolean get() = addQuery("longitude")
}

class DriveShortcutDetailsFieldBuilder(
  grouping: String = "shortcutDetails",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<File.ShortcutDetails, DriveShortcutDetailsFieldBuilder>(
  File.ShortcutDetails::class.java,
  DriveShortcutDetailsFieldBuilder::class.java,
  grouping,
  owner
) {
  val targetId: Boolean get() = addQuery("targetId")
  val targetMimeType: Boolean get() = addQuery("targetMimeType")
}

class DriveVideoMediaMetadataFieldBuilder(
  grouping: String = "shortcutDetails",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<File.VideoMediaMetadata, DriveVideoMediaMetadataFieldBuilder>(
  File.VideoMediaMetadata::class.java,
  DriveVideoMediaMetadataFieldBuilder::class.java,
  grouping,
  owner
) {
  val durationMillis: Boolean get() = addQuery("durationMillis")
  val height: Boolean get() = addQuery("height")
  val width: Boolean get() = addQuery("width")
}

class DriveAboutFieldBuilder(
  grouping: String = "shortcutDetails",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<About, DriveAboutFieldBuilder>(
  About::class.java,
  DriveAboutFieldBuilder::class.java,
  grouping,
  owner
) {
  val appInstalled: Boolean get() = addQuery("appInstalled") // Boolean
  val canCreateDrives: Boolean get() = addQuery("canCreateDrives") // Boolean
  val canCreateTeamDrives: Boolean get() = addQuery("canCreateTeamDrives") // Boolean
  fun driveThemes(block: DriveThemeFieldBuilder.() -> Unit) {
    DriveThemeFieldBuilder("driveThemes", owner = this).use(block)
  }

  val exportFormats: Boolean get() = addQuery("exportFormats") // Map<String, List<String>>
  val folderColorPalette: Boolean get() = addQuery("folderColorPalette") // List<String>
  val importFormats: Boolean get() = addQuery("importFormats") // Map<String, List<String>>
  val kind: Boolean get() = addQuery("kind") // String
  val maxImportSizes: Boolean get() = addQuery("maxImportSizes") // Map<String, Long>
  val maxUploadSize: Boolean get() = addQuery("maxUploadSize") // Long
  fun storageQuota(block: DriveStorageQuotaFieldBuilder.() -> Unit) {
    DriveStorageQuotaFieldBuilder(owner = this).use(block)
  }

  fun teamDriveThemes(block: DriveThemeFieldBuilder.() -> Unit) {
    DriveThemeFieldBuilder("teamDriveThemes", owner = this).use(block)
  }

  fun user(block: DriveUserFieldBuilder.() -> Unit) {
    DriveUserFieldBuilder(owner = this).use(block)
  }
}

class DriveThemeFieldBuilder(
  grouping: String = "driveThemes",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<About.DriveThemes, DriveThemeFieldBuilder>(
  About.DriveThemes::class.java,
  DriveThemeFieldBuilder::class.java,
  grouping,
  owner
) {
  val backgroundImageLink: Boolean get() = addQuery("backgroundImageLink")
  val colorRgb: Boolean get() = addQuery("colorRgb")
  val id: Boolean get() = addQuery("id")
}


class DriveStorageQuotaFieldBuilder(
  grouping: String = "storageQuota",
  owner: DriveRequestFieldsBuilder? = null
) : BaseFieldBuilder<About.StorageQuota, DriveStorageQuotaFieldBuilder>(
  About.StorageQuota::class.java,
  DriveStorageQuotaFieldBuilder::class.java,
  grouping,
  owner
) {
  val limit: Boolean get() = addQuery("limit")
  val usage: Boolean get() = addQuery("usage")
  val usageInDrive: Boolean get() = addQuery("usageInDrive")
  val usageInDriveTrash: Boolean get() = addQuery("usageInDriveTrash")
}

fun Drive.Files.List.buildFields(block: DriveFilesListRequestFieldsBuilder.() -> Unit = {}): Drive.Files.List {
  this.fields = DriveFilesListRequestFieldsBuilder().apply(block).finalValue
  return this
}

fun Drive.Files.Get.buildFields(block: DriveFileFieldBuilder.() -> Unit = {}): Drive.Files.Get {
  this.fields = DriveFileFieldBuilder().apply(block).finalValue
  return this
}

/*
    TODO Drive.Changes.List fields:
     newStartPageToken,nextPageToken,changes(type,changeType,time,removed,fileId,file(id,name,mimeType,starred,trashed,parents,version,size,exportLinks))
 */

/*
Document:
Body body
String documentId
DocumentStyle documentStyle
Map<String, Footer> footers
Map<String, Footnote> footnotes
Map<String, Header> headers
Map<String, InlineObject> inlineObjects
Map<String, List> lists
Map<String, NamedRanges> namedRanges
NamedStyles namedStyles
Map<String, PositionedObject> positionedObjects
String revisionId
Map<String, SuggestedDocumentStyle> suggestedDocumentStyleChanges
Map<String, SuggestedNamedStyles> suggestedNamedStylesChanges
String suggestionsViewMode
String title

DocumentStyle:
Background background
String defaultFooterId
String defaultHeaderId
String evenPageFooterId
String evenPageHeaderId
String firstPageFooterId
String firstPageHeaderId
Dimension marginBottom
Dimension marginFooter
Dimension marginHeader
Dimension marginLeft
Dimension marginRight
Dimension marginTop
Integer pageNumberStart
Size pageSize
Boolean useCustomHeaderFooterMargins
Boolean useEvenPageHeaderFooter
Boolean useFirstPageHeaderFooter

Body:
List<StructuralElement> content

StructuralElement:
Integer endIndex
Paragraph paragraph
SectionBreak sectionBreak
Integer startIndex
Table table
TableOfContents tableOfContents

SectionBreak:
SectionStyle sectionStyle
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds

SectionStyle:
List<SectionColumnProperties> columnProperties
String columnSeparatorStyle
String contentDirection
String defaultFooterId
String defaultHeaderId
String evenPageFooterId
String evenPageHeaderId
String firstPageFooterId
String firstPageHeaderId
Dimension marginBottom
Dimension marginFooter
Dimension marginHeader
Dimension marginLeft
Dimension marginRight
Dimension marginTop
Integer pageNumberStart
String sectionType
Boolean useFirstPageHeaderFooter

SectionColumnProperties:
Dimension paddingEnd
Dimension width

Paragraph:
Bullet bullet
List<ParagraphElement> elements
ParagraphStyle paragraphStyle
List<String> positionedObjectIds
Map<String, SuggestedBullet> suggestedBulletChanges
Map<String, SuggestedParagraphStyle> suggestedParagraphStyleChanges
Map<String, ObjectReferences> suggestedPositionedObjectIds

ParagraphElement:
AutoText autoText
ColumnBreak columnBreak
Integer endIndex
Equation equation
FootnoteReference footnoteReference
HorizontalRule horizontalRule
InlineObjectElement inlineObjectElement
PageBreak pageBreak
Integer startIndex
TextRun textRun

ParagraphStyle:
String alignment
Boolean avoidWidowAndOrphan
ParagraphBorder borderBetween
ParagraphBorder borderBottom
ParagraphBorder borderLeft
ParagraphBorder borderRight
ParagraphBorder borderTop
String direction
String headingId
Dimension indentEnd
Dimension indentFirstLine
Dimension indentStart
Boolean keepLinesTogether
Boolean keepWithNext
Float lineSpacing
String namedStyleType
Shading shading
Dimension spaceAbove
Dimension spaceBelow
String spacingMode
List<TabStop> tabStops

ParagraphBorder:
OptionalColor color
String dashStyle
Dimension padding
Dimension width

SuggestedParagraphStyle:
ParagraphStyle paragraphStyle
ParagraphStyleSuggestionState paragraphStyleSuggestionState

ParagraphStyleSuggestionState:
Boolean alignmentSuggested
Boolean avoidWidowAndOrphanSuggested
Boolean borderBetweenSuggested
Boolean borderBottomSuggested
Boolean borderLeftSuggested
Boolean borderRightSuggested
Boolean borderTopSuggested
Boolean directionSuggested
Boolean headingIdSuggested
Boolean indentEndSuggested
Boolean indentFirstLineSuggested
Boolean indentStartSuggested
Boolean keepLinesTogetherSuggested
Boolean keepWithNextSuggested
Boolean lineSpacingSuggested
Boolean namedStyleTypeSuggested
ShadingSuggestionState shadingSuggestionState
Boolean spaceAboveSuggested
Boolean spaceBelowSuggested
Boolean spacingModeSuggested

Header:
List<StructuralElement> content
String headerId

Footer:
List<StructuralElement> content
String footerId

ShadingSuggestionState:
Boolean backgroundColorSuggested

Bullet:
String listId
Integer nestingLevel
TextStyle textStyle

SuggestedBullet:
Bullet bullet
BulletSuggestionState bulletSuggestionState

BulletSuggestionState:
Boolean listIdSuggested
Boolean nestingLevelSuggested
TextStyleSuggestionState textStyleSuggestionState

HorizontalRule:
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds
Map<String, SuggestedTextStyle> suggestedTextStyleChanges
TextStyle textStyle

InlineObjectElement:
String inlineObjectId
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds
Map<String, SuggestedTextStyle> suggestedTextStyleChanges
TextStyle textStyle

PageBreak:
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds
Map<String, SuggestedTextStyle> suggestedTextStyleChanges
TextStyle textStyle

TextRun:
String content
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds
Map<String, SuggestedTextStyle> suggestedTextStyleChanges
TextStyle textStyle

AutoText:
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds
Map<String, SuggestedTextStyle> suggestedTextStyleChanges
TextStyle textStyle
String type

TextStyle:
OptionalColor backgroundColor
String baselineOffset
Boolean bold
Dimension fontSize
OptionalColor foregroundColor
Boolean italic
Link link
Boolean smallCaps
Boolean strikethrough
Boolean underline
WeightedFontFamily weightedFontFamily

SuggestedTextStyle
TextStyle textStyle
TextStyleSuggestionState textStyleSuggestionState

TextStyleSuggestionState:
Boolean backgroundColorSuggested
Boolean baselineOffsetSuggested
Boolean boldSuggested
Boolean fontSizeSuggested
Boolean foregroundColorSuggested
Boolean italicSuggested
Boolean linkSuggested
Boolean smallCapsSuggested
Boolean strikethroughSuggested
Boolean underlineSuggested
Boolean weightedFontFamilySuggested

ColumnBreak:
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds
Map<String, SuggestedTextStyle> suggestedTextStyleChanges
TextStyle textStyle

Equation:
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds

FootnoteReference:
String footnoteId
String footnoteNumber
List<String> suggestedDeletionIds
List<String> suggestedInsertionIds
Map<String, SuggestedTextStyle> suggestedTextStyleChanges
TextStyle textStyle

TabStop:
String alignment
Dimension offset

Background:
OptionalColor color

Shading:
OptionalColor backgroundColor;

OptionalColor:
Color color

Size:
Dimension height
Dimension width

Dimension:
Double magnitude
String unit

Link:
String bookmarkId
String headingId
String url

WeightedFontFamily:
String fontFamily
Integer weight


ObjectReferences:
List<String> objectIds

 */

/*
GOOGLE DRIVE STUFF

Model Fields:
  File:
    Map<String, String>   appProperties;
    Capabilities          capabilities;
    ContentHints          contentHints;
    Boolean               copyRequiresWriterPermission;
    DateTime              createdTime;
    String                description;
    String                driveId;
    Boolean               explicitlyTrashed;
    Map<String, String>   exportLinks;
    String                fileExtension;
    String                folderColorRgb;
    String                fullFileExtension;
    Boolean               hasAugmentedPermissions;
    Boolean               hasThumbnail;
    String                headRevisionId;
    String                iconLink;
    String                id;
    ImageMediaMetadata    imageMediaMetadata;
    Boolean               isAppAuthorized;
    String                kind;
    User                  lastModifyingUser;
    String                md5Checksum;
    String                mimeType;
    Boolean               modifiedByMe;
    DateTime              modifiedByMeTime;
    DateTime              modifiedTime;
    String                name;
    String                originalFilename;
    Boolean               ownedByMe;
    List<User>            owners;
    List<String>          parents;
    List<String>          permissionIds;
    List<Permission>      permissions;
    Map<String, String>   properties;
    Long                  quotaBytesUsed;
    Boolean               shared;
    DateTime              sharedWithMeTime;
    User                  sharingUser;
    ShortcutDetails       shortcutDetails;
    Long                  size;
    List<String>          spaces;
    Boolean               starred;
    String                teamDriveId;
    String                thumbnailLink;
    Long                  thumbnailVersion;
    Boolean               trashed;
    DateTime              trashedTime;
    User                  trashingUser;
    Long                  version;
    VideoMediaMetadata    videoMediaMetadata;
    Boolean               viewedByMe;
    DateTime              viewedByMeTime;
    Boolean               viewersCanCopyContent;
    String                webContentLink;
    String                webViewLink;
    Boolean               writersCanShare;

  Drive.Capabilities:
    Boolean canAddChildren
    Boolean canAddMyDriveParent
    Boolean canChangeCopyRequiresWriterPermission
    Boolean canChangeViewersCanCopyContent
    Boolean canComment
    Boolean canCopy
    Boolean canDelete
    Boolean canDeleteChildren
    Boolean canDownload
    Boolean canEdit
    Boolean canListChildren
    Boolean canModifyContent
    Boolean canMoveChildrenOutOfDrive
    Boolean canMoveChildrenOutOfTeamDrive
    Boolean canMoveChildrenWithinDrive
    Boolean canMoveChildrenWithinTeamDrive
    Boolean canMoveItemIntoTeamDrive
    Boolean canMoveItemOutOfDrive
    Boolean canMoveItemOutOfTeamDrive
    Boolean canMoveItemWithinDrive
    Boolean canMoveItemWithinTeamDrive
    Boolean canMoveTeamDriveItem
    Boolean canReadDrive
    Boolean canReadRevisions
    Boolean canReadTeamDrive
    Boolean canRemoveChildren
    Boolean canRemoveMyDriveParent
    Boolean canRename
    Boolean canShare
    Boolean canTrash
    Boolean canTrashChildren
    Boolean canUntrash

  ContentHints:
    String indexableText
    Thumbnail thumbnail

  Thumbnail: (this class is only used if google drive cant make a normal thumbnail)
    String image
    String mimeType

  ShortcutDetails:
    String targetId
    String targetMimeType

  VideoMediaMetadata:
    Long durationMillis
    Integer height
    Integer width

  StartPageToken:
    String kind
    String startPageToken

  Change:
    String changeType
    Drive drive
    String driveId
    File file
    String fileId
    String kind
    Boolean removed
    TeamDrive teamDrive
    String teamDriveId
    DateTime time
    String type

  Channel:
    String address
    Long expiration
    String id
    String kind
    Map<String, String> params
    Boolean payload
    String resourceId
    String resourceUri
    String token
    String type

  Comments:
    String anchor
    User author
    String content
    DateTime createdTime
    Boolean deleted
    String htmlContent
    String id
    String kind
    DateTime modifiedTime
    QuotedFileContent quotedFileContent
    List<Reply> replies
    Boolean resolved

  QuotedFileContent:
    String mimeType
    String value

  Reply:
    String action
    User author
    String content
    DateTime createdTime
    Boolean deleted
    String htmlContent
    String id
    String kind
    DateTime modifiedTime

  ChangeList:
    List<Change> changes
    String kind
    String newStartPageToken
    String nextPageToken

  CommentList:
    List<Comment> comments
    String kind
    String nextPageToken

  FileList:
    List<File> files;
    Boolean incompleteSearch;
    String kind;
    String nextPageToken;

  ReplyList:
    String kind
    String nextPageToken
    List<Reply> replies

  DriveList:
    List<Drive> drives
    String kind
    String nextPageToken

  TeamDriveList:
    String kind
    String nextPageToken
    List<TeamDrive> teamDrives

  Drive (Model):
    BackgroundImageFile backgroundImageFile
    String backgroundImageLink
    Capabilities capabilities
    String colorRgb
    DateTime createdTime
    Boolean hidden
    String id
    String kind
    String name
    Restrictions restrictions
    String themeId

  TeamDrive:
    BackgroundImageFile backgroundImageFile
    String backgroundImageLink
    Capabilities capabilities
    String colorRgb
    DateTime createdTime
    String id
    String kind
    String name
    Restrictions restrictions
    String themeId
    String id
    Float width
    Float xCoordinate
    Float yCoordinate

  TeamDrive.Capabilities:
    Boolean canAddChildren
    Boolean canChangeCopyRequiresWriterPermissionRestriction
    Boolean canChangeDomainUsersOnlyRestriction
    Boolean canChangeTeamDriveBackground
    Boolean canChangeTeamMembersOnlyRestriction
    Boolean canComment
    Boolean canCopy
    Boolean canDeleteChildren
    Boolean canDeleteTeamDrive
    Boolean canDownload
    Boolean canEdit
    Boolean canListChildren
    Boolean canManageMembers
    Boolean canReadRevisions
    Boolean canRemoveChildren
    Boolean canRename
    Boolean canRenameTeamDrive
    Boolean canShare
    Boolean canTrashChildren

  Restrictions:
    Boolean adminManagedRestrictions
    Boolean copyRequiresWriterPermission
    Boolean domainUsersOnly
    Boolean teamMembersOnly

  RevisionList:
    String kind
    String nextPageToken
    List<Revision> revisions

  Revision:
    Map<String, String> exportLinks
    String id
    Boolean keepForever
    String kind
    User lastModifyingUser
    String md5Checksum
    String mimeType
    DateTime modifiedTime
    String originalFilename
    Boolean publishAuto
    Boolean published
    Boolean publishedOutsideDomain
    Long size

  Permissions:
    Boolean allowFileDiscovery
    Boolean deleted
    String displayName
    String domain
    String emailAddress
    DateTime expirationTime
    String id
    String kind
    List<PermissionDetails> permissionDetails
    String photoLink
    String role
    List<TeamDrivePermissionDetails> teamDrivePermissionDetails
    String type

  PermissionDetails:
    Boolean inherited
    String inheritedFrom
    String permissionType
    String role

  TeamPermissions:
    Boolean inherited
    String inheritedFrom
    String role
    String teamDrivePermissionType

  DriveRequest<T>:
    String alt
    String fields
    String key
    String oauthToken
    Boolean prettyPrint
    String quotaUser
    String userIp


Request Types:
  Drive
    About
      Get = DriveRequest<com.google.api.services.drive.model.About>

    Changes
      GetStartPageToken = DriveRequest<com.google.api.services.drive.model.StartPageToken>
      List = DriveRequest<com.google.api.services.drive.model.ChangeList>
      Watch = DriveRequest<com.google.api.services.drive.model.Channel>

    Channels
      Stop = DriveRequest<Void>

    Comments
      Create = DriveRequest<com.google.api.services.drive.model.Comment>
      Delete = DriveRequest<Void>
      Get = DriveRequest<com.google.api.services.drive.model.Comment>
      List = DriveRequest<com.google.api.services.drive.model.CommentList>
      Update = DriveRequest<com.google.api.services.drive.model.Comment>

    Drives
      Create = DriveRequest<com.google.api.services.drive.model.Drive>
      Delete = DriveRequest<Void>
      Get = DriveRequest<com.google.api.services.drive.model.Drive>
      Hide = DriveRequest<com.google.api.services.drive.model.Drive>
      List = DriveRequest<com.google.api.services.drive.model.DriveList>
      Unhide = DriveRequest<com.google.api.services.drive.model.Drive>
      Update = DriveRequest<com.google.api.services.drive.model.Drive>

    Files
      Copy = DriveRequest<com.google.api.services.drive.model.File>
      Create = DriveRequest<com.google.api.services.drive.model.File>
      Delete = DriveRequest<Void>
      EmptyTrash = DriveRequest<Void>
      Export = DriveRequest<Void>
      GenerateIds = DriveRequest<com.google.api.services.drive.model.GeneratedIds>
      Get = DriveRequest<com.google.api.services.drive.model.File>
      List = DriveRequest<com.google.api.services.drive.model.FileList>
      Update = DriveRequest<com.google.api.services.drive.model.File>
      Watch = DriveRequest<com.google.api.services.drive.model.Channel>

    Permissions
      Create = DriveRequest<com.google.api.services.drive.model.Permission>
      Delete = DriveRequest<Void>
      Get = DriveRequest<com.google.api.services.drive.model.Permission>
      List = DriveRequest<com.google.api.services.drive.model.PermissionList>
      Update = DriveRequest<com.google.api.services.drive.model.Permission>

    Replies
      Create = DriveRequest<com.google.api.services.drive.model.Reply>
      Delete = DriveRequest<Void>
      Get = DriveRequest<com.google.api.services.drive.model.Reply>
      List = DriveRequest<com.google.api.services.drive.model.ReplyList>
      Update = DriveRequest<com.google.api.services.drive.model.Reply>

    Revisions
      Delete = DriveRequest<Void>
      Get = DriveRequest<com.google.api.services.drive.model.Revision>
      List = DriveRequest<com.google.api.services.drive.model.RevisionList>
      Update = DriveRequest<com.google.api.services.drive.model.Revision>

    Teamdrives
      Create = DriveRequest<com.google.api.services.drive.model.TeamDrive>
      Delete = DriveRequest<Void>
      Get = DriveRequest<com.google.api.services.drive.model.TeamDrive>
      List = DriveRequest<com.google.api.services.drive.model.TeamDriveList>
      Update = DriveRequest<com.google.api.services.drive.model.TeamDrive>



 */
