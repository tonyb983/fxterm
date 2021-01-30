@file:Suppress("unused", "TooManyFunctions", "CovariantEquals")

package io.imtony.vdrive.fxterm.google.ext.drive

import mu.KLogging
import org.apache.commons.text.StringEscapeUtils
import java.nio.file.attribute.FileTime
import com.google.api.services.drive.Drive.Files.List as ListRequest

/**
 * The types of operators that can be applied to a Google Drive Query.
 */
enum class QueryOperator {
  /** The content of one string is present in the other. */
  Contains,

  /** The content of a string or boolean is equal to the other. */
  Eq,

  /** The content of a string or boolean is not equal to the other. */
  NotEq,

  /** A value is less than another. */
  Lt,

  /** A value is less than or equal to another. */
  Lte,

  /** A value is greater than another. */
  Gt,

  /** 	A value is greater than or equal to another. */
  Gte,

  /** An element is contained within a collection. */
  In,

  /** Negates a search query. */
  Not,

  /** A collection contains an element matching the parameters. */
  Has;

  override fun toString(): String = when (this) {
    Contains -> "contains"
    Eq -> "="
    NotEq -> "!="
    Lt -> "<"
    Lte -> "<="
    Gt -> ">"
    Gte -> ">="
    In -> "in"
    Not -> "not"
    Has -> "has"
  }
}

private enum class QueryConnectors {
  /** Return items that match both queries. */
  And,

  /** Return items that match either query. */
  Or;

  override fun toString(): String = when (this) {
    And -> "and"
    Or -> "or"
  }
}

/**
 * The target [com.google.api.services.drive.model.File] property that the [GoogleDriveQuery] is targeting.
 *
 * @property value The value this [QueryTerm] wraps.
 */
inline class QueryTerm(val value: String) {
  override fun toString(): String = value
}

/**
 * The user provided value that the [QueryTerm] is tested against.
 *
 * @property value The value this [QueryValue] wraps.
 * @suppress UseDataClass The generated copy fun would expose the private ctor creating string values without '
 */
@Suppress("UseDataClass")
class QueryValue private constructor(val value: String) {
  constructor(value: Boolean) : this(if (value) "true" else "false")

  constructor(
    value: String,
    escape: Boolean
  ) : this(if (escape) "'${StringEscapeUtils.escapeJava(value)}'" else "'$value'")

  override fun toString(): String = value
}

/**
 * Represents a [GoogleDriveQuery]. It should override the [toString] method to generate it's query string.
 */
interface GoogleDriveQuery

private object EmptyQuery : GoogleDriveQuery {
  override fun equals(other: Any?): Boolean = other is EmptyQuery
  override fun hashCode(): Int = -1

  override fun toString(): String = ""
}

private abstract class ConditionalBinaryQuery(
  val first: GoogleDriveQuery,
  val second: GoogleDriveQuery
) : GoogleDriveQuery {
  protected fun ensureNotEmpty(f: GoogleDriveQuery, s: GoogleDriveQuery): String? {
    val fEmpty = f == EmptyQuery
    val sEmpty = s == EmptyQuery

    if (fEmpty && sEmpty) {
      return ""
    }
    if (fEmpty) {
      return s.toString()
    }
    if (sEmpty) {
      return first.toString()
    }

    return null
  }

  abstract override fun toString(): String
}

private class AndDriveQuery(
  first: GoogleDriveQuery,
  second: GoogleDriveQuery
) : ConditionalBinaryQuery(first, second) {
  override fun toString(): String = ensureNotEmpty(first, second) ?: "$first and $second"
}

private class OrDriveQuery(
  first: GoogleDriveQuery,
  second: GoogleDriveQuery
) : ConditionalBinaryQuery(first, second) {
  override fun toString(): String = ensureNotEmpty(first, second) ?: "$first or $second"
}

private data class GroupedDriveQuery(val other: GoogleDriveQuery) : GoogleDriveQuery {
  override fun toString(): String = "($other)"
}

private class SingleGoogleDriveQuery(
  val negated: Boolean,
  var queryTerm: QueryTerm,
  var queryOperator: QueryOperator,
  var queryValue: QueryValue,
) : GoogleDriveQuery {
  constructor(
    queryTerm: QueryTerm,
    queryOperator: QueryOperator,
    queryValue: QueryValue,
  ) : this(false, queryTerm, queryOperator, queryValue)

  override fun toString(): String = (if (negated) "not " else "") +
    if (queryOperator == QueryOperator.In) {
      "$queryValue $queryOperator $queryTerm"
    } else {
      "$queryTerm $queryOperator $queryValue"
    }
}

/**
 * Represents a [QueryOperation] that is initialized with a [QueryTerm].
 */
interface QueryOperation {
  /** The [QueryTerm] this operation will target when complete. */
  val term: QueryTerm

  /** The [GoogleDriveQueryBuilder] that the final [GoogleDriveQuery] will be added to. */
  val builder: GoogleDriveQueryBuilder?

  /** Creates a new query targeting the [term] saved in this [QueryOperation] along
   * with the given [op] and [value]. If [builder] is not null, the returned
   * [GoogleDriveQuery] will be added to it on creation.
   */
  fun newQuery(
    op: QueryOperator,
    value: QueryValue
  ): GoogleDriveQuery = SingleGoogleDriveQuery(term, op, value).also {
    if (builder == null) {
      logger.warn { "No builder to add '$it' to." }
    } else {
      if (builder?.logging == true) logger.info("Adding '$it' to builder '$builder'")
    }
    builder?.addQuery(it)
  }

  companion object : KLogging()
}

/**
 * Represents a [QueryOperation] that can use the [equals] aka [QueryOperator.Eq] as well
 * as the [doesntEqual] aka [QueryOperator.NotEq] operator.
 */
@Suppress("CovariantEquals")
interface QueryOperationEquals : QueryOperation {

  /** Creates the equals ( = ) operation. */
  infix fun equals(value: QueryValue): GoogleDriveQuery

  /** Creates the not equal ( != ) operation. */
  infix fun doesntEqual(value: QueryValue): GoogleDriveQuery
}

/**
 * Represents a [QueryOperation] that can use the [contains] aka [QueryOperator.Contains] operator.
 */
interface QueryOperationContains : QueryOperation {
  /**
   * Creates a *CONTAINS* operation using [value] to target [term].
   */
  infix fun contains(value: QueryValue): GoogleDriveQuery
}

/**
 * Represents a [QueryOperation] that can use the [has] aka [QueryOperator.Has] operator.
 */
interface QueryOperationHas : QueryOperation {
  /**
   * Creates an *IN* operation (normally the [QueryValue] should be listed first for an [QueryOperator.In] operation).
   */
  infix fun has(value: QueryValue): GoogleDriveQuery
}

/**
 * Represents a [QueryOperation] that can use the [greaterThan] aka [QueryOperator.Gt] operator.
 */
interface QueryOperationGreaterThan : QueryOperation {
  /** Creates the greater than ( > ) operation. */
  infix fun greaterThan(value: QueryValue): GoogleDriveQuery
}

/**
 * Represents a [QueryOperation] that can use the [greaterThanOrEqual] aka [QueryOperator.Gte] operator.
 */
interface QueryOperationGte : QueryOperation {
  /** Creates the greater than or equal ( >= ) operation. */
  infix fun greaterThanOrEqual(value: QueryValue): GoogleDriveQuery
}

/**
 * Represents a [QueryOperation] that can use the [lessThan] aka [QueryOperator.Lt] operator.
 */
interface QueryOperationLessThan : QueryOperation {
  /** Creates the less than ( < ) operation. */
  infix fun lessThan(value: QueryValue): GoogleDriveQuery
}

/**
 * Represents a [QueryOperation] that can use the [lessThanOrEqual] aka [QueryOperator.Lte] operator.
 */
interface QueryOperationLte : QueryOperation {
  /** Creates the less than or equal ( <= ) operation. */
  infix fun lessThanOrEqual(value: QueryValue): GoogleDriveQuery
}

// Convenience functions
/** Convenience function pointing to [equals]. */
infix fun QueryOperationEquals.equals(value: Boolean): GoogleDriveQuery = equals(QueryValue(value))

/** Convenience function pointing to [equals]. */
infix fun QueryOperationEquals.equals(value: Int): GoogleDriveQuery = equals(QueryValue(value.toString(), false))

/** Convenience function pointing to [equals]. */
infix fun QueryOperationEquals.equals(value: Float): GoogleDriveQuery = equals(QueryValue(value.toString(), false))

/** Convenience function pointing to [equals]. */
infix fun QueryOperationEquals.equals(value: Long): GoogleDriveQuery = equals(QueryValue(value.toString(), false))

/** Convenience function pointing to [equals]. */
infix fun QueryOperationEquals.eq(value: QueryValue): GoogleDriveQuery = equals(value)

/** Convenience function pointing to [equals]. */
infix fun QueryOperationEquals.eq(value: Boolean): GoogleDriveQuery = equals(QueryValue(value))

/** Convenience function pointing to [doesntEqual]. */
infix fun QueryOperationEquals.doesntEqual(value: Boolean): GoogleDriveQuery = doesntEqual(QueryValue(value))

/** Convenience function pointing to [doesntEqual]. */
infix fun QueryOperationEquals.notEq(value: QueryValue): GoogleDriveQuery = doesntEqual(value)

/** Convenience function pointing to [doesntEqual]. */
infix fun QueryOperationEquals.notEq(value: Boolean): GoogleDriveQuery = doesntEqual(QueryValue(value))

/** Convenience function pointing to [greaterThan]. */
infix fun QueryOperationGreaterThan.greaterThan(
  value: Int
): GoogleDriveQuery = greaterThan(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [greaterThan]. */
infix fun QueryOperationGreaterThan.greaterThan(value: Float): GoogleDriveQuery = greaterThan(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [greaterThan]. */
infix fun QueryOperationGreaterThan.greaterThan(value: Long): GoogleDriveQuery = greaterThan(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [greaterThan]. */
infix fun QueryOperationGreaterThan.gt(value: QueryValue): GoogleDriveQuery = greaterThan(value)

/** Convenience function pointing to [greaterThanOrEqual]. */
infix fun QueryOperationGte.greaterThanOrEqual(value: Int): GoogleDriveQuery = greaterThanOrEqual(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [greaterThanOrEqual]. */
infix fun QueryOperationGte.greaterThanOrEqual(value: Float): GoogleDriveQuery = greaterThanOrEqual(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [greaterThanOrEqual]. */
infix fun QueryOperationGte.greaterThanOrEqual(value: Long): GoogleDriveQuery = greaterThanOrEqual(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [greaterThanOrEqual]. */
infix fun QueryOperationGte.gte(value: QueryValue): GoogleDriveQuery = greaterThanOrEqual(value)

/** Convenience function pointing to [lessThan]. */
infix fun QueryOperationLessThan.lessThan(value: Int): GoogleDriveQuery = lessThan(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [lessThan]. */
infix fun QueryOperationLessThan.lessThan(value: Float): GoogleDriveQuery = lessThan(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [lessThan]. */
infix fun QueryOperationLessThan.lessThan(value: Long): GoogleDriveQuery = lessThan(
  QueryValue(value.toString(), false)
)

/** Convenience function pointing to [lessThan]. */
infix fun QueryOperationLessThan.lt(value: QueryValue): GoogleDriveQuery = lessThan(value)

/** Convenience function pointing to [lessThanOrEqual]. */
infix fun QueryOperationLte.lessThanOrEqual(value: Int): GoogleDriveQuery =
  lessThanOrEqual(QueryValue(value.toString(), false))

/** Convenience function pointing to [lessThanOrEqual]. */
infix fun QueryOperationLte.lessThanOrEqual(value: Float): GoogleDriveQuery =
  lessThanOrEqual(QueryValue(value.toString(), false))

/** Convenience function pointing to [lessThanOrEqual]. */
infix fun QueryOperationLte.lessThanOrEqual(value: Long): GoogleDriveQuery =
  lessThanOrEqual(QueryValue(value.toString(), false))

/** Convenience function pointing to [lessThanOrEqual]. */
infix fun QueryOperationLte.lte(value: QueryValue): GoogleDriveQuery = lessThanOrEqual(value)

/**
 * Base class for query operations that are built [term]-first. Takes a reference to the [builder]
 * that it will be added to when finalized.
 *
 * @property term The term this operation will eventually target.
 * @property builder The builder this operation will be added to when complete.
 * @constructor Create empty Query term operation base
 */
@Suppress("CovariantEquals")
open class QueryTermOperationBase(
  override val term: QueryTerm,
  override var builder: GoogleDriveQueryBuilder?
) : QueryOperation

/**
 * Defines the operations that are possible on a google drive query.
 *
 * @property term The target query term.
 * @property builder The builder that owns this operation.
 * @constructor Create empty Query term operation.
 */
open class GenericQueryTermOperation(term: QueryTerm, builder: GoogleDriveQueryBuilder?) :
  QueryOperationEquals, QueryOperationContains, QueryOperationHas, QueryOperationGreaterThan,
  QueryOperationLessThan, QueryOperationLte, QueryOperationGte,
  QueryTermOperationBase(term, builder) {

  /**
   * Creates a *CONTAINS* operation using [value] to target [term].
   */
  override infix fun contains(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.Contains, value)

  /**
   * Creates an *IN* operation (normally the [QueryValue] should be listed first for an [QueryOperator.In] operation).
   */
  override infix fun has(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.In, value)

  /** Creates the equals ( = ) operation. */
  override infix fun equals(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.Eq, value)

  /** Creates the not equal ( != ) operation. */
  override infix fun doesntEqual(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.NotEq, value)

  /** Creates the greater than ( > ) operation. */
  override infix fun greaterThan(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.Gt, value)

  /** Creates the greater than or equal ( >= ) operation. */
  override infix fun greaterThanOrEqual(value: QueryValue): GoogleDriveQuery = newQuery(
    QueryOperator.Gte,
    value
  )

  /** Creates the less than ( < ) operation. */
  override infix fun lessThan(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.Lt, value)

  /** Creates the less than or equal ( <= ) operation. */
  override infix fun lessThanOrEqual(value: QueryValue): GoogleDriveQuery = newQuery(
    QueryOperator.Lte,
    value
  )
}

/**
 * Defines the query operations that are possible on a mimeType query.
 */
class MimeTypeQueryOperation(builder: GoogleDriveQueryBuilder?) :
  QueryOperationContains, QueryOperationEquals,
  QueryTermOperationBase(
    QueryTerm("mimeType"),
    builder
  ) {
  override fun equals(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.Eq, value)

  override fun doesntEqual(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.NotEq, value)

  override fun contains(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.Contains, value)
}

/**
 * Defines the query operations that are possible on a container query.
 */
class ContainerQueryOperation(
  override val term: QueryTerm,
  builder: GoogleDriveQueryBuilder?
) :
  QueryOperationHas,
  QueryTermOperationBase(
    term,
    builder
  ) {
  override fun has(value: QueryValue): GoogleDriveQuery = newQuery(QueryOperator.In, value)
}

/**
 * DSL Marker for [GoogleDriveQueryBuilder].
 */
@DslMarker
annotation class DriveQueryDsl

/**
 * Main [GoogleDriveQuery] builder class.
 *
 * @property logging Whether this builder should log its actions.
 * @constructor Create empty Google drive query builder
 */
@DriveQueryDsl
@Suppress("TooManyFunctions")
open class GoogleDriveQueryBuilder(
  /**
   * Whether this builder should log it's operations to the stdout stream. (Debugging use only mostly.)
   */
  var logging: Boolean = false
) {
  private var query: GoogleDriveQuery? = null

  /**
   * This function adds the given query to this builders stored query/queries (implementation dependent).
   * #### This function should not be used during query building.
   */
  open fun addQuery(q: GoogleDriveQuery) {
    if (logging) logger.info { "MasterBuilder: Adding '$q'.${if (query != null) "" else "Replacing '$query'."}" }
    query = q
  }

  /**
   * Gets the query stored in this builder in [String] form. This should be called automatically.
   * #### This function should not be used during query building.
   */
  fun getQuery(): String = query.toString()

  /**
   * Gets the query stored in this builder in [GoogleDriveQuery] form. This should be called automatically.
   * #### This function should not be used during query building.
   */
  open fun asRawQuery(): GoogleDriveQuery = query ?: EmptyQuery

  /**
   * Starts a query operation that targets the "*input*" property of Drive Files.
   *
   * Example:
   * ```kotlin
   * term("parents") has value("root")
   * // creates "'root' in parents"
   * ```
   */
  fun term(input: String): GenericQueryTermOperation = GenericQueryTermOperation(QueryTerm(input), this)

  /**
   * Starts a query operation that targets the "*input*" property of Drive Files.
   *
   * Example:
   * ```kotlin
   * target("starred") equals true
   * // creates "starred = true"
   * ```
   */
  fun target(input: String): GenericQueryTermOperation = term(input)

  /**
   * Creates the user-defined target value for a query.
   *
   * Example:
   * ```kotlin
   * target("title") equals value("MyFile")
   * // creates "title = 'MyFile'"
   * ```
   */
  fun value(value: String): QueryValue = QueryValue(value, escape = false)

  /**
   * Creates and escapes the user-defined target value for a query.
   *
   * Example:
   * ```kotlin
   * target("title") equals escapedValue("My name is 'tony'")
   * // creates "title = 'My name is \'tony\''"
   * ```
   */
  fun escapedValue(value: String): QueryValue = QueryValue(value, escape = true)

  /**
   * Creates the user-defined target value for a query from a [FileTime] object.
   *
   * Example:
   * ```
   * val created = somePath.readAttributes<BasicFileAttributes>().creationTime()
   * target("createdAt") greaterThan value(created)
   * // creates "createdAt > 2019-02-11T08:35:20.318203Z"
   * ```
   */
  fun value(fileTime: FileTime?): QueryValue = QueryValue(fileTime.toString(), false)

  /**
   * Creates the user-defined target value for a query with the option to escape the value.
   *
   * Example:
   * ```kotlin
   * target("title") equals value("My name is 'tony'", true)
   * // creates "title = 'My name is \'tony\''"
   * ```
   */
  fun value(value: String, escape: Boolean): QueryValue = QueryValue(value, escape)

  /**
   * Starts a query operation that targets the **mimeType** property of Drive Files.
   */
  val mimeType: MimeTypeQueryOperation get() = MimeTypeQueryOperation(this)

  /**
   * Starts a query operation that targets the **parents** property of Drive Files.
   */
  val parents: GenericQueryTermOperation get() = GenericQueryTermOperation(QueryTerm("parents"), this)

  /**
   * Starts a query operation that targets the **title** property of Drive Files.
   */
  val title: GenericQueryTermOperation get() = GenericQueryTermOperation(QueryTerm("title"), this)

  /**
   * Starts a query operation that targets the **fullText** property of Drive Files.
   *
   * ---
   *
   * #### Note, as an implementation detail google only matches on string tokens of **fullText** queries.
   * #### This means that "HelloWorld" will match a file containing "HelloWorld" but "Hello" will not.
   */
  val fullText: GenericQueryTermOperation get() = GenericQueryTermOperation(QueryTerm("fullText"), this)

  /**
   * Convenience function to create the query **'root' in parents**.
   */
  fun isRoot(): GoogleDriveQuery = parents has value("root")

  /**
   * Convenience function to create the query **trashed = false**.
   */
  fun notTrashed(): GoogleDriveQuery = target("trashed") equals false

  /**
   * Convenience function to create the query **starred = true**.
   */
  fun isStarred(): GoogleDriveQuery = target("starred") equals true

  /**
   * ### Creates a parenthesis grouped query.
   *
   * ```kotlin
   * val req = driveService.files().list().buildQ {
   *   grouped {
   *     isStarred()
   *   }
   * }
   *
   * req.q shouldBe "(isStarred = true)"
   * ```
   * ---
   * ```kotlin
   * val req = driveService.files().list().buildQ {
   *   grouped {
   *     and {
   *       isStarred()
   *       isRoot()
   *     }
   *   }
   * }
   *
   * req.q shouldBe "(isStarred = true and 'root' in parents)"
   * ```
   */
  fun grouped(block: GoogleDriveQueryBuilder.() -> Unit) {
    addQuery(GroupedQueryBuilder().apply(block).asRawQuery())
  }

  /**
   * ### Creates an **and** group in this query string.
   *
   * ```kotlin
   * val req = driveService.files().list().buildQ {
   *   and {
   *     isStarred()
   *     isRoot()
   *   }
   * }
   *
   * req.q shouldBe "isStarred = true and 'root' in parents"
   * ```
   *
   * ### Use true for [grouped] to group the queries together with parenthesis.
   *
   * ```kotlin
   * val req = driveService.files().list().buildQ {
   *   and(true) {
   *     isStarred()
   *     isRoot()
   *   }
   * }
   *
   * req.q shouldBe "(isStarred = true and 'root' in parents)"
   * ```
   */
  fun and(grouped: Boolean = false, block: GoogleDriveQueryBuilder.() -> Unit) {
    if (grouped) {
      addQuery(
        GroupedQueryBuilder().apply {
          addQuery(AndQueryBuilder().apply(block).asRawQuery())
        }.asRawQuery()
      )
    } else {
      addQuery(AndQueryBuilder().apply(block).asRawQuery())
    }
  }

  /**
   * ### Creates an **or** group in this query string.
   *
   * ```kotlin
   * val req = driveService.files().list().buildQ {
   *   or {
   *     isStarred()
   *     isRoot()
   *   }
   * }
   *
   * req.q shouldBe "isStarred = true or 'root' in parents"
   * ```
   *
   * ### Use true for [grouped] to group the queries together with parenthesis.
   *
   * ```kotlin
   * val req = driveService.files().list().buildQ {
   *   or(true) {
   *     isStarred()
   *     isRoot()
   *   }
   * }
   *
   * req.q shouldBe "(isStarred = true or 'root' in parents)"
   * ```
   */
  fun or(grouped: Boolean = false, block: GoogleDriveQueryBuilder.() -> Unit) {
    if (grouped) {
      addQuery(
        GroupedQueryBuilder().apply {
          addQuery(OrQueryBuilder().apply(block).asRawQuery())
        }.asRawQuery()
      )
    } else {
      addQuery(OrQueryBuilder().apply(block).asRawQuery())
    }
  }

  override fun toString(): String = "MasterBuilder [${if (query != null) query.toString() else "No Query"}]"

  companion object : KLogging()
}

private class GroupedQueryBuilder : GoogleDriveQueryBuilder() {
  override fun asRawQuery(): GoogleDriveQuery = GroupedDriveQuery(super.asRawQuery())
    .also { if (logging) logger.info("GroupBuilder: Finished Query: $it") }

  override fun addQuery(q: GoogleDriveQuery) {
    if (logging) logger.info("GroupBuilder: Adding '$q'.")
    super.addQuery(q)
  }

  override fun toString(): String = "GroupBuilder [${getQuery()}]"
}

private class AndQueryBuilder : GoogleDriveQueryBuilder() {
  private val queries: MutableList<GoogleDriveQuery> = mutableListOf()

  override fun addQuery(q: GoogleDriveQuery) {
    if (logging) logger.info("AndBuilder: Adding '$q'. Current = [${queries.joinToString(" and ")}]")
    queries.add(q)
  }

  override fun asRawQuery(): GoogleDriveQuery = queries.reduce { ex, new ->
    if (logging) logger.info("Gathering And Queries. ${queries.size} queries: ${queries.joinToString(" and ")}")
    AndDriveQuery(ex, new)
  }.also { if (logging) logger.info("AndBuilder: Finished Query: $it") }

  override fun toString(): String =
    "AndBuilder [${if (queries.isNotEmpty()) queries.joinToString(", ") else "No Queries"}]"
}

private class OrQueryBuilder : GoogleDriveQueryBuilder() {
  private val queries: MutableList<GoogleDriveQuery> = mutableListOf()

  override fun addQuery(q: GoogleDriveQuery) {
    if (logging) logger.info("OrBuilder: Adding '$q'. Current = [${queries.joinToString(" or ")}]")
    queries.add(q)
  }

  override fun asRawQuery(): GoogleDriveQuery = queries.reduce { ex, new ->
    if (logging) logger.info("Gathering Or Queries. ${queries.size} queries: ${queries.joinToString(" or ")}")
    OrDriveQuery(ex, new)
  }.also { if (logging) logger.info("OrBuilder: Finished Query: $it") }

  override fun toString(): String =
    "OrBuilder [${if (queries.isNotEmpty()) queries.joinToString(", ") else "No Queries"}]"
}

/**
 * Forwards to [buildQ]. Builds the file list request's [ListRequest.getQ] property using the given builder.
 *
 * Simple example:
 * ```kotlin
 *  val request = driveService.files().list().buildQuery {
 *     and {
 *       isRoot()
 *       notTrashed()
 *     }
 *  }
 *
 * request.q shouldBe "'root' in parents and trashed = false"
 * ```
 */
fun ListRequest.buildQuery(builder: GoogleDriveQueryBuilder.() -> Unit): ListRequest = this.buildQ(builder)

/**
 * Builds the file list request's [ListRequest.getQ] property using the given builder.
 *
 * ---
 *
 * Simple example:
 * ```kotlin
 *  val request = driveService.files().list().buildQ {
 *     and {
 *       isRoot()
 *       notTrashed()
 *     }
 *  }
 *
 * request.q shouldBe "'root' in parents and trashed = false"
 * ```
 *
 * More involved example:
 * ```kotlin
 * fun hourAgo() = FileTime.from(Instant.now(Clock.systemUTC()).minus(Duration.ofHours(1)))
 * fun dayAgo() = FileTime.from(Instant.now(Clock.systemUTC()).minus(Duration.ofDays(1)))
 *
 * val request = driveService.files().list().buildQ {
 *   or {
 *     grouped {
 *       or {
 *         and {
 *           isRoot()
 *           notTrashed()
 *         }
 *         isStarred()
 *       }
 *     }
 *     and {
 *       target("modifiedAt") lessThan value(hourAgo())
 *       target("createdAt") lessThan value(dayAgo())
 *     }
 *   }
 * }
 *
 * fileListRequest.q shouldBe "(
 */
fun ListRequest.buildQ(builder: GoogleDriveQueryBuilder.() -> Unit): ListRequest {
  GoogleDriveQueryBuilder().apply(builder).also { this.q = it.getQuery() }
  return this
}
