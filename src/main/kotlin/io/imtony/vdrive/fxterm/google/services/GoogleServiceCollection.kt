package io.imtony.vdrive.fxterm.google.services

/**
 * A collection of all available Google services in a single container.
 */
interface GoogleServiceCollection {

  /**
   * The [GoogleDriveService].
   */
  val drive: GoogleDriveService

  /**
   * The [GoogleDocsService].
   */
  val docs: GoogleDocsService

  /**
   * The [GoogleSheetsService].
   */
  val sheets: GoogleSheetsService

  /**
   * The [GoogleCalendarService].
   */
  val calendar: GoogleCalendarService

  companion object {
    /**
     * Constructor that creates the bare-bones basic [GoogleServiceCollection]. It uses [ServiceInitializer.createDefault]
     * to get the default constructable [ServiceInitializer] and uses it to create [Lazy] instances of
     * all 4 Google services.
     */
    fun createDefault(
      serviceInitializer: ServiceInitializer = ServiceInitializer.createDefault()
    ): GoogleServiceCollection = GoogleServicesCollectionImpl(serviceInitializer)
  }
}

private class GoogleServicesCollectionImpl(
  private val serviceInitializer: ServiceInitializer
) :
  GoogleServiceCollection {
  override val drive: GoogleDriveService by lazy { GoogleDriveService.create(serviceInitializer) }
  override val docs: GoogleDocsService by lazy { GoogleDocsService.create(serviceInitializer) }
  override val calendar: GoogleCalendarService by lazy { GoogleCalendarService.create(serviceInitializer) }
  override val sheets: GoogleSheetsService by lazy { GoogleSheetsService.create(serviceInitializer) }
}
