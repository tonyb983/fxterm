package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.docs.v1.Docs
import com.google.api.services.docs.v1.model.*
import com.google.api.services.docs.v1.model.Request as DocsRequest

interface GoogleDocsService : GoogleService<Docs> {
  companion object {
    fun create(serviceInitializer: ServiceInitializer): GoogleDocsService = GoogleDocsServiceImpl(serviceInitializer)
  }
}

private class GoogleDocsServiceImpl(serviceCreator: ServiceInitializer) : GoogleDocsService,
  GenericInjectedService<Docs>(lazy { serviceCreator.createDocs() }) {
  fun getDocument(id: String): Document = service.documents().get(id).execute()

  fun createReplaceTextRequest(
    findText: String,
    matchCase: Boolean,
    replaceText: String
  ): DocsRequest = DocsRequest()
    .setReplaceAllText(
      ReplaceAllTextRequest()
        .setContainsText(SubstringMatchCriteria().setText(findText).setMatchCase(matchCase))
        .setReplaceText(replaceText)
    )

  fun createReplaceTextRequest(
    findText: String,
    matchCase: Boolean,
    replaceText: () -> String
  ): DocsRequest = DocsRequest()
    .setReplaceAllText(
      ReplaceAllTextRequest()
        .setContainsText(SubstringMatchCriteria().setText(findText).setMatchCase(matchCase))
        .setReplaceText(replaceText.invoke())
    )

  fun createBatchUpdateRequest(
    vararg requests: DocsRequest
  ): BatchUpdateDocumentRequest =
    BatchUpdateDocumentRequest()
      .setRequests(requests.toMutableList())

  fun executeRequests(
    docId: String,
    vararg requests: DocsRequest
  ): BatchUpdateDocumentResponse = service
    .documents()
    .batchUpdate(docId, createBatchUpdateRequest(*requests))
    .execute()

  fun executeRequests(
    docId: String,
    batch: BatchUpdateDocumentRequest
  ): BatchUpdateDocumentResponse = service
    .documents()
    .batchUpdate(docId, batch)
    .execute()
}
