@file:Suppress("unused")

package io.imtony.vdrive.fxterm.google.ext

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest
import dispatch.core.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Execute this request inside [withContext] using [DispatcherProvider.io] as the default context.
 */
suspend fun <T> AbstractGoogleClientRequest<T>.coExecute(
  context: CoroutineContext = DispatcherProvider().io
): T = withContext(context) { execute() }

/**
 * Execute this request inside [withContext] using the given coroutine [scope].
 */
suspend fun <T> AbstractGoogleClientRequest<T>.coExecute(
  scope: CoroutineScope
): T = withContext(scope.coroutineContext) { execute() }


/**
 * Execute this request using [CoroutineScope.async] and return the [Deferred] result.
 */
fun <T> AbstractGoogleClientRequest<T>.executeAsync(
  scope: CoroutineScope
): Deferred<T> = scope.async { execute() }

/**
 * Execute this request using [CoroutineScope][CoroutineScope] ([context]).[async] and return the [Deferred] result.
 */
fun <T> AbstractGoogleClientRequest<T>.executeAsync(
  context: CoroutineContext = DispatcherProvider().io
): Deferred<T> = CoroutineScope(context).async { execute() }
