package io.imtony.vdrive.fxterm.google.services

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient

/**
 * Base interface for all Google Services.
 */
interface GoogleService<TService : AbstractGoogleJsonClient> {
  /**
   * The actual service.
   */
  val service: TService
}

/**
 * Base class for all Google Service wrappers.
 */
abstract class GenericService<TService : AbstractGoogleJsonClient>(
  lazyCreator: Lazy<TService>
) :
  GoogleService<TService> {

  private val service_: TService by lazyCreator

  /**
   * Direct access to the google json client service.
   */
  override val service: TService
    get() = service_
}
