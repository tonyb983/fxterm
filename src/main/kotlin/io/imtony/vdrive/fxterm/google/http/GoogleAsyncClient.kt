package io.imtony.vdrive.fxterm.google.http

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest
import com.google.api.client.http.HttpHeaders
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.HttpStatusCodes
import com.google.api.client.http.LowLevelHttpResponse
import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.client.entity.DeflateInputStream
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ContentType
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.zip.GZIPInputStream
import com.google.api.client.http.HttpResponse as GoogleHttpResponse

object GoogleAsyncClient {
  @Throws(IOException::class)
  fun <T> executeAsync(request: AbstractGoogleClientRequest<T?>, callback: FutureCallback<T?>) {
    executeAsync(request, callback, false)
  }

  @Throws(IOException::class)
  private fun <T> executeAsync(
    request: AbstractGoogleClientRequest<T?>,
    callback: FutureCallback<T?>,
    isRetry: Boolean
  ) {
    val httpResponse = request.executeUnparsed()
    val waitingForCallbackToExecuteHttpResponse: NIOHttpTransport.WaitingForCallbackToExecuteHttpResponse = try {
      val f: Field = httpResponse.javaClass.getDeclaredField("response")
      f.isAccessible = true
      if (httpResponse.request.interceptor != null) {
        httpResponse.request.interceptor.intercept(httpResponse.request)
      }
      f.get(httpResponse) as NIOHttpTransport.WaitingForCallbackToExecuteHttpResponse
    } catch (e: NoSuchFieldException) {
      throw IOException("Unable to access the private response field", e)
    } catch (e: IllegalAccessException) {
      throw IOException("Unable to access the private response value", e)
    }
    waitingForCallbackToExecuteHttpResponse.withCallback(
      ResponseCallback(
        callback,
        httpResponse.request,
        request.responseClass,
        request,
        isRetry
      )
    )
  }

  private class ResponseCallback<T>(
    private val callback: FutureCallback<T?>,
    private val request: HttpRequest,
    private val responseClass: Class<T>,
    private val originalRequest: AbstractGoogleClientRequest<T?>,
    private val isRetry: Boolean,
  ) : FutureCallback<HttpResponse> {

    override fun completed(result: HttpResponse) {
      val lowLevelHttpResponseProxy = LowLevelHttpResponseProxy(result)
      val googleHttpResponse: GoogleHttpResponse = buildGoogleHttpResponse(request, lowLevelHttpResponseProxy)
      if (!HttpStatusCodes.isSuccess(result.statusLine.statusCode)) {
        val logger = StringBuilder()
        val httpHeaders = HttpHeaders()
        try {
          httpHeaders.fromHttpResponse(lowLevelHttpResponseProxy, logger)
        } catch (io: IOException) {
          throw RuntimeException("Unable to read headers", io)
        }
        var errorHandled = false
        if (request.unsuccessfulResponseHandler != null) {
          // Even if we don't have the potential to retry, we might want to run the
          // handler to fix conditions (like expired tokens) that might cause us
          // trouble on our next request
          errorHandled = try {
            request.unsuccessfulResponseHandler.handleResponse(request, googleHttpResponse, true)
          } catch (io: IOException) {
            throw RuntimeException("Unable to handle error in request", io)
          }
        }
        if (!errorHandled && request.handleRedirect(result.statusLine.statusCode, httpHeaders)) {
          // The unsuccessful request's error could not be handled and it is a redirect request.
          errorHandled = true
        }

        // A retry is required if the error was successfully handled or if it is a redirect
        if (errorHandled) {
          if (!isRetry) {
            try {
              result.entity.content.close()
              executeAsync(originalRequest, callback, true)
              return
            } catch (e1: IOException) {
              throw RuntimeException("Unable to retry request", e1)
            }
          } else {
            callback.failed(HttpResponseException(googleHttpResponse))
          }
        }
      }

      // throw an exception if unsuccessful response
      if (!HttpStatusCodes.isSuccess(result.statusLine.statusCode)) {
        callback.failed(HttpResponseException(googleHttpResponse))
      }
      try {
        contentFromEncodingInputStream(result!!.entity.content, result.entity.contentEncoding).use { input ->
          callback.completed(
            request.parser.parseAndClose(input, ContentType.getOrDefault(result.entity).charset, responseClass)
          )
        }
      } catch (e: IOException) {
        throw RuntimeException("Unable to process response", e)
      }
    }

    @Throws(IOException::class)
    private fun contentFromEncodingInputStream(content: InputStream, contentEncoding: Header?): InputStream =
      if (contentEncoding?.value == null) {
        content
      } else {
        when (contentEncoding.value.toLowerCase()) {
          "gzip" -> GZIPInputStream(content)
          "deflate" -> DeflateInputStream(content)
          else -> content
        }
      }

    private fun buildGoogleHttpResponse(
      request: HttpRequest,
      lowLevelHttpResponse: LowLevelHttpResponse
    ): GoogleHttpResponse {
      val constructor: Constructor<*> = try {
        GoogleHttpResponse::class.java.getDeclaredConstructor(HttpRequest::class.java, LowLevelHttpResponse::class.java)
      } catch (e: NoSuchMethodException) {
        throw RuntimeException("Expecting a constructor for HttpResponse, check your version of the API client", e)
      }
      constructor.isAccessible = true
      return try {
        constructor.newInstance(request, lowLevelHttpResponse) as GoogleHttpResponse
      } catch (e: InstantiationException) {
        throw RuntimeException("Unable to build the HttpResponse", e)
      } catch (e: IllegalAccessException) {
        throw RuntimeException("Unable to build the HttpResponse", e)
      } catch (e: InvocationTargetException) {
        throw RuntimeException("Unable to build the HttpResponse", e)
      }
    }

    override fun failed(ex: Exception?) {
      callback.failed(ex)
    }

    override fun cancelled() {
      callback.cancelled()
    }

    private inner class LowLevelHttpResponseProxy(
      private val response: HttpResponse?
    ) : LowLevelHttpResponse() {

      @Throws(IOException::class)
      override fun getContent(): InputStream? = if (response?.entity == null) {
        null
      } else response.entity.content

      @Throws(IOException::class)
      override fun getContentEncoding(): String? =
        if (response?.entity == null || response.entity.contentEncoding == null) {
          null
        } else response.entity.contentEncoding.value

      @Throws(IOException::class)
      override fun getContentLength(): Long = if (response?.entity == null) {
        0
      } else response.entity.contentLength

      @Throws(IOException::class)
      override fun getContentType(): String? = if (response?.entity == null || response.entity.contentType == null) {
        null
      } else response.entity.contentType.value

      @Throws(IOException::class)
      override fun getStatusLine(): String? = if (response?.statusLine == null) {
        null
      } else {
        "${response.statusLine.protocolVersion} ${response.statusLine.statusCode}" +
          "${response.statusLine.statusCode} ${response.statusLine.reasonPhrase}"
      }

      @Throws(IOException::class)
      override fun getStatusCode(): Int = if (response?.statusLine == null) {
        -1
      } else response.statusLine.statusCode

      @Throws(IOException::class)
      override fun getReasonPhrase(): String? = if (response?.statusLine == null) {
        null
      } else response.statusLine.reasonPhrase

      @Throws(IOException::class)
      override fun getHeaderCount(): Int = response?.allHeaders?.size ?: 0

      @Throws(IOException::class)
      override fun getHeaderName(index: Int): String = response?.allHeaders?.get(index)?.name ?: ""

      @Throws(IOException::class)
      override fun getHeaderValue(index: Int): String = response?.allHeaders?.get(index)?.value ?: ""
    }
  }
}
