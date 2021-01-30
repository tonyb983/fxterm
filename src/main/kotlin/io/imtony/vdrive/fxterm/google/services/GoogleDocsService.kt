@file:Suppress("unused", "BlockingMethodInNonBlockingContext")

package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.docs.v1.Docs
import com.google.api.services.docs.v1.model.*
import dispatch.core.DispatcherProvider
import io.imtony.vdrive.fxterm.google.ext.coExecute
import io.imtony.vdrive.fxterm.google.ext.executeAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext
import com.google.api.services.docs.v1.model.Request as DocsRequest

/**
 * The Google [Docs] service interface.
 */
interface GoogleDocsService : GoogleService<Docs> {
  companion object {

    /**
     * Constructor function for the [GoogleDocsService] interface.
     */
    fun create(serviceInitializer: ServiceInitializer): GoogleDocsService = GoogleDocsServiceImpl(serviceInitializer)
  }
}

/**
 * Gets the document with the given [id].
 *
 * @see getDocumentCoroutine
 * @see getDocumentAsync
 */
fun GoogleDocsService.getDocument(
  id: String,
): Document? = service.documents().get(id).execute()

/**
 * Gets the document with the given [id] using [coExecute].
 *
 * @see getDocument
 * @see getDocumentAsync
 */
suspend fun GoogleDocsService.getDocumentCoroutine(
  id: String,
  context: CoroutineContext = DispatcherProvider().io
): Document? = service.documents().get(id).coExecute(context)

/**
 * Gets the document with the given [id] using [coExecute].
 *
 * @see getDocument
 * @see getDocumentAsync
 */
suspend fun GoogleDocsService.getDocumentCoroutine(
  id: String,
  scope: CoroutineScope
): Document? = service.documents().get(id).coExecute(scope)

/**
 * Gets the document with the given [id] using [executeAsync].
 *
 * @see getDocumentCoroutine
 * @see getDocument
 */
fun GoogleDocsService.getDocumentAsync(
  id: String,
  context: CoroutineContext = DispatcherProvider().io
): Deferred<Document?> = service.documents().get(id).executeAsync(context)

/**
 * Gets the document with the given [id] using [executeAsync].
 *
 * @see getDocumentCoroutine
 * @see getDocument
 */
fun GoogleDocsService.getDocumentAsync(
  id: String,
  scope: CoroutineScope
): Deferred<Document?> = service.documents().get(id).executeAsync(scope)

/**
 * Create a new [DocsRequest] containing a [ReplaceAllTextRequest] set to replace [findText] with [replaceText].
 *
 * @see createReplaceTextRequestLazy
 */
fun GoogleDocsService.createReplaceTextRequest(
  findText: String,
  matchCase: Boolean,
  replaceText: String
): DocsRequest = DocsRequest()
  .setReplaceAllText(
    ReplaceAllTextRequest()
      .setContainsText(SubstringMatchCriteria().setText(findText).setMatchCase(matchCase))
      .setReplaceText(replaceText)
  )

/**
 * Create a new [DocsRequest] containing a [ReplaceAllTextRequest] set to replace [findText]
 * with the result of calling [replaceText].
 *
 * @see createReplaceTextRequestLazy
 */
fun GoogleDocsService.createReplaceTextRequest(
  findText: String,
  matchCase: Boolean,
  replaceText: () -> String
): DocsRequest = DocsRequest()
  .setReplaceAllText(
    ReplaceAllTextRequest()
      .setContainsText(SubstringMatchCriteria().setText(findText).setMatchCase(matchCase))
      .setReplaceText(replaceText.invoke())
  )

/**
 * Create a new [DocsRequest] containing a [ReplaceAllTextRequest] set to replace [findText]
 * with the result of calling [replaceText].
 */
fun GoogleDocsService.createReplaceTextRequestLazy(
  findText: String,
  matchCase: Boolean,
  replaceText: () -> String
): Lazy<DocsRequest> = lazy {
  DocsRequest()
    .setReplaceAllText(
      ReplaceAllTextRequest()
        .setContainsText(SubstringMatchCriteria().setText(findText).setMatchCase(matchCase))
        .setReplaceText(replaceText.invoke())
    )
}

/**
 * Creates a [BatchUpdateDocumentRequest] containing the given [requests].
 */
fun GoogleDocsService.createBatchUpdateRequest(
  vararg requests: DocsRequest
): BatchUpdateDocumentRequest =
  BatchUpdateDocumentRequest()
    .setRequests(requests.toMutableList())

/**
 * Wraps the given [requests] into a [BatchUpdateDocumentRequest] and executes them targeting the given [docId]. The
 * resulting [BatchUpdateDocumentResponse] is returned.
 */
fun GoogleDocsService.executeRequests(
  docId: String,
  vararg requests: DocsRequest
): BatchUpdateDocumentResponse = service
  .documents()
  .batchUpdate(docId, createBatchUpdateRequest(*requests))
  .execute()

/**
 * Executes the given [batch] targeting the given [docId]. The resulting [BatchUpdateDocumentResponse] is returned.
 */
fun GoogleDocsService.executeRequests(
  docId: String,
  batch: BatchUpdateDocumentRequest
): BatchUpdateDocumentResponse = service
  .documents()
  .batchUpdate(docId, batch)
  .execute()

private class GoogleDocsServiceImpl(serviceCreator: ServiceInitializer) : GoogleDocsService,
  GenericService<Docs>(lazy { serviceCreator.createDocs() })
