package io.imtony.vdrive.fxterm.google.http

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.StreamingContent;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.ContentEncoderChannel;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.Channels;

/**
 * Apache NIO implementation of Google's HTTP Transport.
 * Call {@link #shutdown() shutdown()} before closing your application is required, as this transport starts a thread.
 *
 * @see GoogleAsyncClient
 */
open class NIOHttpTransport : HttpTransport {
  private val httpClient: CloseableHttpAsyncClient

  constructor() {
    val requestConfig = RequestConfig.custom()
      .setSocketTimeout(3000)
      .setConnectTimeout(3000).build()
    httpClient = HttpAsyncClients.custom()
      .setDefaultRequestConfig(requestConfig)
      .build()
  }

  constructor(httpClient: CloseableHttpAsyncClient) {
    this.httpClient = httpClient
  }

  @Throws(IOException::class)
  override fun buildRequest(method: String, url: String): LowLevelHttpRequest {
    if (!httpClient.isRunning) {
      httpClient.start()
    }
    val requestBase: HttpRequestBase = when (method) {
      HttpMethods.DELETE -> HttpDelete(url)
      HttpMethods.GET -> HttpGet(url)
      HttpMethods.HEAD -> HttpHead(url)
      HttpMethods.POST -> HttpPost(url)
      HttpMethods.PUT -> HttpPut(url)
      HttpMethods.TRACE -> HttpTrace(url)
      HttpMethods.OPTIONS -> HttpOptions(url)
      else -> HttpExtensionMethod(method, url)
    }
    return ApacheNIOLowLevelHttpRequest(httpClient, requestBase)
  }

  /*
 * This method must be called before stopping the application, in order to shutdown the associated executor thread.
 */
  @Throws(IOException::class)
  override fun shutdown() {
    httpClient.close()
  }

  /**
   * HTTP extension method.
   *
   * @author Yaniv Inbar
   */
  private inner class HttpExtensionMethod(methodName: String?, uri: String) : HttpEntityEnclosingRequestBase() {
    /**
     * Request method name.
     */
    private val method_: String = checkNotNull(methodName)
    override fun getMethod(): String = method_

    init {
      setURI(URI.create(uri))
    }
  }

  private inner class ApacheNIOLowLevelHttpRequest(
    private val httpclient: CloseableHttpAsyncClient,
    private val request: HttpRequestBase
  ) : LowLevelHttpRequest() {

    @Throws(IOException::class)
    override fun addHeader(name: String, value: String) {
      request.addHeader(name, value)
    }

    @Throws(IOException::class)
    override fun execute(): LowLevelHttpResponse {
      var actualRequest: HttpUriRequest = request
      if (streamingContent != null) {
        Preconditions.checkArgument(
          request is HttpEntityEnclosingRequest,
          "Apache HTTP client does not support %s requests with content.",
          request.requestLine.method
        )
        try {
          actualRequest = StreamingRequest(
            request,
            streamingContent,
            contentType,
            contentLength
          ).generateRequest() as HttpUriRequest
        } catch (e: HttpException) {
          throw IOException("Unable to generate streaming request", e)
        }
      }
      return WaitingForCallbackToExecuteHttpResponse(httpclient, actualRequest)
    }
  }

  private inner class StreamingRequest(
    private val request: HttpRequestBase,
    private val streamingContent: StreamingContent,
    private val contentType: String?,
    private val contentLength: Long = 0,
  ) : HttpAsyncRequestProducer {

    override fun getTarget(): HttpHost = URIUtils.extractHost(request.uri)

    override fun isRepeatable(): Boolean = false

    @Throws(IOException::class, HttpException::class)
    override fun generateRequest(): HttpRequest {
      val entity = BasicHttpEntity()
      entity.isChunked = false
      entity.contentLength = contentLength
      if (this.contentType != null) {
        entity.setContentType(this.contentType)
      }
      (request as HttpEntityEnclosingRequest).entity = entity
      return request
    }

    @Throws(IOException::class)
    override fun produceContent(contentEncoder: ContentEncoder, ioControl: IOControl) {
      val outputStream: OutputStream = Channels.newOutputStream(ContentEncoderChannel(contentEncoder))
      streamingContent.writeTo(outputStream)
      outputStream.flush()
      contentEncoder.complete()
    }

    override fun requestCompleted(httpContext: HttpContext) {}
    override fun failed(e: Exception) {}

    @Throws(IOException::class)
    override fun resetRequest() {
    }

    @Throws(IOException::class)
    override fun close() {
    }
  }

  class WaitingForCallbackToExecuteHttpResponse(
    private val httpclient: CloseableHttpAsyncClient,
    private val actualRequest: HttpUriRequest
  ) : LowLevelHttpResponse() {

    fun withCallback(callback: FutureCallback<HttpResponse>) {
      httpclient.execute(actualRequest, callback)
    }

    override fun getContent(): InputStream = ByteArrayInputStream(byteArrayOf())

    override fun getContentEncoding(): String = "DO-NOT-USE"

    override fun getContentLength(): Long = throw IOException("Non blocking response should not be called directly")

    override fun getContentType(): String = "application/do-not-use"

    override fun getStatusLine(): String = "-1 : ASYNC REQUEST NOT STARTED YET"

    override fun getStatusCode(): Int = 200

    override fun getReasonPhrase(): String = "ASYNC REQUEST NOT STARTED YET"

    override fun getHeaderCount(): Int = 0

    @Throws(IOException::class)
    override fun getHeaderName(index: Int): String {
      throw IOException("Non blocking response should not be called directly")
    }

    @Throws(IOException::class)
    override fun getHeaderValue(index: Int): String {
      throw IOException("Non blocking response should not be called directly")
    }
  }
}
