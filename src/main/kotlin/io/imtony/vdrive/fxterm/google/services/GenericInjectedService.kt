package io.imtony.vdrive.fxterm.google.services

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient

interface GoogleService<TService : AbstractGoogleJsonClient> {
  val service: TService
}

abstract class GenericInjectedService<TService : AbstractGoogleJsonClient>(lazyCreator: Lazy<TService>) :
  GoogleService<TService> {
  private val service_: TService by lazyCreator

  /**
   * Direct access to the google json client service.
   */
  override val service: TService
    get() = service_
}
