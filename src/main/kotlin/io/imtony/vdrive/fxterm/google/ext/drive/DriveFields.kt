package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.client.json.GenericJson
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import io.imtony.vdrive.fxterm.utils.ifNotNull
import mu.KLogging
import java.beans.Introspector
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaMethod

interface GeneralBuilder {
  val finalValue: String
}

interface DriveRequestFieldsBuilder : GeneralBuilder, AutoCloseable {
  fun addQuery(input: String): Boolean
}

class DriveFilesListRequestFieldsBuilder(
  private val owner: DriveRequestFieldsBuilder? = null
) : DriveRequestFieldsBuilder {
  private var queryParts: MutableSet<String> = mutableSetOf()

  override val finalValue get() = queryParts.joinToString(",")

  override fun addQuery(input: String): Boolean = queryParts.add(input)

  override fun close() {
    owner?.addQuery(finalValue)
  }

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

interface PropertyBuilder<TTarget, TThis : PropertyBuilder<TTarget, TThis>> : DriveRequestFieldsBuilder {
  fun build(block: TThis.() -> Unit): Unit
}

open class PropertyBuilderImpl<T>(
  private val targetClass: Class<T>,
  private val groupPrefix: String? = null,
  private val owner: DriveRequestFieldsBuilder? = null,
) : PropertyBuilder<T, PropertyBuilderImpl<T>> {

  constructor(
    targetClass: Class<T>,
    groupPrefix: String? = null,
    owner: DriveRequestFieldsBuilder? = null,
    initBlock: PropertyBuilderImpl<T>.() -> Unit = {},
  ) : this(targetClass, groupPrefix, owner) {
    build(initBlock)
  }

  private val beanInfo by lazy { Introspector.getBeanInfo(targetClass, GenericJson::class.java) }
  private val queries: MutableSet<String> = mutableSetOf()

  override val finalValue: String
    get() = if (queries.isEmpty()) "" else {
      if (groupPrefix.isNullOrBlank()) {
        queries.joinToString(",")
      }else {
        "$groupPrefix(${queries.joinToString(",")})"
      }
    }

  override fun build(block: PropertyBuilderImpl<T>.() -> Unit) {
    block.invoke(this)
  }

  override fun addQuery(input: String): Boolean {
    if (input.isNotBlank() && input !in queries) {
      return queries.add(input)
    }

    return false
  }

  fun <TValue> addField(prop: KProperty1<T, TValue>) {
    val name = if (prop is KProperty1.Getter<*, *>) {
      prop.name.removePrefix("<get-").removeSuffix(">")
    } else {
      prop.name
    }

    logger.info { "Found name '$name' from KProperty1 '$prop'." }
    addQuery(name)
  }

  fun <TValue> addField(getter: KFunction1<T, MutableList<TValue>>) {
    val jm = getter.javaMethod
    if (jm == null) {
      val msg = "Could not get javaMethod for getter: '$getter'"
      logger.error { msg }
      throw IllegalArgumentException(msg)
    }

    val pd = beanInfo.propertyDescriptors.firstOrNull { it.writeMethod == jm || it.readMethod == jm }

    if (pd == null) {
      val msg = "No propertyDescriptor found with read or write methods matching '$jm'."
      logger.error { msg }
      throw IllegalArgumentException(msg)
    }

    addQuery(pd.name)
  }

  override fun close() {
    owner.ifNotNull {
      it.addQuery(finalValue)
    }
  }

  companion object : KLogging() {
    inline fun <reified T> new(
      owner: DriveRequestFieldsBuilder? = null,
      groupPrefix: String? = null,
      noinline initBlock: PropertyBuilderImpl<T>.() -> Unit = {}
    ): PropertyBuilder<T, PropertyBuilderImpl<T>> = PropertyBuilderImpl(T::class.java, groupPrefix, owner, initBlock)
  }
}

class FilesListPropertyBuilder(
  owner: DriveRequestFieldsBuilder? = null,
) : PropertyBuilderImpl<FileList>(FileList::class.java, null, owner) {
  constructor(owner: DriveRequestFieldsBuilder? = null, block: FilesListPropertyBuilder.() -> Unit) : this(owner) {
  }

  override fun build(block: PropertyBuilderImpl<FileList>.() -> Unit) {
    block.invoke(this)
  }

  companion object : KLogging() {
    inline fun <reified T> new(
      owner: DriveRequestFieldsBuilder? = null,
      groupPrefix: String? = null,
      noinline initBlock: FilesListPropertyBuilder.() -> Unit = {}
    ): FilesListPropertyBuilder = FilesListPropertyBuilder()
  }
}

private fun tester() {
  FilesListPropertyBuilder
}

class DriveFileFieldBuilder(private val owner: DriveRequestFieldsBuilder? = null) : DriveRequestFieldsBuilder {
  private val queries: MutableList<String> = mutableListOf()

  override val finalValue: String
    get() = if (queries.isEmpty()) "" else "files(${queries.joinToString(",")}})"

  override fun addQuery(input: String): Boolean =
    if (queries.contains(input)) {
      logger.warn { "Cannot add same field twice. Input: $input" }
      false
    } else {
      queries.add(input)
    }

  val id: Boolean get() = addQuery("id")
  val name: Boolean get() = addQuery("name")
  val mimeType: Boolean get() = addQuery("mimeType")
  val description: Boolean get() = addQuery("description")
  val starred: Boolean get() = addQuery("starred")
  val trashed: Boolean get() = addQuery("trashed")
  val explicitlyTrashed: Boolean get() = addQuery("explicitlyTrashed")

  // trashingUser

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

  // sharingUser

  val owners: Boolean get() = addQuery("owners")
  val teamDriveId: Boolean get() = addQuery("teamDriveId")
  val driveId: Boolean get() = addQuery("driveId")

  // lastModifyingUser

  val shared: Boolean get() = addQuery("shared")
  val ownedByMe: Boolean get() = addQuery("ownedByMe")

  // capabilities

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

  override fun close() {
    owner?.addQuery(finalValue)
  }

  companion object : KLogging()
}

fun Drive.Files.List.buildFields(block: DriveFilesListRequestFieldsBuilder.() -> Unit = {}): Drive.Files.List {
  val builder = DriveFilesListRequestFieldsBuilder().apply(block)
  this.fields = builder.finalValue
  return this
}

fun Drive.Files.Get.buildFields(block: DriveFileFieldBuilder.() -> Unit = {}): Drive.Files.Get {
  val builder = DriveFileFieldBuilder().apply(block)
  this.fields = builder.finalValue
  return this
}

/*
    TODO Drive.Changes.List fields:
     newStartPageToken,nextPageToken,changes(type,changeType,time,removed,fileId,file(id,name,mimeType,starred,trashed,parents,version,size,exportLinks))
 */

/* TODO Expand this to be more general and usable for other requests now that I'm starting to
        explore the Drive.Changes.* api, which will also involve setting response fields.
 */

/*

File:
Map<String, String> appProperties;
Capabilities capabilities;
ContentHints contentHints;
Boolean copyRequiresWriterPermission;
DateTime createdTime;
String description;
String driveId;
Boolean explicitlyTrashed;
Map<String, String> exportLinks;
String fileExtension;
String folderColorRgb;
String fullFileExtension;
Boolean hasAugmentedPermissions;
Boolean hasThumbnail;
String headRevisionId;
String iconLink;
String id;
ImageMediaMetadata imageMediaMetadata;
Boolean isAppAuthorized;
String kind;
User lastModifyingUser;
String md5Checksum;
String mimeType;
Boolean modifiedByMe;
DateTime modifiedByMeTime;
DateTime modifiedTime;
String name;
String originalFilename;
Boolean ownedByMe;
List<User> owners;
List<String> parents;
List<String> permissionIds;
List<Permission> permissions;
Map<String, String> properties;
Long quotaBytesUsed;
Boolean shared;
DateTime sharedWithMeTime;
User sharingUser;
ShortcutDetails shortcutDetails;
Long size;
List<String> spaces;
Boolean starred;
String teamDriveId;
String thumbnailLink;
Long thumbnailVersion;
Boolean trashed;
DateTime trashedTime;
User trashingUser;
Long version;
VideoMediaMetadata videoMediaMetadata;
Boolean viewedByMe;
DateTime viewedByMeTime;
Boolean viewersCanCopyContent;
String webContentLink;
String webViewLink;
Boolean writersCanShare;

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

ImageMediaMetadata:
Float aperture
String cameraMake
String cameraModel
String colorSpace
Float exposureBias
String exposureMode
Float exposureTime
Boolean flashUsed
Float focalLength
Integer height
Integer isoSpeed
String lens
Location location
Float maxApertureValue
String meteringMode
Integer rotation
String sensor
Integer subjectDistance
String time
String whiteBalance
Integer width

ShortcutDetails:
String targetId
String targetMimeType

VideoMediaMetadata:
Long durationMillis
Integer height
Integer width

User:
String displayName
String emailAddress
String kind
Boolean me
String permissionId
String photoLink

About:
Boolean appInstalled
Boolean canCreateDrives
Boolean canCreateTeamDrives
List<DriveThemes> driveThemes
Map<String, List<String>> exportFormats
List<String> folderColorPalette
Map<String, List<String>> importFormats
String kind
Map<String, Long> maxImportSizes
Long maxUploadSize
StorageQuota storageQuota
List<TeamDriveThemes> teamDriveThemes
User user

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

DriveList
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

TeamDrive.Capabilities
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

========================================
              Docs

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

  ImageMediaMetadata:
    Float aperture
    String cameraMake
    String cameraModel
    String colorSpace
    Float exposureBias
    String exposureMode
    Float exposureTime
    Boolean flashUsed
    Float focalLength
    Integer height
    Integer isoSpeed
    String lens
    Location location
    Float maxApertureValue
    String meteringMode
    Integer rotation
    String sensor
    Integer subjectDistance
    String time
    String whiteBalance
    Integer width

  ShortcutDetails:
    String targetId
    String targetMimeType

  VideoMediaMetadata:
    Long durationMillis
    Integer height
    Integer width

  User:
    String displayName
    String emailAddress
    String kind
    Boolean me
    String permissionId
    String photoLink

  About:
    Boolean appInstalled
    Boolean canCreateDrives
    Boolean canCreateTeamDrives
    List<DriveThemes> driveThemes
    Map<String, List<String>> exportFormats
    List<String> folderColorPalette
    Map<String, List<String>> importFormats
    String kind
    Map<String, Long> maxImportSizes
    Long maxUploadSize
    StorageQuota storageQuota
    List<TeamDriveThemes> teamDriveThemes
    User user

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
