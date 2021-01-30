package io.imtony.vdrive.fxterm.google.services

interface GoogleServiceCollection {
  val drive: GoogleDriveService
  val docs: GoogleDocsService
  val sheets: GoogleSheetsService
  val calendar: GoogleCalendarService

  companion object {
    fun createDefault(
      serviceInitializer: ServiceInitializer = ServiceInitializer.createDefault()
    ): GoogleServiceCollection = GoogleServicesCollectionImpl(serviceInitializer)
  }
}

private class GoogleServicesCollectionImpl(private val serviceInitializer: ServiceInitializer) :
  GoogleServiceCollection {
  override val drive: GoogleDriveService by lazy { GoogleDriveService.create(serviceInitializer) }
  override val docs: GoogleDocsService by lazy { GoogleDocsService.create(serviceInitializer) }
  override val calendar: GoogleCalendarService by lazy { GoogleCalendarService.create(serviceInitializer) }
  override val sheets: GoogleSheetsService by lazy { GoogleSheetsService.create(serviceInitializer) }
}
